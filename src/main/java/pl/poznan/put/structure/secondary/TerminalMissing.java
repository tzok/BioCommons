package pl.poznan.put.structure.secondary;

import java.util.ArrayList;
import java.util.List;

public class TerminalMissing {
  private final List<DotBracketSymbol> symbols;

  public TerminalMissing(final List<DotBracketSymbol> symbols) {
    super();
    this.symbols = new ArrayList<>(symbols);
  }

  public int getLength() {
    return symbols.size();
  }

  public boolean contains(final DotBracketSymbol symbol) {
    return symbols.contains(symbol);
  }

  @Override
  public String toString() {
    return "TerminalMissing, first " + getFirst() + ", last " + getLast();
  }

  public DotBracketSymbol getFirst() {
    return !symbols.isEmpty() ? symbols.get(0) : null;
  }

  public DotBracketSymbol getLast() {
    return !symbols.isEmpty() ? symbols.get(symbols.size() - 1) : null;
  }
}
