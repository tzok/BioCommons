package pl.poznan.put.rna;

import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.commons.lang3.tuple.Pair;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.ImmutableDefaultPdbModel;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.structure.CanonicalStructureExtractor;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.LevelByLevelConverter;
import pl.poznan.put.structure.pseudoknots.elimination.MinGain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A set of methods to reorder chains in an RNA structure. The order is derived from base pairing
 * information in two steps: (1) a graph of connection is traversed to find connected components
 * which are processed together, and (2) all permutations of each component's order are analyzed to
 * find one which minimizes the pseudoknot order.
 */
public final class ChainReorderer {
  private ChainReorderer() {
    super();
  }

  /**
   * Finds canonical base pairs (see {@link
   * CanonicalStructureExtractor#basePairs(ResidueCollection)}) and reorders chains to keep
   * connected ones together while minimizing the overall pseudoknot order.
   *
   * @param model The input PDB model.
   * @return The PDB model filtered to contain only RNA and with chains reordered.
   */
  public static PdbModel reorderAtoms(final PdbModel model) {
    final PdbModel rna = model.filteredNewInstance(MoleculeType.RNA);
    return ChainReorderer.reorderAtoms(rna, CanonicalStructureExtractor.basePairs(rna));
  }

  /**
   * Reorders chains according to given canonical base pairs to keep connected chains together while
   * minimizing the overall pseudoknot order.
   *
   * @param model The input PDB model.
   * @param basePairs The list of base pairs to take into account.
   * @return The PDB model filtered to contain only RNA and with chains reordered.
   */
  public static PdbModel reorderAtoms(
      final PdbModel model, final Collection<? extends ClassifiedBasePair> basePairs) {
    final PdbModel rna = model.filteredNewInstance(MoleculeType.RNA);
    final List<String> order = ChainReorderer.chainOrder(rna.namedResidueIdentifiers(), basePairs);
    final List<PdbAtomLine> atoms =
        rna.atoms().stream()
            .sorted(Comparator.comparingInt(t -> order.indexOf(t.chainIdentifier())))
            .collect(Collectors.toList());
    return ImmutableDefaultPdbModel.of(
        rna.header(),
        rna.experimentalData(),
        rna.resolution(),
        rna.modelNumber(),
        atoms,
        rna.modifiedResidues(),
        rna.missingResidues(),
        rna.title(),
        rna.chainTerminatedAfter());
  }

  private static List<String> chainOrder(
      final Collection<PdbNamedResidueIdentifier> residues,
      final Collection<? extends ClassifiedBasePair> basePairs) {
    final List<String> distinct =
        residues.stream()
            .map(PdbNamedResidueIdentifier::chainIdentifier)
            .distinct()
            .collect(Collectors.toList());

    // 1 or 2 chains do not reordering
    if (distinct.size() <= 2) {
      return distinct;
    }

    final Map<String, Set<String>> graph = ChainReorderer.buildGraph(basePairs);
    final Set<String> visited = new HashSet<>();
    final List<String> order = new ArrayList<>();

    for (final String chain : distinct) {
      if (!visited.contains(chain)) {
        final Set<String> component = new LinkedHashSet<>();
        ChainReorderer.depthFirstSearch(chain, graph, visited, component);
        order.addAll(ChainReorderer.componentOrder(component, residues, basePairs));
      }
    }

    return order;
  }

  private static List<String> componentOrder(
      final Set<String> component,
      final Collection<PdbNamedResidueIdentifier> residues,
      final Collection<? extends ClassifiedBasePair> basePairs) {
    final Iterator<List<String>> iterator = new PermutationIterator<>(component);
    final Iterable<List<String>> iterable = () -> iterator;
    return StreamSupport.stream(iterable.spliterator(), false)
        .min(
            Comparator.comparingInt(
                order -> ChainReorderer.countPseudoknots(order, residues, basePairs)))
        .orElse(new ArrayList<>(component));
  }

  private static int countPseudoknots(
      final List<String> candidateOrder,
      final Collection<PdbNamedResidueIdentifier> residues,
      final Collection<? extends ClassifiedBasePair> basePairs) {
    final List<PdbNamedResidueIdentifier> reordered =
        residues.stream()
            .filter(identifier -> candidateOrder.contains(identifier.chainIdentifier()))
            .sorted(Comparator.comparingInt(t -> candidateOrder.indexOf(t.chainIdentifier())))
            .collect(Collectors.toList());
    final List<? extends ClassifiedBasePair> filteredBasePairs =
        basePairs.stream()
            .filter(
                basePair ->
                    candidateOrder.contains(basePair.basePair().getLeft().chainIdentifier()))
            .filter(
                basePair ->
                    candidateOrder.contains(basePair.basePair().getRight().chainIdentifier()))
            .collect(Collectors.toList());
    final BpSeq bpSeq = BpSeq.fromResidueCollection(reordered, filteredBasePairs);
    final Converter converter = new LevelByLevelConverter(new MinGain(), 1);
    return converter.convert(bpSeq).pseudoknotOrder();
  }

  private static Map<String, Set<String>> buildGraph(
      final Collection<? extends ClassifiedBasePair> basePairs) {
    return basePairs.stream()
        .map(ClassifiedBasePair::basePair)
        .map(
            basePair ->
                Pair.of(
                    basePair.getLeft().chainIdentifier(), basePair.getRight().chainIdentifier()))
        .filter(pair -> !pair.getLeft().equals(pair.getRight()))
        .flatMap(pair -> Stream.of(pair, Pair.of(pair.getRight(), pair.getLeft())))
        .distinct()
        .collect(
            Collectors.toMap(
                Pair::getLeft,
                pair -> Collections.singleton(pair.getRight()),
                (s1, s2) ->
                    new LinkedHashSet<>(
                        Stream.concat(s1.stream(), s2.stream()).collect(Collectors.toList()))));
  }

  private static void depthFirstSearch(
      final String u,
      final Map<String, Set<String>> graph,
      final Set<String> visited,
      final Set<String> component) {
    visited.add(u);
    component.add(u);

    for (final String v : graph.getOrDefault(u, Collections.emptySet())) {
      if (!visited.contains(v)) {
        ChainReorderer.depthFirstSearch(v, graph, visited, component);
      }
    }
  }
}
