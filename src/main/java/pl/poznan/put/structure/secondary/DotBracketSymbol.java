package pl.poznan.put.structure.secondary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DotBracketSymbol implements Comparable<DotBracketSymbol>, Serializable {
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

  public static int getOrder(final char c) {
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

  public char getSequence() {
    return sequence;
  }

  public char getStructure() {
    return structure;
  }

  public int getIndex() {
    return index;
  }

  public DotBracketSymbol getPrevious() {
    return previous;
  }

  public void setPrevious(final DotBracketSymbol previous) {
    this.previous = previous;
  }

  public DotBracketSymbol getNext(final int count) {
    DotBracketSymbol symbol = this;
    for (int i = 1; (i < count) && (symbol != null); i++) {
      symbol = symbol.next;
    }
    return symbol;
  }

  public DotBracketSymbol getNext() {
    return next;
  }

  public void setNext(final DotBracketSymbol next) {
    this.next = next;
  }

  public DotBracketSymbol getPair() {
    return pair;
  }

  public void setPair(final DotBracketSymbol pair) {
    this.pair = pair;
  }

  // required to be named like this by Spring
  public boolean getIsNonCanonical() {
    return isNonCanonical;
  }

  public void setNonCanonical(final boolean isNonCanonical) {
    this.isNonCanonical = isNonCanonical;
  }

  public boolean isMissing() {
    return structure == '-';
  }

  public boolean isPairing() {
    return isOpening() || isClosing();
  }

  public boolean isOpening() {
    return DotBracketSymbol.isOpening(structure);
  }

  public boolean isClosing() {
    return DotBracketSymbol.isClosing(structure);
  }

  public int getOrder() {
    return DotBracketSymbol.getOrder(structure);
  }

  public char getMatchingBracket() {
    return DotBracketSymbol.getMatchingBracket(structure);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + index;
    result = (prime * result) + sequence;
    result = (prime * result) + structure;
    return result;
  }

  @Override
  public boolean equals(final Object o) {
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
  public String toString() {
    return index + " " + sequence + ' ' + structure;
  }

  @Override
  public int compareTo(final DotBracketSymbol t) {
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
