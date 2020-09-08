package pl.poznan.put.structure.secondary.formats;

import org.immutables.value.Value;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.ArrayList;
import java.util.List;

@Value.Immutable
public abstract class StrandDirect implements Strand {
  @Value.Parameter(order = 1)
  public abstract String name();

  @Override
  public final TerminalMissing missingBegin() {
    final List<DotBracketSymbol> missing = new ArrayList<>();
    for (final DotBracketSymbol symbol : symbols()) {
      if (!symbol.isMissing()) {
        break;
      }
      missing.add(symbol);
    }
    return ImmutableTerminalMissing.of(missing);
  }

  @Override
  public final TerminalMissing missingEnd() {
    final List<DotBracketSymbol> missing = new ArrayList<>();
    for (int i = symbols().size() - 1; i >= 0; i--) {
      final DotBracketSymbol symbol = symbols().get(i);
      if (!symbol.isMissing()) {
        break;
      }
      missing.add(symbol);
    }
    return ImmutableTerminalMissing.of(missing);
  }

  @Value.Parameter(order = 2)
  public abstract List<DotBracketSymbol> symbols();

  @Override
  public final String description() {
    return String.format(
        "%d %d %s %s %s",
        symbols().get(0).index(),
        symbols().get(symbols().size() - 1).index(),
        sequence(),
        structure(),
        sequenceRY());
  }

  @Override
  public final int length() {
    return symbols().size();
  }

  @Override
  public final String toString() {
    return String.format(">strand_%s\n%s\n%s", name(), sequence(), structure());
  }
}
