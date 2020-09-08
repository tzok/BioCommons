package pl.poznan.put.structure.secondary.formats;

import lombok.EqualsAndHashCode;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
public class StrandDirect extends AbstractStrand {
  private final List<DotBracketSymbol> symbols;

  public StrandDirect(final String name, final List<DotBracketSymbol> symbols) {
    super(name);
    this.symbols = new ArrayList<>(symbols);
  }

  @Override
  public final TerminalMissing getMissingBegin() {
    final List<DotBracketSymbol> missing = new ArrayList<>();
    for (final DotBracketSymbol symbol : symbols) {
      if (!symbol.isMissing()) {
        break;
      }
      missing.add(symbol);
    }
    return new TerminalMissing(missing);
  }

  @Override
  public final TerminalMissing getMissingEnd() {
    final List<DotBracketSymbol> missing = new ArrayList<>();
    for (int i = symbols.size() - 1; i >= 0; i--) {
      final DotBracketSymbol symbol = symbols.get(i);
      if (!symbol.isMissing()) {
        break;
      }
      missing.add(symbol);
    }
    return new TerminalMissing(missing);
  }

  @Override
  public final List<DotBracketSymbol> getSymbols() {
    return Collections.unmodifiableList(symbols);
  }

  @Override
  public final String getDescription() {
    return String.format(
        "%d %d %s %s %s",
        symbols.get(0).getIndex(),
        symbols.get(symbols.size() - 1).getIndex(),
        getSequence(),
        getStructure(),
        getRSequence());
  }

  @Override
  public final int getLength() {
    return symbols.size();
  }
}
