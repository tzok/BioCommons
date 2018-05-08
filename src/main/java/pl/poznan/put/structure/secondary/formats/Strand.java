package pl.poznan.put.structure.secondary.formats;

import java.util.Comparator;
import java.util.List;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

public interface Strand {
  String getName();

  List<DotBracketSymbol> getSymbols();

  TerminalMissing getMissingBegin();

  TerminalMissing getMissingEnd();

  default int getFrom() {
    final List<DotBracketSymbol> symbols = getSymbols();
    return symbols.isEmpty() ? 1 : symbols.get(0).getIndex();
  }

  default int getTo() {
    final List<DotBracketSymbol> symbols = getSymbols();
    return symbols.isEmpty() ? 1 : symbols.get(symbols.size() - 1).getIndex();
  }

  default String getSequence() {
    final List<DotBracketSymbol> symbols = getSymbols();
    final StringBuilder builder = new StringBuilder(symbols.size());
    for (final DotBracketSymbol symbol : symbols) {
      builder.append(symbol.getSequence());
    }
    return builder.toString();
  }

  default String getStructure() {
    final List<DotBracketSymbol> symbols = getSymbols();
    final StringBuilder builder = new StringBuilder(symbols.size());
    for (final DotBracketSymbol symbol : symbols) {
      builder.append(symbol.getStructure());
    }
    return builder.toString();
  }

  default int getLength() {
    return getTo() - getFrom();
  }

  default int getPseudoknotOrder() {
    return getSymbols()
        .stream()
        .map(DotBracketSymbol::getOrder)
        .max(Comparator.naturalOrder())
        .orElse(0);
  }

  /**
   * Check if this strand is "single strand" which means that it does not have any base-pair
   * embedded inside its structure.
   *
   * @return True if there is no base-pair inside of this strand. An opening or closing bracket is
   *     allowed as long as it points somewhere outside this strand.
   */
  default boolean isSingleStrand() {
    final List<DotBracketSymbol> symbols = getSymbols();
    for (int i = 1; i < (symbols.size() - 1); i++) {
      final DotBracketSymbol symbol = symbols.get(i);
      if (symbol.isPairing() && symbols.contains(symbol.getPair())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Prepare description of strand in RNAComposer format i.e. 5 elements: index-from, index-to,
   * sequence, structure, RY-sequence.
   *
   * @return A description of strand in RNAComposer format.
   */
  String getDescription();

  default String getRSequence() {
    final char[] cs = getSequence().toCharArray();
    for (int i = 0; i < cs.length; i++) {
      cs[i] = ((cs[i] == 'A') || (cs[i] == 'G')) ? 'R' : 'Y';
    }
    return new String(cs);
  }

  default boolean containsMissing() {
    return getSymbols().stream().anyMatch(DotBracketSymbol::isMissing);
  }
}
