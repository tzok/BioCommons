package pl.poznan.put.structure.secondary.formats;

import lombok.Data;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.List;

@Data
public class TerminalMissing {
  private final List<DotBracketSymbol> symbols;

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
