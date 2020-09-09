package pl.poznan.put.structure.secondary;

import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Value.Modifiable
public abstract class DotBracketSymbol implements Comparable<DotBracketSymbol>, Serializable {
  private static final List<Character> OPENING =
      "([{<ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().mapToObj(c -> (char) c).collect(Collectors.toList());
  private static final List<Character> CLOSING =
      ")]}>abcdefghijklmnopqrstuvwxyz".chars().mapToObj(c -> (char) c).collect(Collectors.toList());

  public static boolean isPairing(final char c) {
    return DotBracketSymbol.isOpening(c) || DotBracketSymbol.isClosing(c);
  }

  public static boolean isOpening(final char c) {
    return DotBracketSymbol.OPENING.contains(c);
  }

  public static boolean isClosing(final char c) {
    return DotBracketSymbol.CLOSING.contains(c);
  }

  public static char getMatchingBracket(final char c) {
    if (DotBracketSymbol.isOpening(c)) {
      return DotBracketSymbol.CLOSING.get(DotBracketSymbol.getOrder(c));
    }
    if (DotBracketSymbol.isClosing(c)) {
      return DotBracketSymbol.OPENING.get(DotBracketSymbol.getOrder(c));
    }
    return '.';
  }

  private static int getOrder(final char c) {
    if (DotBracketSymbol.isOpening(c)) {
      return DotBracketSymbol.OPENING.indexOf(c);
    }
    if (DotBracketSymbol.isClosing(c)) {
      return DotBracketSymbol.CLOSING.indexOf(c);
    }
    return 0;
  }

  @Value.Parameter(order = 1)
  public abstract char sequence();

  @Value.Parameter(order = 2)
  public abstract char structure();

  @Value.Parameter(order = 3)
  public abstract int index();

  @Value.Auxiliary
  public abstract Optional<DotBracketSymbol> previous();

  @Value.Auxiliary
  public abstract Optional<DotBracketSymbol> next();

  @Value.Auxiliary
  public abstract Optional<DotBracketSymbol> pair();

  @Value.Auxiliary
  public abstract Optional<Boolean> isNonCanonical();

  public final boolean isMissing() {
    return structure() == '-';
  }

  public final boolean isPairing() {
    return isOpening() || isClosing();
  }

  public final boolean isOpening() {
    return DotBracketSymbol.isOpening(structure());
  }

  public final boolean isClosing() {
    return DotBracketSymbol.isClosing(structure());
  }

  public final int getOrder() {
    return DotBracketSymbol.getOrder(structure());
  }

  public final char getMatchingBracket() {
    return DotBracketSymbol.getMatchingBracket(structure());
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
