package pl.poznan.put.structure.secondary.formats;

import org.immutables.value.Value;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.List;

@Value.Immutable
public abstract class TerminalMissing {
  @Value.Parameter(order = 1)
  public abstract List<DotBracketSymbol> symbols();

  public final boolean contains(final DotBracketSymbol symbol) {
    return symbols().contains(symbol);
  }
}
