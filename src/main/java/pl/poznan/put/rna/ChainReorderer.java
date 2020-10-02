package pl.poznan.put.rna;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
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
import pl.poznan.put.structure.formats.ImmutableDefaultConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A set of methods to reorder chains in an RNA structure. The order is derived from base pairing
 * information in two steps: (1) a graph of connection is traversed to find connected components
 * which are processed together, and (2) all permutations of each component's order are analyzed to
 * find one which minimizes the pseudoknot order.
 */
public final class ChainReorderer {
  private static final SpearmansCorrelation SPEARMAN = new SpearmansCorrelation();

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
    final Collection<String> visited = new HashSet<>();
    final List<String> order = new ArrayList<>();

    for (final String chain : distinct) {
      if (!visited.contains(chain)) {
        final List<String> component = new ArrayList<>();
        ChainReorderer.depthFirstSearch(chain, graph, visited, component);
        order.addAll(ChainReorderer.componentOrder(component, distinct, residues, basePairs));
      }
    }

    return order;
  }

  private static List<String> componentOrder(
      final List<String> component,
      final List<String> originalChainOrder,
      final Collection<PdbNamedResidueIdentifier> residues,
      final Collection<? extends ClassifiedBasePair> basePairs) {
    // gather all permutations of chains which have the minimal pseudoknot order
    final SortedMap<Integer, List<List<String>>> map = new TreeMap<>();
    CollectionUtils.permutations(component)
        .forEach(
            order -> {
              final int pseudoknots = ChainReorderer.countPseudoknots(order, residues, basePairs);
              map.putIfAbsent(pseudoknots, new ArrayList<>());
              map.get(pseudoknots).add(order);
            });

    // return one of the selected permutations which is the most similar to the input chain order
    // (i.e. introduce the minimal number of changes).
    final double[] yArray =
        IntStream.range(0, originalChainOrder.size())
            .filter(i -> component.contains(originalChainOrder.get(i)))
            .mapToDouble(i -> i)
            .toArray();

    return map.get(map.firstKey()).stream()
        .map(
            candidate ->
                Pair.of(
                    candidate,
                    ChainReorderer.spearmanCorrelation(originalChainOrder, yArray, candidate)))
        .max(Comparator.comparingDouble(Pair::getValue))
        .map(Pair::getKey)
        .orElse(component);
  }

  private static double spearmanCorrelation(
      final List<String> originalChainOrder,
      final double[] yArray,
      final Collection<String> candidate) {
    final double[] xArray =
        candidate.stream().map(originalChainOrder::indexOf).mapToDouble(i -> i).toArray();
    return ChainReorderer.SPEARMAN.correlation(xArray, yArray);
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
                basePair -> candidateOrder.contains(basePair.basePair().left().chainIdentifier()))
            .filter(
                basePair -> candidateOrder.contains(basePair.basePair().right().chainIdentifier()))
            .collect(Collectors.toList());
    final BpSeq bpSeq = BpSeq.fromBasePairs(reordered, filteredBasePairs);
    final Converter converter = ImmutableDefaultConverter.of();
    return converter.convert(bpSeq).pseudoknotOrder();
  }

  private static Map<String, Set<String>> buildGraph(
      final Collection<? extends ClassifiedBasePair> basePairs) {
    final Map<String, Set<String>> map = new HashMap<>();
    basePairs.stream()
        .map(ClassifiedBasePair::basePair)
        .flatMap(basePair -> Stream.of(basePair, basePair.invert()))
        .map(
            basePair ->
                Pair.of(basePair.left().chainIdentifier(), basePair.right().chainIdentifier()))
        .filter(pair -> !pair.getLeft().equals(pair.getRight()))
        .distinct()
        .forEach(
            pair -> {
              map.putIfAbsent(pair.getLeft(), new HashSet<>());
              map.get(pair.getLeft()).add(pair.getRight());
            });
    return map;
  }

  private static void depthFirstSearch(
      final String u,
      final Map<String, Set<String>> graph,
      final Collection<String> visited,
      final Collection<String> component) {
    visited.add(u);
    component.add(u);

    for (final String v : graph.getOrDefault(u, Collections.emptySet())) {
      if (!visited.contains(v)) {
        ChainReorderer.depthFirstSearch(v, graph, visited, component);
      }
    }
  }
}
