package pl.poznan.put.structure.secondary.formats;

import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DotBracket {
  String sequence();

  String structure();

  List<DotBracketSymbol> symbols();

  List<Strand> strands();

  List<DotBracket> combineStrands();

  /**
   * Return *real* index of a dot-bracket symbol. The index can reflect PDB residue number or other
   * data source.
   *
   * @param symbol Dot-bracket symbol for which a real index is sought.
   * @return An index which reflects the numbering in real structure (e.g. PDB).
   */
  int getRealSymbolIndex(DotBracketSymbol symbol);

  default String toStringWithStrands() {
    return strands().stream().map(String::valueOf).collect(Collectors.joining("\n"));
  }

  default int length() {
    return sequence().length();
  }

  default boolean contains(final DotBracketSymbol symbol) {
    return strands().stream().anyMatch(strand -> strand.symbols().contains(symbol));
  }

  default List<TerminalMissing> getTerminalMissing() {
    return strands().stream()
        .flatMap(strand -> Stream.of(strand.missingBegin(), strand.missingEnd()))
        .collect(Collectors.toList());
  }

  default List<DotBracketSymbol> getInternalMissing() {
    // collect all missing from beginning and ends of strands
    final Set<DotBracketSymbol> missingNonInternal =
        strands().stream()
            .flatMap(
                strand ->
                    Stream.concat(
                        strand.missingBegin().symbols().stream(),
                        strand.missingEnd().symbols().stream()))
            .collect(Collectors.toSet());

    // get all missing symbols which are internal
    return strands().stream()
        .flatMap(strand -> strand.symbols().stream())
        .filter(dotBracketSymbol -> !missingNonInternal.contains(dotBracketSymbol))
        .filter(DotBracketSymbol::isMissing)
        .collect(Collectors.toList());
  }

  default int getPseudoknotOrder() {
    return strands().stream().map(Strand::pseudoknotOrder).max(Integer::compareTo).orElse(0);
  }

  default Strand getStrand(final DotBracketSymbol symbol) {
    return strands().stream()
        .filter(strand -> strand.symbols().contains(symbol))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException("Failed to find strand containing symbol: " + symbol));
  }

  default String getSequence(final boolean separateStrands) {
    final StringBuilder builder = new StringBuilder();
    for (final Strand strand : strands()) {
      builder.append(strand.sequence());
      if (separateStrands) {
        builder.append('&');
      }
    }
    return builder.toString();
  }

  default String getStructure(final boolean separateStrands) {
    final StringBuilder builder = new StringBuilder();
    for (final Strand strand : strands()) {
      builder.append(strand.structure());
      if (separateStrands) {
        builder.append('&');
      }
    }
    return builder.toString();
  }
}
