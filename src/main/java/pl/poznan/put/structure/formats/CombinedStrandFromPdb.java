package pl.poznan.put.structure.formats;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.immutables.value.Value;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.DotBracketSymbol;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A dot-bracket encoded structure made from combining one or more strands. This structure has
 * one-to-one correspondence with a 3D structure.
 */
@Value.Immutable
public abstract class CombinedStrandFromPdb extends AbstractCombinedStrand
    implements DotBracketFromPdb {
  /** @return The list of input strands. */
  @Override
  @Value.Parameter(order = 1)
  protected abstract List<Strand> inputStrands();

  @Override
  @Value.Lazy
  public List<DotBracketSymbol> symbols() {
    return super.symbols();
  }

  @Override
  @Value.Lazy
  public List<Strand> strands() {
    return super.strands();
  }

  /** @return The mapping of dot-bracket symbols with corresponding PDB identifiers. */
  @Value.Parameter(order = 2)
  protected abstract Map<DotBracketSymbol, PdbResidueIdentifier> inputSymbolToResidue();

  @Override
  public final int originalIndex(final DotBracketSymbol symbol) {
    return mapping().get(symbol).residueNumber();
  }

  @Override
  @Value.Lazy
  @Value.Auxiliary
  public Map<DotBracketSymbol, DotBracketSymbol> pairs() {
    return super.pairs();
  }

  @Override
  public final PdbResidueIdentifier identifier(final DotBracketSymbol symbol) {
    return mapping().get(symbol);
  }

  @Override
  public final DotBracketSymbol symbol(final PdbResidueIdentifier residueIdentifier) {
    return mapping().getKey(residueIdentifier);
  }

  @Override
  public final boolean contains(final PdbResidueIdentifier residueIdentifier) {
    return mapping().containsValue(residueIdentifier);
  }

  @Override
  public final List<DotBracketFromPdb> combineStrands(final List<ClassifiedBasePair> unused) {
    return Collections.singletonList(this);
  }

  @Override
  public final Set<PdbResidueIdentifier> identifierSet() {
    return mapping().values();
  }

  @Value.Lazy
  @Value.Auxiliary
  protected Map<DotBracketSymbol, PdbResidueIdentifier> symbolToResidue() {
    final List<DotBracketSymbol> inputSymbols =
        inputStrands().stream()
            .map(DotBracket::symbols)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    return IntStream.range(0, inputSymbols.size())
        .boxed()
        .collect(
            Collectors.toMap(
                i -> symbols().get(i), i -> inputSymbolToResidue().get(inputSymbols.get(i))));
  }

  @Value.Lazy
  @Value.Auxiliary
  protected BidiMap<DotBracketSymbol, PdbResidueIdentifier> mapping() {
    return new DualHashBidiMap<>(
        symbols().stream().collect(Collectors.toMap(Function.identity(), symbolToResidue()::get)));
  }
}
