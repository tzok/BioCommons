package pl.poznan.put.structure.secondary.formats;

import lombok.Data;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.List;

@Data
public class TerminalMissing {
  private final List<DotBracketSymbol> symbols;

  public final boolean contains(final DotBracketSymbol symbol) {
    return symbols.contains(symbol);
  }

  @Override
  public final String toString() {
    if (isEmpty()) {
      return "TerminalMissing, empty symbol list";
    }
    return "TerminalMissing, first " + symbols.get(0) + ", last " + symbols.get(size());
  }

  private boolean isEmpty() {
    return symbols.isEmpty();
  }

  private int size() {
    return symbols.size();
  }
}
