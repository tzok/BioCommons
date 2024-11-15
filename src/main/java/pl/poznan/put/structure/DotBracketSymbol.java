package pl.poznan.put.structure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import org.immutables.value.Value;

/** A single symbol in a dot-bracket structure. */
@Value.Immutable
@JsonSerialize(as = ImmutableDotBracketSymbol.class)
@JsonDeserialize(as = ImmutableDotBracketSymbol.class)
public abstract class DotBracketSymbol implements Comparable<DotBracketSymbol>, Serializable {
  private static final List<Character> OPENING =
      "([{<ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().mapToObj(c -> (char) c).collect(Collectors.toList());
  private static final List<Character> CLOSING =
      ")]}>abcdefghijklmnopqrstuvwxyz".chars().mapToObj(c -> (char) c).collect(Collectors.toList());

  /**
   * Checks if the given character is a pairing character in dot-bracket format.
   *
   * @param c The character to check.
   * @return True if {@code c} is either an opening or closing symbol.
   */
  public static boolean isPairing(final char c) {
    return DotBracketSymbol.isOpening(c) || DotBracketSymbol.isClosing(c);
  }

  /**
   * Checks if the given character is an opening character in dot-bracket format.
   *
   * @param c The character to check
   * @return True if {@code c} is one of: ([{&lt;ABCDEFGHIJKLMNOPQRSTUVWXYZ.
   */
  public static boolean isOpening(final char c) {
    return DotBracketSymbol.OPENING.contains(c);
  }

  /**
   * Checks if the given character is a closing character in dot-bracket format.
   *
   * @param c The character to check
   * @return True if {@code c} is one of: )]}&gt;abcdefghijklmnopqrstuvwxyz.
   */
  public static boolean isClosing(final char c) {
    return DotBracketSymbol.CLOSING.contains(c);
  }

  /**
   * Finds a matching bracket (closing for opening and vice versa) of the same level.
   *
   * @param c The character to find a matching bracket for.
   * @return A matching bracket or a dot if the input is also a dot.
   */
  public static char matchingBracket(final char c) {
    if (DotBracketSymbol.isOpening(c)) {
      return DotBracketSymbol.CLOSING.get(DotBracketSymbol.order(c));
    }
    if (DotBracketSymbol.isClosing(c)) {
      return DotBracketSymbol.OPENING.get(DotBracketSymbol.order(c));
    }
    return '.';
  }

  private static int order(final char c) {
    int result = DotBracketSymbol.OPENING.indexOf(c);
    if (result == -1) {
      result = DotBracketSymbol.CLOSING.indexOf(c);
    }
    return result == -1 ? 0 : result;
  }

  /**
   * @return The sequence character.
   */
  @Value.Parameter(order = 1)
  public abstract char sequence();

  /**
   * @return The structure character (dot or bracket).
   */
  @Value.Parameter(order = 2)
  public abstract char structure();

  /**
   * @return The index of this symbol.
   */
  @Value.Parameter(order = 3)
  public abstract int index();

  /**
   * @return True if this symbol is a minus '-'.
   */
  public final boolean isMissing() {
    return structure() == '-';
  }

  /**
   * @return True if this symbol is either opening or closing.
   */
  public final boolean isPairing() {
    return isOpening() || isClosing();
  }

  /**
   * @return True if this symbol is opening (see {@link DotBracketSymbol#isOpening(char)}).
   */
  public final boolean isOpening() {
    return DotBracketSymbol.isOpening(structure());
  }

  /**
   * @return True if this symbol is closing (see {@link DotBracketSymbol#isClosing(char)}).
   */
  public final boolean isClosing() {
    return DotBracketSymbol.isClosing(structure());
  }

  /**
   * @return The pseudoknot order of this symbol.
   */
  public final int order() {
    return DotBracketSymbol.order(structure());
  }

  @Override
  public final String toString() {
    return index() + " " + sequence() + ' ' + structure();
  }

  @Override
  public final int compareTo(final DotBracketSymbol t) {
    return Integer.compare(index(), t.index());
  }
}
