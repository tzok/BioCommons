package pl.poznan.put.structure.formats;

import org.immutables.value.Value;
import pl.poznan.put.structure.DotBracketSymbol;

import java.util.List;

/** A default implementation of a strand. */
@Value.Immutable
public abstract class DefaultStrand implements Strand {
  @Override
  @Value.Parameter(order = 1)
  public abstract String name();

  @Override
  @Value.Parameter(order = 2)
  public abstract List<DotBracketSymbol> symbols();

  @Override
  public final String toString() {
    return String.format(">strand_%s\n%s\n%s", name(), sequence(), structure());
  }
}
