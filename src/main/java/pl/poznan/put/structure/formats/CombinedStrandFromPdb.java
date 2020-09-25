package pl.poznan.put.structure.formats;

import org.immutables.value.Value;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ClassifiedBasePair;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Value.Immutable
public abstract class CombinedStrandFromPdb implements DotBracketFromPdb {
  @Value.Parameter(order = 1)
  protected abstract CombinedStrand combinedStrand();

  @Value.Parameter(order = 2)
  protected abstract Map<DotBracketSymbol, PdbNamedResidueIdentifier> inputSymbolToResidue();

  @Override
  public String sequence() {
    return combinedStrand().sequence();
  }

  @Override
  public String structure() {
    return combinedStrand().structure();
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
  public final List<CombinedStrandFromPdb> combineStrands(final List<ClassifiedBasePair> unused) {
    return Collections.singletonList(this);
  }

  public final Set<PdbNamedResidueIdentifier> getResidueIdentifiers() {
    return residueToSymbol().keySet();
  }

  @Value.Lazy
  @Value.Auxiliary
  protected Map<DotBracketSymbol, PdbNamedResidueIdentifier> symbolToResidue() {
    final List<DotBracketSymbol> inputSymbols =
        combinedStrand().inputStrands().stream()
            .map(Strand::symbols)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    return IntStream.range(0, inputSymbols.size())
        .boxed()
        .collect(
            Collectors.toMap(
                i -> symbols().get(i), i -> inputSymbolToResidue().get(inputSymbols.get(i))));
  }

  @Value.Auxiliary
  protected Map<PdbNamedResidueIdentifier, DotBracketSymbol> residueToSymbol() {
    return symbolToResidue().entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
  }
}
