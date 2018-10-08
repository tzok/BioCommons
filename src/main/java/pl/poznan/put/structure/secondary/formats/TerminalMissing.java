package pl.poznan.put.structure.secondary.formats;

import java.util.ArrayList;
import java.util.List;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

public class TerminalMissing {
  private final List<DotBracketSymbol> symbols;

  public TerminalMissing(final List<DotBracketSymbol> symbols) {
    super();
    this.symbols = new ArrayList<>(symbols);
  }

  public final boolean isEmpty() {
    return symbols.isEmpty();
  }

  public final int size() {
    return symbols.size();
  }

  public final boolean contains(final DotBracketSymbol symbol) {
    return symbols.contains(symbol);
  }

  @Override
  public final String toString() {
    return "TerminalMissing, first " + first() + ", last " + last();
  }

  public final DotBracketSymbol first() {
    return !symbols.isEmpty() ? symbols.get(0) : null;
  }

  public final DotBracketSymbol last() {
    return !symbols.isEmpty() ? symbols.get(symbols.size() - 1) : null;
  }
}
