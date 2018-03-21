package pl.poznan.put.structure.secondary.formats;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.secondary.ClassifiedBasePair;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

public class CombinedStrandFromPdb extends CombinedStrand implements DotBracketFromPdbInterface {
  private final Map<DotBracketSymbol, PdbResidueIdentifier> symbolToResidue = new HashMap<>();
  private final Map<PdbResidueIdentifier, DotBracketSymbol> residueToSymbol = new HashMap<>();

  public CombinedStrandFromPdb(
      final List<Strand> strands,
      final Map<DotBracketSymbol, PdbResidueIdentifier> previousSymbolToResidue) {
    super(strands);

    int i = 0;
    for (final Strand strand : strands) {
      for (final DotBracketSymbol previousSymbol : strand.getSymbols()) {
        final DotBracketSymbol currentSymbol = symbols.get(i);
        final PdbResidueIdentifier residueIdentifier = previousSymbolToResidue.get(previousSymbol);
        symbolToResidue.put(currentSymbol, residueIdentifier);
        residueToSymbol.put(residueIdentifier, currentSymbol);
        i += 1;
      }
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
}
