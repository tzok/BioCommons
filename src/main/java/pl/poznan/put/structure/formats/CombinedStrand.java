package pl.poznan.put.structure.formats;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.immutables.value.Value;
import pl.poznan.put.structure.DotBracketSymbol;

/** A dot-bracket encoded structure made from combining one or more strands. */
@Value.Immutable
public abstract class CombinedStrand extends AbstractCombinedStrand {
  /**
   * @return The list of input strands.
   */
  @Override
  @Value.Parameter(order = 1)
  protected abstract List<Strand> inputStrands();

  @Override
  @Value.Lazy
  @Value.Auxiliary
  public List<DotBracketSymbol> symbols() {
    return super.symbols();
  }

  @Override
  @Value.Lazy
  @Value.Auxiliary
  public List<Strand> strands() {
    return super.strands();
  }

  @Override
  public final String toString() {
    final String builder = strands().stream().map(Strand::name).collect(Collectors.joining());
    return ">strand_" + builder + '\n' + sequence() + '\n' + structure();
  }

  @Override
  @Value.Lazy
  @Value.Auxiliary
  public Map<DotBracketSymbol, DotBracketSymbol> pairs() {
    return super.pairs();
  }
}
