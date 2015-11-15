package pl.poznan.put.structure.secondary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DotBracketSymbol implements Comparable<DotBracketSymbol>, Serializable {
    private static final List<Character> OPENING = new ArrayList<Character>();
    private static final List<Character> CLOSING = new ArrayList<Character>();

    static {
        for (char c : "([{<ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
            DotBracketSymbol.OPENING.add(c);
        }
        for (char c : ")]}>abcdefghijklmnopqrstuvwxyz".toCharArray()) {
            DotBracketSymbol.CLOSING.add(c);
        }
    }

    public static boolean isOpening(char c) {
        return DotBracketSymbol.OPENING.contains(c);
    }

    public static boolean isClosing(char c) {
        return DotBracketSymbol.CLOSING.contains(c);
    }

    public static boolean isPairing(char c) {
        return DotBracketSymbol.isOpening(c) || DotBracketSymbol.isClosing(c);
    }

    private final char sequence;
    private final char structure;
    private final int index;
    private DotBracketSymbol previous;
    private DotBracketSymbol next;
    private DotBracketSymbol pair;
    private boolean isNonCanonical = false;

    public DotBracketSymbol(char sequence, char structure, int index) {
        super();
        this.sequence = sequence;
        this.structure = structure;
        this.index = index;
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

    public void setPrevious(DotBracketSymbol previous) {
        this.previous = previous;
    }

    public DotBracketSymbol getNext(int count) {
        DotBracketSymbol symbol = this;
        for (int i = 1; i < count && symbol != null; i++) {
            symbol = symbol.next;
        }
        return symbol;
    }

    public DotBracketSymbol getNext() {
        return next;
    }

    public void setNext(DotBracketSymbol next) {
        this.next = next;
    }

    public DotBracketSymbol getPair() {
        return pair;
    }

    public void setPair(DotBracketSymbol pair) {
        this.pair = pair;
    }

    // required to be named like this by Spring
    public boolean getIsNonCanonical() {
        return isNonCanonical;
    }

    public void setNonCanonical(boolean isNonCanonical) {
        this.isNonCanonical = isNonCanonical;
    }

    public boolean isMissing() {
        return structure == '-';
    }

    public boolean isOpening() {
        return DotBracketSymbol.isOpening(structure);
    }

    public boolean isClosing() {
        return DotBracketSymbol.isClosing(structure);
    }

    public boolean isPairing() {
        return isOpening() || isClosing();
    }

    public int getOrder() {
        if (isOpening()) {
            return DotBracketSymbol.OPENING.indexOf(structure);
        } else if (isClosing()) {
            return DotBracketSymbol.CLOSING.indexOf(structure);
        }
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + sequence;
        result = prime * result + structure;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DotBracketSymbol other = (DotBracketSymbol) obj;
        if (index != other.index) {
            return false;
        }
        return sequence == other.sequence && structure == other.structure;
    }

    @Override
    public int compareTo(DotBracketSymbol o) {
        if (equals(o)) {
            return 0;
        }

        return index < o.index ? -1 : (index == o.index ? 0 : 1);
    }

    @Override
    public String toString() {
        return index + " " + sequence + " " + structure;
    }
}
