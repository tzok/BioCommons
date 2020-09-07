package pl.poznan.put.structure.secondary.formats;

import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Strand {
  String getName();

  TerminalMissing getMissingBegin();

  TerminalMissing getMissingEnd();

  default String getStructure() {
    final List<DotBracketSymbol> symbols = getSymbols();
    return symbols.stream()
        .map(symbol -> String.valueOf(symbol.getStructure()))
        .collect(Collectors.joining());
  }

  List<DotBracketSymbol> getSymbols();

  default int getLength() {
    return getTo() - getFrom();
  }

  default int getFrom() {
    final List<DotBracketSymbol> symbols = getSymbols();
    return symbols.isEmpty() ? 1 : symbols.get(0).getIndex();
  }

  default int getTo() {
    final List<DotBracketSymbol> symbols = getSymbols();
    return symbols.isEmpty() ? 1 : symbols.get(symbols.size() - 1).getIndex();
  }

  default int getPseudoknotOrder() {
    return getSymbols().stream()
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
    return IntStream.range(1, (symbols.size() - 1))
        .mapToObj(symbols::get)
        .noneMatch(symbol -> symbol.isPairing() && symbols.contains(symbol.getPair()));
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

  default String getSequence() {
    final List<DotBracketSymbol> symbols = getSymbols();
    return symbols.stream()
        .map(symbol -> String.valueOf(symbol.getSequence()))
        .collect(Collectors.joining());
  }

  default boolean containsMissing() {
    return getSymbols().stream().anyMatch(DotBracketSymbol::isMissing);
  }
}
