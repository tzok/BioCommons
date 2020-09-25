package pl.poznan.put.structure.formats;

import org.immutables.value.Value;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ModifiableAnalyzedBasePair;
import pl.poznan.put.structure.ModifiableDotBracketSymbol;

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

@Value.Immutable
public abstract class DefaultDotBracketFromPdb implements DotBracketFromPdb {
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

  @Value.Parameter(order = 1)
  public abstract DefaultDotBracket dotBracket();

  @Value.Parameter(order = 2)
  public abstract PdbModel model();

  @Override
  public final String sequence() {
    return dotBracket().sequence();
  }

  @Override
  public final String structure() {
    return dotBracket().structure();
  }

  @Override
  public final List<DotBracketSymbol> symbols() {
    return dotBracket().symbols();
  }

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
  public final List<DotBracket> combineStrands() {
    return DefaultDotBracket.candidatesToCombine(strands()).stream()
        .map(
            strands ->
                ImmutableCombinedStrandFromPdb.of(
                    ImmutableCombinedStrand.of(strands), symbolToResidue()))
        .collect(Collectors.toList());
  }

  @Override
  public final int getRealSymbolIndex(final DotBracketSymbol symbol) {
    return symbolToResidue().get(symbol).residueNumber();
  }

  @Override
  public final PdbNamedResidueIdentifier getResidueIdentifier(final DotBracketSymbol symbol) {
    return symbolToResidue().get(symbol);
  }

  @Override
  public final DotBracketSymbol getSymbol(final PdbNamedResidueIdentifier residueIdentifier) {
    return residueToSymbol().get(residueIdentifier);
  }

  @Override
  public final boolean contains(final PdbNamedResidueIdentifier residueIdentifier) {
    return residueToSymbol().containsKey(residueIdentifier);
  }

  @Override
  public final List<CombinedStrandFromPdb> combineStrands(
      final List<ClassifiedBasePair> availableNonCanonical) {
    // map containing links between strands
    final Map<Strand, Set<Strand>> strandMap = new LinkedHashMap<>();

    // link strands connected by canonical base pairs
    for (final Strand strand : strands()) {
      final List<DotBracketSymbol> strandSymbols = strand.symbols();
      for (final DotBracketSymbol symbol : strandSymbols) {
        if (symbol.isPairing()) {
          final DotBracketSymbol pair =
              symbol
                  .pair()
                  .orElseThrow(
                      () ->
                          new IllegalArgumentException(
                              "Failed to find a pair for seemingly pairing dot-bracket symbol: "
                                  + symbol));
          if (!strandSymbols.contains(pair)) {
            linkStrands(strand, pair, strandMap);
          }
        }
      }
    }

    // link strands connected by non-canonical base pairs
    for (final ClassifiedBasePair nonCanonicalPair : availableNonCanonical) {
      final DotBracketSymbol leftSymbol =
          residueToSymbol().get(nonCanonicalPair.basePair().getLeft());
      final DotBracketSymbol rightSymbol =
          residueToSymbol().get(nonCanonicalPair.basePair().getRight());

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
    final List<CombinedStrandFromPdb> result = new ArrayList<>(components.size());
    for (final Set<Strand> strandCluster : components) {
      final List<Strand> combinedStrands = new ArrayList<>(strandCluster);
      combinedStrands.sort(Comparator.comparingInt(strands()::indexOf));
      result.add(
          ImmutableCombinedStrandFromPdb.of(
              ImmutableCombinedStrand.of(combinedStrands), symbolToResidue()));
    }

    return result;
  }

  public final Map<DotBracketSymbol, PdbNamedResidueIdentifier> getSymbolToResidue() {
    return Collections.unmodifiableMap(symbolToResidue());
  }

  public final Map<PdbNamedResidueIdentifier, DotBracketSymbol> getResidueToSymbol() {
    return Collections.unmodifiableMap(residueToSymbol());
  }

  public final BasePair basePair(final DotBracketSymbol symbol) {
    if (symbol.isPairing()) {
      return new BasePair(
          symbolToResidue().get(symbol),
          symbolToResidue()
              .get(
                  symbol
                      .pair()
                      .orElseThrow(
                          () ->
                              new IllegalArgumentException(
                                  "Failed to find a pair for seemingly pairing dot-bracket symbol: "
                                      + symbol))));
    }
    throw new IllegalArgumentException(
        "Cannot create base pair from unpaired nucleotide: " + symbol);
  }

  @Value.Lazy
  public Map<DotBracketSymbol, PdbNamedResidueIdentifier> symbolToResidue() {
    return IntStream.range(0, symbols().size())
        .boxed()
        .collect(
            Collectors.toMap(
                i -> symbols().get(i), i -> model().residues().get(i).namedResidueIdentifer()));
  }

  @Value.Lazy
  public Map<PdbNamedResidueIdentifier, DotBracketSymbol> residueToSymbol() {
    return IntStream.range(0, symbols().size())
        .boxed()
        .collect(
            Collectors.toMap(
                i -> model().residues().get(i).namedResidueIdentifer(), i -> symbols().get(i)));
  }

  public final void markRepresentedNonCanonicals(
      final Iterable<ModifiableAnalyzedBasePair> nonCanonical) {
    final Collection<BasePair> representedSet = new HashSet<>();

    for (final DotBracketSymbol symbol : symbols()) {
      if (symbol.isPairing()) {
        final PdbNamedResidueIdentifier left = getResidueIdentifier(symbol);
        final PdbNamedResidueIdentifier right =
            getResidueIdentifier(
                symbol
                    .pair()
                    .orElseThrow(
                        () ->
                            new IllegalArgumentException(
                                "Failed to find a pair for seemingly pairing dot-bracket symbol: "
                                    + symbol)));
        representedSet.add(new BasePair(left, right));
      }
    }

    for (final ModifiableAnalyzedBasePair cbp : nonCanonical) {
      final BasePair basePair = cbp.basePair();
      if (representedSet.contains(basePair)) {
        cbp.setIsRepresented(true);

        if (!cbp.isCanonical()) {
          final ModifiableDotBracketSymbol left =
              (ModifiableDotBracketSymbol) getSymbol(basePair.getLeft());
          final ModifiableDotBracketSymbol right =
              (ModifiableDotBracketSymbol) getSymbol(basePair.getRight());
          left.setIsNonCanonical(true);
          right.setIsNonCanonical(true);
        }
      }
    }
  }

  @Value.Check
  protected DefaultDotBracketFromPdb useMinusForMissingResidues() {
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

    return ImmutableDefaultDotBracketFromPdb.of(
        ImmutableDefaultDotBracket.of(sequence(), String.valueOf(chars)), model());
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
