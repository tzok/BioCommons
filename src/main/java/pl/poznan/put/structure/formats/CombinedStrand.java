package pl.poznan.put.structure.formats;

import org.immutables.value.Value;
import pl.poznan.put.structure.DotBracketSymbol;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** A dot-bracket encoded structure made from combining one or more strands. */
@Value.Immutable
public abstract class CombinedStrand extends AbstractCombinedStrand {
  /** @return The list of input strands. */
  @Value.Parameter(order = 1)
  protected abstract List<Strand> inputStrands();

  @Value.Lazy
  @Value.Auxiliary
  public List<DotBracketSymbol> symbols() {
    return super.symbols();
  }

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
  public final List<DotBracket> combineStrands() {
    return Collections.singletonList(this);
  }
}
