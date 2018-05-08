package pl.poznan.put.structure.secondary.formats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.secondary.ClassifiedBasePair;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

public class CombinedStrandFromPdb extends CombinedStrand implements DotBracketFromPdbInterface {
  private final Map<DotBracketSymbol, PdbResidueIdentifier> symbolToResidue = new HashMap<>();
  private final Map<PdbResidueIdentifier, DotBracketSymbol> residueToSymbol = new HashMap<>();

  public CombinedStrandFromPdb(
      final Iterable<Strand> strands,
      final Map<DotBracketSymbol, PdbResidueIdentifier> symbolToResidue) {
    super();

    final Map<DotBracketSymbol, Integer> symbolToIndex = new HashMap<>();
    int i = 0;
    for (final Strand strand : strands) {
      for (final DotBracketSymbol symbol : strand.getSymbols()) {
        symbolToIndex.put(symbol, i);
        i += 1;
      }
    }

    for (final Strand strand : strands) {
      final List<DotBracketSymbol> strandSymbols = new ArrayList<>();
      for (final DotBracketSymbol symbol : strand.getSymbols()) {
        final char sequence = symbol.getSequence();
        final char structure = symbol.getStructure();
        final int index = symbolToIndex.get(symbol);
        final DotBracketSymbol renumbered = new DotBracketSymbol(sequence, structure, index);
        strandSymbols.add(renumbered);
        symbols.add(renumbered);

        final PdbResidueIdentifier identifier = symbolToResidue.get(symbol);
        this.symbolToResidue.put(renumbered, identifier);
      }
      this.strands.add(new StrandDirect(strand.getName(), strandSymbols));
    }

    for (final Strand strand : strands) {
      for (final DotBracketSymbol symbol : strand.getSymbols()) {
        if (symbol.isPairing()) {
          final DotBracketSymbol u = symbols.get(symbolToIndex.get(symbol));
          final DotBracketSymbol v = symbols.get(symbolToIndex.get(symbol.getPair()));
          u.setPair(v);
          v.setPair(u);
        }
      }
    }

    for (final Map.Entry<DotBracketSymbol, PdbResidueIdentifier> entry :
        this.symbolToResidue.entrySet()) {
      residueToSymbol.put(entry.getValue(), entry.getKey());
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
  public final List<CombinedStrandFromPdb> combineStrands(
      final List<ClassifiedBasePair> availableNonCanonical) {
    return Collections.singletonList(this);
  }

  @Override
  public final int getRealSymbolIndex(final DotBracketSymbol symbol) {
    return symbolToResidue.get(symbol).getResidueNumber();
  }

  public final Set<PdbResidueIdentifier> getResidueIdentifiers() {
    return residueToSymbol.keySet();
  }
}
