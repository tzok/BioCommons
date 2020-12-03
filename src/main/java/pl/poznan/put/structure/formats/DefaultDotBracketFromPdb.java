package pl.poznan.put.structure.formats;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.DotBracketSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A default implementation of a dot-bracket structure which is mapped to data from 3D coordinates.
 */
@Value.Immutable
public abstract class DefaultDotBracketFromPdb extends AbstractDotBracket
    implements DotBracketFromPdb {
  private static void depthFirstSearch(
      final Strand u,
      final Map<Strand, Set<Strand>> graph,
      final Set<Strand> visited,
      final Set<Strand> component) {
    visited.add(u);
    component.add(u);

    for (final Strand v : graph.getOrDefault(u, Collections.emptySet())) {
      if (!visited.contains(v)) {
        DefaultDotBracketFromPdb.depthFirstSearch(v, graph, visited, component);
      }
    }
  }

  @Value.Parameter(order = 3)
  public abstract PdbModel model();

  @Override
  public final List<DotBracket> combineStrands() {
    return candidatesToCombine().stream()
        .map(strands -> ImmutableCombinedStrandFromPdb.of(strands, symbolToResidue()))
        .collect(Collectors.toList());
  }

  @Override
  public final int originalIndex(final DotBracketSymbol symbol) {
    return symbolToResidue().get(symbol).residueNumber();
  }

  @Override
  @Value.Default
  @Value.Auxiliary
  public List<Strand> strands() {
    final List<Strand> strands = new ArrayList<>();
    int start = 0;
    int end = 0;

    for (final PdbChain chain : model().chains()) {
      end += chain.residues().size();
      strands.add(ImmutableStrandView.of(chain.identifier(), this, start, end));
      start = end;
    }

    return strands;
  }

  @Override
  @Value.Parameter(order = 1)
  public abstract String sequence();

  @Override
  @Value.Parameter(order = 2)
  public abstract String structure();

  @Override
  @Value.Lazy
  @Value.Auxiliary
  public Map<DotBracketSymbol, DotBracketSymbol> pairs() {
    return super.pairs();
  }

  @Override
  public final PdbNamedResidueIdentifier identifier(final DotBracketSymbol symbol) {
    return symbolToResidue().get(symbol);
  }

  @Override
  public final DotBracketSymbol symbol(final PdbNamedResidueIdentifier residueIdentifier) {
    return residueToSymbol().get(residueIdentifier);
  }

  @Override
  public final boolean contains(final PdbNamedResidueIdentifier residueIdentifier) {
    return residueToSymbol().containsKey(residueIdentifier);
  }

  @Override
  public final List<DotBracketFromPdb> combineStrands(final List<ClassifiedBasePair> nonCanonical) {
    // map containing links between strands
    final Map<Strand, Set<Strand>> strandMap = new LinkedHashMap<>();

    // link strands connected by canonical base pairs
    strands()
        .forEach(
            strand ->
                pairs().keySet().stream()
                    .filter(symbol -> strand.symbols().contains(symbol))
                    .forEach(symbol -> linkStrands(strand, pairs().get(symbol), strandMap)));

    // link strands connected by non-canonical base pairs
    for (final ClassifiedBasePair nonCanonicalPair : nonCanonical) {
      final DotBracketSymbol leftSymbol = residueToSymbol().get(nonCanonicalPair.basePair().left());
      final DotBracketSymbol rightSymbol =
          residueToSymbol().get(nonCanonicalPair.basePair().right());

      for (final Strand strand : strands()) {
        final List<DotBracketSymbol> strandSymbols = strand.symbols();
        if (strandSymbols.contains(leftSymbol) && !strandSymbols.contains(rightSymbol)) {
          linkStrands(strand, rightSymbol, strandMap);
        }
      }
    }

    // find all connected components
    final Set<Strand> visited = new HashSet<>(strands().size());
    final Collection<Set<Strand>> components = new ArrayList<>();

    for (final Strand strand : strands()) {
      if (!visited.contains(strand)) {
        final Set<Strand> component = new HashSet<>();
        DefaultDotBracketFromPdb.depthFirstSearch(strand, strandMap, visited, component);
        components.add(component);
      }
    }

    // prepare the final result
    final List<DotBracketFromPdb> result = new ArrayList<>(components.size());
    for (final Set<Strand> strandCluster : components) {
      final List<Strand> combinedStrands = new ArrayList<>(strandCluster);
      combinedStrands.sort(Comparator.comparingInt(strands()::indexOf));
      result.add(ImmutableCombinedStrandFromPdb.of(combinedStrands, symbolToResidue()));
    }

    return result;
  }

  @Value.Lazy
  protected Map<PdbNamedResidueIdentifier, DotBracketSymbol> residueToSymbol() {
    return IntStream.range(0, symbols().size())
        .boxed()
        .collect(
            Collectors.toMap(
                i -> model().residues().get(i).namedResidueIdentifer(), i -> symbols().get(i)));
  }

  @Value.Lazy
  protected Map<DotBracketSymbol, PdbNamedResidueIdentifier> symbolToResidue() {
    return IntStream.range(0, symbols().size())
        .boxed()
        .collect(
            Collectors.toMap(
                i -> symbols().get(i), i -> model().residues().get(i).namedResidueIdentifer()));
  }

  @Value.Check
  protected DefaultDotBracketFromPdb validate() {
    Validate.matchesPattern(sequence(), DefaultDotBracket.SEQUENCE_PATTERN);
    Validate.matchesPattern(structure(), DefaultDotBracket.STRUCTURE_PATTERN);

    Validate.isTrue(
        sequence().length() == structure().length(),
        "Sequence and structure must be of the same length");

    final List<Integer> positionsToModify =
        IntStream.range(0, model().residues().size())
            .filter(i -> model().residues().get(i).isMissing() && structure().charAt(i) != '-')
            .boxed()
            .collect(Collectors.toList());
    if (positionsToModify.isEmpty()) {
      return this;
    }

    final char[] chars = structure().toCharArray();
    positionsToModify.forEach(i -> chars[i] = '-');

    return ImmutableDefaultDotBracketFromPdb.of(sequence(), String.valueOf(chars), model());
  }

  private void linkStrands(
      final Strand firstStrand,
      final DotBracketSymbol symbolInSecondStrand,
      final Map<Strand, Set<Strand>> strandMap) {
    for (final Strand secondStrand : strands()) {
      if (!secondStrand.equals(firstStrand)
          && secondStrand.symbols().contains(symbolInSecondStrand)) {
        if (!strandMap.containsKey(firstStrand)) {
          strandMap.put(firstStrand, new LinkedHashSet<>());
        }
        strandMap.get(firstStrand).add(secondStrand);
        if (!strandMap.containsKey(secondStrand)) {
          strandMap.put(secondStrand, new LinkedHashSet<>());
        }
        strandMap.get(secondStrand).add(firstStrand);
        return;
      }
    }
  }
}
