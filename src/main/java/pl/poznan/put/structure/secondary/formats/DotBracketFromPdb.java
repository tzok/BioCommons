package pl.poznan.put.structure.secondary.formats;

import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.pdb.analysis.StructureModel;
import pl.poznan.put.structure.secondary.BasePair;
import pl.poznan.put.structure.secondary.ClassifiedBasePair;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DotBracketFromPdb extends DotBracket implements DotBracketFromPdbInterface {
  private final Map<DotBracketSymbol, PdbNamedResidueIdentifier> symbolToResidue = new HashMap<>();
  private final Map<PdbNamedResidueIdentifier, DotBracketSymbol> residueToSymbol = new HashMap<>();

  public DotBracketFromPdb(
      final DotBracketInterface dotBracket,
      final StructureModel model,
      final Iterable<? extends ClassifiedBasePair> nonCanonical) {
    this(dotBracket.getSequence(), dotBracket.getStructure(), model);
    markRepresentedNonCanonicals(nonCanonical);
  }

  public DotBracketFromPdb(
      final String sequence, final String structure, final StructureModel model) {
    super(sequence, DotBracketFromPdb.updateMissingIndices(structure, model));

    mapSymbolsAndResidues(model);
    splitStrands(model);
  }

  private static String updateMissingIndices(
      final String structure, final ResidueCollection model) {
    final List<PdbResidue> residues = model.residues();
    final char[] dotBracket = structure.toCharArray();
    assert dotBracket.length == residues.size();

    for (int i = 0; i < dotBracket.length; i++) {
      if (residues.get(i).isMissing()) {
        dotBracket[i] = '-';
      }
    }

    return String.valueOf(dotBracket);
  }

  private static void depthFirstSearch(
      final Strand u,
      final Map<Strand, Set<Strand>> graph,
      final Set<Strand> visited,
      final Set<Strand> component) {
    visited.add(u);
    component.add(u);

    for (final Strand v : graph.getOrDefault(u, Collections.emptySet())) {
      if (!visited.contains(v)) {
        DotBracketFromPdb.depthFirstSearch(v, graph, visited, component);
      }
    }
  }

  @Override
  public final PdbNamedResidueIdentifier getResidueIdentifier(final DotBracketSymbol symbol) {
    return symbolToResidue.get(symbol);
  }

  @Override
  public final DotBracketSymbol getSymbol(final PdbNamedResidueIdentifier residueIdentifier) {
    return residueToSymbol.get(residueIdentifier);
  }

  @Override
  public final boolean contains(final PdbNamedResidueIdentifier residueIdentifier) {
    return residueToSymbol.containsKey(residueIdentifier);
  }

  @Override
  public final List<CombinedStrandFromPdb> combineStrands(
      final List<? extends ClassifiedBasePair> availableNonCanonical) {
    // map containing links between strands
    final Map<Strand, Set<Strand>> strandMap = new LinkedHashMap<>();

    // link strands connected by canonical base pairs
    for (final Strand strand : strands) {
      final List<DotBracketSymbol> strandSymbols = strand.getSymbols();
      for (final DotBracketSymbol symbol : strandSymbols) {
        if (symbol.isPairing() && !strandSymbols.contains(symbol.getPair())) {
          linkStrands(strand, symbol.getPair(), strandMap);
        }
      }
    }

    // link strands connected by non-canonical base pairs
    for (final ClassifiedBasePair nonCanonicalPair : availableNonCanonical) {
      final DotBracketSymbol leftSymbol =
          residueToSymbol.get(nonCanonicalPair.getBasePair().getLeft());
      final DotBracketSymbol rightSymbol =
          residueToSymbol.get(nonCanonicalPair.getBasePair().getRight());

      for (final Strand strand : strands) {
        final List<DotBracketSymbol> strandSymbols = strand.getSymbols();
        if (strandSymbols.contains(leftSymbol) && !strandSymbols.contains(rightSymbol)) {
          linkStrands(strand, rightSymbol, strandMap);
        }
      }
    }

    // find all connected components
    final Set<Strand> visited = new HashSet<>(strands.size());
    final Collection<Set<Strand>> components = new ArrayList<>();

    for (final Strand strand : strands) {
      if (!visited.contains(strand)) {
        final Set<Strand> component = new HashSet<>();
        DotBracketFromPdb.depthFirstSearch(strand, strandMap, visited, component);
        components.add(component);
      }
    }

    // prepare the final result
    final List<CombinedStrandFromPdb> result = new ArrayList<>(components.size());
    for (final Set<Strand> strandCluster : components) {
      final ArrayList<Strand> combinedStrands = new ArrayList<>(strandCluster);
      combinedStrands.sort(Comparator.comparingInt(strands::indexOf));
      result.add(new CombinedStrandFromPdb(combinedStrands, symbolToResidue));
    }

    return result;
  }

  @Override
  public final List<? extends CombinedStrand> combineStrands() {
    return super.combineStrands().stream()
        .map(
            combinedStrand ->
                new CombinedStrandFromPdb(combinedStrand.getStrands(), symbolToResidue))
        .collect(Collectors.toList());
  }

  @Override
  public final int getRealSymbolIndex(final DotBracketSymbol symbol) {
    return symbolToResidue.get(symbol).residueNumber();
  }

  public final Map<DotBracketSymbol, PdbNamedResidueIdentifier> getSymbolToResidue() {
    return Collections.unmodifiableMap(symbolToResidue);
  }

  public final Map<PdbNamedResidueIdentifier, DotBracketSymbol> getResidueToSymbol() {
    return Collections.unmodifiableMap(residueToSymbol);
  }

  public final BasePair basePair(final DotBracketSymbol symbol) {
    if (symbol.isPairing()) {
      return new BasePair(symbolToResidue.get(symbol), symbolToResidue.get(symbol.getPair()));
    }
    throw new IllegalArgumentException(
        "Cannot create base pair from unpaired nucleotide: " + symbol);
  }

  private void mapSymbolsAndResidues(final ResidueCollection model) {
    final List<PdbResidue> residues = model.residues();
    assert residues.size() == symbols.size();

    for (int i = 0; i < residues.size(); i++) {
      final DotBracketSymbol symbol = symbols.get(i);
      final PdbResidue residue = residues.get(i);
      final PdbNamedResidueIdentifier residueIdentifier = residue.namedResidueIdentifer();
      symbolToResidue.put(symbol, residueIdentifier);
      residueToSymbol.put(residueIdentifier, symbol);
    }
  }

  private void splitStrands(final StructureModel model) {
    strands.clear();
    int start = 0;
    int end = 0;

    for (final PdbChain chain : model.chains()) {
      end += chain.residues().size();
      strands.add(new StrandView(chain.identifier(), this, start, end));
      start = end;
    }
  }

  private void markRepresentedNonCanonicals(
      final Iterable<? extends ClassifiedBasePair> nonCanonical) {
    final Collection<BasePair> representedSet = new HashSet<>();

    for (final DotBracketSymbol symbol : symbols) {
      if (symbol.isPairing()) {
        final PdbNamedResidueIdentifier left = getResidueIdentifier(symbol);
        final PdbNamedResidueIdentifier right = getResidueIdentifier(symbol.getPair());
        representedSet.add(new BasePair(left, right));
      }
    }

    for (final ClassifiedBasePair cbp : nonCanonical) {
      final BasePair basePair = cbp.getBasePair();
      if (representedSet.contains(basePair)) {
        cbp.setRepresented(true);

        if (!cbp.isCanonical()) {
          final DotBracketSymbol left = getSymbol(basePair.getLeft());
          final DotBracketSymbol right = getSymbol(basePair.getRight());
          left.setNonCanonical(true);
          right.setNonCanonical(true);
        }
      }
    }
  }

  private void linkStrands(
      final Strand firstStrand,
      final DotBracketSymbol symbolInSecondStrand,
      final Map<? super Strand, Set<Strand>> strandMap) {
    for (final Strand secondStrand : strands) {
      if (!secondStrand.equals(firstStrand)
          && secondStrand.getSymbols().contains(symbolInSecondStrand)) {
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
