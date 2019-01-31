package pl.poznan.put.structure.secondary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DotBracketSymbol implements Comparable<DotBracketSymbol>, Serializable {
  private static final long serialVersionUID = 362493908870317368L;

  private static final List<Character> OPENING = new ArrayList<>();
  private static final List<Character> CLOSING = new ArrayList<>();

  static {
    for (final char c : "([{<ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
      DotBracketSymbol.OPENING.add(c);
    }
    for (final char c : ")]}>abcdefghijklmnopqrstuvwxyz".toCharArray()) {
      DotBracketSymbol.CLOSING.add(c);
    }
  }

  private final char sequence;
  private final char structure;
  private final int index;
  private DotBracketSymbol previous;
  private DotBracketSymbol next;
  private DotBracketSymbol pair;
  private boolean isNonCanonical;

  public DotBracketSymbol(final char sequence, final char structure, final int index) {
    super();
    this.sequence = sequence;
    this.structure = structure;
    this.index = index;
  }

  public static boolean isPairing(final char c) {
    return DotBracketSymbol.isOpening(c) || DotBracketSymbol.isClosing(c);
  }

  public static boolean isOpening(final char c) {
    return DotBracketSymbol.OPENING.contains(c);
  }

  public static boolean isClosing(final char c) {
    return DotBracketSymbol.CLOSING.contains(c);
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

  public static char getMatchingBracket(final char c) {
    if (DotBracketSymbol.isOpening(c)) {
      return DotBracketSymbol.CLOSING.get(DotBracketSymbol.getOrder(c));
    }
    if (DotBracketSymbol.isClosing(c)) {
      return DotBracketSymbol.OPENING.get(DotBracketSymbol.getOrder(c));
    }
    return '.';
  }

  public final char getSequence() {
    return sequence;
  }

  public final char getStructure() {
    return structure;
  }

  public final int getIndex() {
    return index;
  }

  public final DotBracketSymbol getPrevious() {
    return previous;
  }

  public final void setPrevious(final DotBracketSymbol previous) {
    this.previous = previous;
  }

  public final DotBracketSymbol getNext(final int count) {
    DotBracketSymbol symbol = this;
    for (int i = 1; (i < count) && (symbol != null); i++) {
      symbol = symbol.next;
    }
    return symbol;
  }

  public final DotBracketSymbol getNext() {
    return next;
  }

  public final void setNext(final DotBracketSymbol next) {
    this.next = next;
  }

  public final DotBracketSymbol getPair() {
    return pair;
  }

  public final void setPair(final DotBracketSymbol pair) {
    this.pair = pair;
  }

  // required to be named like this by Spring
  public final boolean getIsNonCanonical() {
    return isNonCanonical;
  }

  public final void setNonCanonical(final boolean isNonCanonical) {
    this.isNonCanonical = isNonCanonical;
  }

  public final boolean isMissing() {
    return structure == '-';
  }

  public final boolean isPairing() {
    return isOpening() || isClosing();
  }

  public final boolean isOpening() {
    return DotBracketSymbol.isOpening(structure);
  }

  public final boolean isClosing() {
    return DotBracketSymbol.isClosing(structure);
  }

  public final int getOrder() {
    return DotBracketSymbol.getOrder(structure);
  }

  public final char getMatchingBracket() {
    return DotBracketSymbol.getMatchingBracket(structure);
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + index;
    result = (prime * result) + sequence;
    result = (prime * result) + structure;
    return result;
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    final DotBracketSymbol other = (DotBracketSymbol) o;
    return (index == other.index) && (sequence == other.sequence) && (structure == other.structure);
  }

  @Override
  public final String toString() {
    return index + " " + sequence + ' ' + structure;
  }

  @Override
  public final int compareTo(final DotBracketSymbol t) {
    if (t == null) {
      throw new NullPointerException();
    }

    if (equals(t)) {
      return 0;
    }

    if (index < t.index) return -1;
    else return (index == t.index) ? 0 : 1;
  }
}
