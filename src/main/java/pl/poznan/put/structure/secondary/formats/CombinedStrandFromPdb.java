package pl.poznan.put.structure.secondary.formats;

import lombok.EqualsAndHashCode;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.secondary.ClassifiedBasePair;
import pl.poznan.put.structure.secondary.DotBracketSymbol;
import pl.poznan.put.structure.secondary.ModifiableDotBracketSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class CombinedStrandFromPdb extends CombinedStrand implements DotBracketFromPdbInterface {
  private final Map<DotBracketSymbol, PdbNamedResidueIdentifier> symbolToResidue = new HashMap<>();
  private final Map<PdbNamedResidueIdentifier, DotBracketSymbol> residueToSymbol = new HashMap<>();

  public CombinedStrandFromPdb(
      final Iterable<? extends Strand> strands,
      final Map<DotBracketSymbol, ? extends PdbNamedResidueIdentifier> symbolToResidue) {
    super();

    final Map<DotBracketSymbol, Integer> symbolToIndex = new HashMap<>();
    int i = 0;
    for (final Strand strand : strands) {
      for (final DotBracketSymbol symbol : strand.symbols()) {
        symbolToIndex.put(symbol, i);
        i += 1;
      }
    }

    for (final Strand strand : strands) {
      final List<DotBracketSymbol> strandSymbols = new ArrayList<>();
      for (final DotBracketSymbol symbol : strand.symbols()) {
        final char sequence = symbol.sequence();
        final char structure = symbol.structure();
        final int index = symbolToIndex.get(symbol);
        final ModifiableDotBracketSymbol renumbered =
            ModifiableDotBracketSymbol.create(sequence, structure, index);
        strandSymbols.add(renumbered);
        symbols.add(renumbered);

        final PdbNamedResidueIdentifier identifier = symbolToResidue.get(symbol);
        this.symbolToResidue.put(renumbered, identifier);
      }
      this.strands.add(ImmutableStrandDirect.of(strand.name(), strandSymbols));
    }

    for (final Strand strand : strands) {
      for (final DotBracketSymbol symbol : strand.symbols()) {
        if (symbol.isPairing()) {
          final ModifiableDotBracketSymbol u = symbols.get(symbolToIndex.get(symbol));
          final ModifiableDotBracketSymbol v =
              symbols.get(
                  symbolToIndex.get(
                      symbol
                          .pair()
                          .orElseThrow(
                              () ->
                                  new IllegalArgumentException(
                                      "Paired dot-bracket symbol without `pair` field set: "
                                          + symbol))));
          u.setPair(v);
          v.setPair(u);
        }
      }
    }

    for (final Map.Entry<DotBracketSymbol, PdbNamedResidueIdentifier> entry :
        this.symbolToResidue.entrySet()) {
      residueToSymbol.put(entry.getValue(), entry.getKey());
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
    return Collections.singletonList(this);
  }

  @Override
  public final int getRealSymbolIndex(final DotBracketSymbol symbol) {
    return symbolToResidue.get(symbol).residueNumber();
  }

  public final Set<PdbNamedResidueIdentifier> getResidueIdentifiers() {
    return residueToSymbol.keySet();
  }
}
