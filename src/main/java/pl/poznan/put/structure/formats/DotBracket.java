package pl.poznan.put.structure.formats;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import pl.poznan.put.structure.DotBracketSymbol;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** An RNA structure encoded in dot-bracket format. */
public interface DotBracket {
  /** @return The list of dot-bracket symbols. */
  List<DotBracketSymbol> symbols();

  /**
   * Combines strands which share a base pair into a new dot-bracket instance and returns a list of
   * those.
   *
   * @return The list of dot-bracket instances, each containing strands which only pair with each
   *     other.
   */
  List<DotBracket> combineStrands();

  /**
   * Returns the index of a dot-bracket symbol according to some external source like PDB numbering.
   *
   * @param symbol Dot-bracket symbol for which the original index is sought.
   * @return An index which reflects the numbering in real structure (e.g. PDB).
   */
  default int originalIndex(final DotBracketSymbol symbol) {
    return symbol.index() + 1;
  }

  /** @return The list of strands. */
  default List<Strand> strands() {
    return Collections.singletonList(ImmutableStrandView.of("", this, 0, structure().length()));
  }

  /** @return The sequence of nucleotides. */
  default String sequence() {
    return symbols().stream()
        .map(DotBracketSymbol::sequence)
        .map(c -> Character.toString(c))
        .collect(Collectors.joining());
  }

  /** @return The sequence of dots and brackets representing paired and unpaired residues. */
  default String structure() {
    return symbols().stream()
        .map(DotBracketSymbol::structure)
        .map(c -> Character.toString(c))
        .collect(Collectors.joining());
  }

  default Map<DotBracketSymbol, DotBracketSymbol> pairs() {
    final String opening = "([{<ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    final String closing = ")]}>abcdefghijklmnopqrstuvwxyz";

    final BidiMap<Character, Character> parentheses =
        new DualHashBidiMap<>(
            IntStream.range(0, opening.length())
                .boxed()
                .collect(Collectors.toMap(opening::charAt, closing::charAt)));

    final Map<Character, Deque<DotBracketSymbol>> parenthesesStacks =
        parentheses.keySet().stream()
            .collect(Collectors.toMap(Function.identity(), ignored -> new ArrayDeque<>()));

    final Map<DotBracketSymbol, DotBracketSymbol> result = new HashMap<>();

    for (final DotBracketSymbol symbol : symbols()) {
      final char structure = symbol.structure();

      if (parentheses.containsKey(structure)) {
        // catch opening '(', '[', etc.
        parenthesesStacks.get(structure).push(symbol);
      } else if (parentheses.containsValue(structure)) {
        // catch closing ')', ']', etc.
        final DotBracketSymbol pair = parenthesesStacks.get(parentheses.getKey(structure)).pop();
        result.put(symbol, pair);
        result.put(pair, symbol);
      }
    }

    return result;
  }

  /**
   * @return A string representation of this dot-bracket, where every strand is written out
   *     separately.
   */
  default String toStringWithStrands() {
    return strands().stream().map(String::valueOf).collect(Collectors.joining("\n"));
  }

  /** @return The number of nucleotides in this structure. */
  default int length() {
    return symbols().size();
  }

  /** @return True, if at least one symbol represents a missing residue. */
  default boolean containsMissing() {
    return symbols().stream().anyMatch(DotBracketSymbol::isMissing);
  }

  /** @return The list of missing symbols at 5' and 3' ends of all strands. */
  default List<TerminalMissing> missingTerminal() {
    return strands().stream()
        .flatMap(strand -> Stream.of(strand.missingBegin(), strand.missingEnd()))
        .collect(Collectors.toList());
  }

  /** @return The list of missing symbols which are not at 5' or 3' ends of any strand. */
  default List<DotBracketSymbol> missingInternal() {
    // collect all missing from beginning and ends of strands
    final Set<DotBracketSymbol> missingTerminal =
        missingTerminal().stream()
            .map(TerminalMissing::symbols)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

    // get all missing symbols which are internal
    return strands().stream()
        .flatMap(strand -> strand.symbols().stream())
        .filter(DotBracketSymbol::isMissing)
        .filter(dotBracketSymbol -> !missingTerminal.contains(dotBracketSymbol))
        .collect(Collectors.toList());
  }

  /** @return The pseudoknot order of this structure. */
  default int pseudoknotOrder() {
    return symbols().stream().map(DotBracketSymbol::order).max(Comparator.naturalOrder()).orElse(0);
  }

  /**
   * Finds a strand which contains the given symbol.
   *
   * @param symbol The symbol to look for.
   * @return The strand containing the symbol.
   */
  default Strand findStrand(final DotBracketSymbol symbol) {
    return strands().stream()
        .filter(strand -> strand.symbols().contains(symbol))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException("Failed to find strand containing symbol: " + symbol));
  }

  /**
   * Creates a string representation of nucleotide sequence, where strands may be separated with
   * ampersand {@code &amp;} or not.
   *
   * @param separateStrands If true, the result will contain ampersand between strands.
   * @return The string representation of nucleotide sequence.
   */
  default String sequence(final boolean separateStrands) {
    final StringBuilder builder = new StringBuilder();
    for (final Strand strand : strands()) {
      builder.append(strand.sequence());
      if (separateStrands) {
        builder.append('&');
      }
    }
    return builder.toString();
  }

  /**
   * Creates a string of dots and brackets which represents base pairing, where strands may be
   * separated with ampersand {@code &amp;} or not.
   *
   * @param separateStrands If true, the result will contain ampersand between strands.
   * @return The string representation of this structure.
   */
  default String structure(final boolean separateStrands) {
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
