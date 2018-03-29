package pl.poznan.put.structure.secondary.formats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.structure.secondary.BasePair;
import pl.poznan.put.structure.secondary.ClassifiedBasePair;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

public class DotBracketFromPdb extends DotBracket implements DotBracketFromPdbInterface {
  private static final long serialVersionUID = -4415694977869681897L;

  private final Map<DotBracketSymbol, PdbResidueIdentifier> symbolToResidue = new HashMap<>();
  private final Map<PdbResidueIdentifier, DotBracketSymbol> residueToSymbol = new HashMap<>();

  public DotBracketFromPdb(
      final DotBracketInterface dotBracket,
      final PdbModel model,
      final Iterable<ClassifiedBasePair> nonCanonical)
      throws InvalidStructureException {
    this(dotBracket.getSequence(), dotBracket.getStructure(), model);
    markRepresentedNonCanonicals(nonCanonical);
  }

  private void markRepresentedNonCanonicals(final Iterable<ClassifiedBasePair> nonCanonical) {
    final Collection<BasePair> representedSet = new HashSet<>();

    for (final DotBracketSymbol symbol : symbols) {
      if (symbol.isPairing()) {
        final PdbResidueIdentifier left = getResidueIdentifier(symbol);
        final PdbResidueIdentifier right = getResidueIdentifier(symbol.getPair());
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

  public DotBracketFromPdb(final String sequence, final String structure, final PdbModel model)
      throws InvalidStructureException {
    super(sequence, DotBracketFromPdb.updateMissingIndices(structure, model));

    mapSymbolsAndResidues(model);
    splitStrands(model);
  }

  private static String updateMissingIndices(
      final String structure, final ResidueCollection model) {
    final List<PdbResidue> residues = model.getResidues();
    final char[] dotBracket = structure.toCharArray();
    assert dotBracket.length == residues.size();

    for (int i = 0; i < dotBracket.length; i++) {
      if (residues.get(i).isMissing()) {
        dotBracket[i] = '-';
      }
    }

    return String.valueOf(dotBracket);
  }

  private void mapSymbolsAndResidues(final ResidueCollection model) {
    final List<PdbResidue> residues = model.getResidues();
    assert residues.size() == symbols.size();

    for (int i = 0; i < residues.size(); i++) {
      final DotBracketSymbol symbol = symbols.get(i);
      final PdbResidue residue = residues.get(i);
      final PdbResidueIdentifier residueIdentifier = residue.getResidueIdentifier();
      symbolToResidue.put(symbol, residueIdentifier);
      residueToSymbol.put(residueIdentifier, symbol);
    }
  }

  private void splitStrands(final PdbModel model) {
    strands.clear();
    int start = 0;
    int end = 0;

    for (final PdbChain chain : model.getChains()) {
      end += chain.getResidues().size();
      strands.add(new StrandView(chain.getIdentifier(), this, start, end));
      start = end;
    }
  }

  @Override
  public final PdbResidueIdentifier getResidueIdentifier(final DotBracketSymbol symbol) {
    return symbolToResidue.get(symbol);
  }

  @Override
  public final DotBracketSymbol getSymbol(final PdbResidueIdentifier residueIdentifier) {
    return residueToSymbol.get(residueIdentifier);
  }

  @Override
  public final boolean contains(final PdbResidueIdentifier residueIdentifier) {
    return residueToSymbol.containsKey(residueIdentifier);
  }

  @Override
  public final int getRealSymbolIndex(final DotBracketSymbol symbol) {
    return symbolToResidue.get(symbol).getResidueNumber();
  }

  @Override
  public final List<? extends CombinedStrand> combineStrands() {
    final List<CombinedStrand> result = new ArrayList<>();
    for (final CombinedStrand combinedStrand : super.combineStrands()) {
      result.add(new CombinedStrandFromPdb(combinedStrand.getStrands(), symbolToResidue));
    }
    return result;
  }

  @Override
  public final List<CombinedStrandFromPdb> combineStrands(
      final List<ClassifiedBasePair> nonCanonicalPairs) {
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
    for (final ClassifiedBasePair nonCanonicalPair : nonCanonicalPairs) {
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

    // now link in depth all the strands linked together even indirectly
    final Map<Strand, Set<Strand>> solutionMap = new LinkedHashMap<>();
    final Collection<Set<Strand>> strandClusters = new ArrayList<>();

    for (final Map.Entry<Strand, Set<Strand>> entry : strandMap.entrySet()) {
      final Strand strand = entry.getKey();
      final Set<Strand> linkedStrands = entry.getValue();

      if (!solutionMap.containsKey(strand)) {
        final Set<Strand> strandCluster = new LinkedHashSet<>();
        solutionMap.put(strand, strandCluster);
        strandClusters.add(strandCluster);
      }

      final Set<Strand> strandCluster = solutionMap.get(strand);
      strandCluster.add(strand);

      for (final Strand linkedStrand : linkedStrands) {
        solutionMap.put(linkedStrand, strandCluster);
        strandCluster.add(linkedStrand);
      }
    }

    // prepare the final result
    final List<CombinedStrandFromPdb> result = new ArrayList<>(strandClusters.size());
    for (final Set<Strand> strandCluster : strandClusters) {
      result.add(new CombinedStrandFromPdb(new ArrayList<>(strandCluster), symbolToResidue));
    }

    // add strands without inter-strand connections
    for (final Strand strand : strands) {
      if (!solutionMap.containsKey(strand)) {
        result.add(new CombinedStrandFromPdb(Collections.singletonList(strand), symbolToResidue));
      }
    }
    return result;
  }

  private void linkStrands(
      final Strand firstStrand,
      final DotBracketSymbol symbolInSecondStrand,
      final Map<Strand, Set<Strand>> strandMap) {
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
