package pl.poznan.put.structure.formats;

import pl.poznan.put.structure.DotBracketSymbol;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Strand {
  String name();

  TerminalMissing missingBegin();

  TerminalMissing missingEnd();

  List<DotBracketSymbol> symbols();

  /**
   * Prepare description of strand in RNAComposer format i.e. 5 elements: index-from, index-to,
   * sequence, structure, RY-sequence.
   *
   * @return A description of strand in RNAComposer format.
   */
  String description();

  default String structure() {
    final List<DotBracketSymbol> symbols = symbols();
    return symbols.stream()
        .map(symbol -> String.valueOf(symbol.structure()))
        .collect(Collectors.joining());
  }

  default int length() {
    return end() - begin();
  }

  default int begin() {
    final List<DotBracketSymbol> symbols = symbols();
    return symbols.isEmpty() ? 1 : symbols.get(0).index();
  }

  default int end() {
    final List<DotBracketSymbol> symbols = symbols();
    return symbols.isEmpty() ? 1 : symbols.get(symbols.size() - 1).index();
  }

  default int pseudoknotOrder() {
    return symbols().stream()
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
    final List<DotBracketSymbol> symbols = symbols();
    return IntStream.range(1, (symbols.size() - 1))
        .mapToObj(symbols::get)
        .noneMatch(
            symbol ->
                symbol.isPairing()
                    && symbols.contains(symbol.pair().orElseThrow(InvalidStructureException::new)));
  }

  default String sequenceRY() {
    final char[] cs = sequence().toCharArray();
    for (int i = 0; i < cs.length; i++) {
      cs[i] = ((cs[i] == 'A') || (cs[i] == 'G')) ? 'R' : 'Y';
    }
    return new String(cs);
  }

  default String sequence() {
    final List<DotBracketSymbol> symbols = symbols();
    return symbols.stream()
        .map(symbol -> String.valueOf(symbol.sequence()))
        .collect(Collectors.joining());
  }

  default boolean containsMissing() {
    return symbols().stream().anyMatch(DotBracketSymbol::isMissing);
  }
}
