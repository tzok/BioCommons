package pl.poznan.put.structure.formats;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.immutables.value.Value;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.DotBracketSymbol;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A dot-bracket encoded structure made from combining one or more strands. This structure has
 * one-to-one correspondence with a 3D structure.
 */
@Value.Immutable
public abstract class CombinedStrandFromPdb implements DotBracketFromPdb {
  /** @return The input combined strand. */
  @Value.Parameter(order = 1)
  protected abstract CombinedStrand combinedStrand();

  /** @return The mapping of dot-bracket symbols with corresponding PDB identifiers. */
  @Value.Parameter(order = 2)
  protected abstract Map<DotBracketSymbol, PdbNamedResidueIdentifier> inputSymbolToResidue();

  @Override
  public final String sequence() {
    return combinedStrand().sequence();
  }

  @Override
  public final String structure() {
    return combinedStrand().structure();
  }

  @Override
  public final int originalIndex(final DotBracketSymbol symbol) {
    return mapping().get(symbol).residueNumber();
  }

  @Override
  public final PdbNamedResidueIdentifier identifier(final DotBracketSymbol symbol) {
    return mapping().get(symbol);
  }

  @Override
  public final DotBracketSymbol symbol(final PdbNamedResidueIdentifier residueIdentifier) {
    return mapping().getKey(residueIdentifier);
  }

  @Override
  public final boolean contains(final PdbNamedResidueIdentifier residueIdentifier) {
    return mapping().containsValue(residueIdentifier);
  }

  @Override
  public final List<DotBracketFromPdb> combineStrands(final List<ClassifiedBasePair> unused) {
    return Collections.singletonList(this);
  }

  @Override
  public final Set<PdbNamedResidueIdentifier> identifierSet() {
    return mapping().values();
  }

  @Value.Lazy
  @Value.Auxiliary
  protected BidiMap<DotBracketSymbol, PdbNamedResidueIdentifier> mapping() {
    final Set<DotBracketSymbol> symbols = new HashSet<>(combinedStrand().symbols());
    return new DualHashBidiMap<>(
        symbols().stream()
            .filter(symbols::contains)
            .collect(Collectors.toMap(Function.identity(), inputSymbolToResidue()::get)));
  }
}
