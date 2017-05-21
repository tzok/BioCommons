package pl.poznan.put.rna;

import java.io.Serializable;
import java.util.Objects;

public class RNAInteractionType
        implements Serializable, Comparable<RNAInteractionType> {
    private static final long serialVersionUID = -3447319886413641277L;

    public static final RNAInteractionType BASE_BASE =
            new RNAInteractionType(RNAResidueComponentType.BASE,
                                   RNAResidueComponentType.BASE, true);
    public static final RNAInteractionType BASE_BASE_1H =
            new RNAInteractionType(RNAResidueComponentType.BASE,
                                   RNAResidueComponentType.BASE,
                                   "base - base (1H)");
    public static final RNAInteractionType BASE_PHOSPHATE =
            new RNAInteractionType(RNAResidueComponentType.BASE,
                                   RNAResidueComponentType.PHOSPHATE, false);
    public static final RNAInteractionType BASE_RIBOSE =
            new RNAInteractionType(RNAResidueComponentType.BASE,
                                   RNAResidueComponentType.RIBOSE, false);
    public static final RNAInteractionType SUGAR_SUGAR =
            new RNAInteractionType(RNAResidueComponentType.RIBOSE,
                                   RNAResidueComponentType.RIBOSE, false);
    public static final RNAInteractionType STACKING =
            new RNAInteractionType(RNAResidueComponentType.BASE,
                                   RNAResidueComponentType.BASE, "stacking");
    public static final RNAInteractionType OTHER =
            new RNAInteractionType(RNAResidueComponentType.UNKNOWN,
                                   RNAResidueComponentType.UNKNOWN, "other");

    private final RNAResidueComponentType left;
    private final RNAResidueComponentType right;
    private final boolean isPairing;
    private final String description;

    public RNAInteractionType(
            final RNAResidueComponentType left,
            final RNAResidueComponentType right, final boolean isPairing) {
        super();
        this.left = left;
        this.right = right;
        this.isPairing = isPairing;
        description =
                left.name().toLowerCase() + " - " + right.name().toLowerCase();
    }

    public RNAInteractionType(
            final RNAResidueComponentType left,
            final RNAResidueComponentType right, final String description) {
        super();
        this.left = left;
        this.right = right;
        isPairing = false;
        this.description = description;
    }

    public RNAResidueComponentType getLeft() {
        return left;
    }

    public RNAResidueComponentType getRight() {
        return right;
    }

    public boolean isPairing() {
        return isPairing;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((description == null) ? 0 : description
                .hashCode());
        result = (prime * result) + (isPairing ? 1231 : 1237);
        result = (prime * result) + ((left == null) ? 0 : left.hashCode());
        result = (prime * result) + ((right == null) ? 0 : right.hashCode());
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
        final RNAInteractionType other = (RNAInteractionType) o;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!Objects.equals(description, other.description)) {
            return false;
        }
        return (isPairing == other.isPairing) && (left == other.left) && (right
                                                                          ==
                                                                          other.right);
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public int compareTo(final RNAInteractionType t) {
        if (t == null) {
            throw new NullPointerException();
        }

        if (equals(t)) {
            return 0;
        }

        final int mine = getInternalValue();
        final int theirs = t.getInternalValue();
        return (mine < theirs) ? -1 : ((mine == theirs) ? 0 : 1);
    }

    /*
     * The internal value ranks fragments like this: base, sugar, phosphate and
     * rest. This allows to sort interactions in ascending order. The top will
     * be taken by pairing base-base interactions, then non-pairing, then
     * base-sugar, etc. Also, 'stacking' interactions should be the last
     */
    private int getInternalValue() {
        if (equals(RNAInteractionType.STACKING)) {
            return Integer.MAX_VALUE;
        }

        int value = 0;
        value += RNAInteractionType.getNucleotideFragmentInternalValue(left);
        value += RNAInteractionType.getNucleotideFragmentInternalValue(right);
        if (isPairing) {
            value = -value;
        }
        return value;
    }

    private static int getNucleotideFragmentInternalValue(
            final RNAResidueComponentType type) {
        switch (type) {
            case BASE:
                return 1;
            case RIBOSE:
                return 10;
            case PHOSPHATE:
                return 100;
            case UNKNOWN:
            default:
                return 1000;
        }
    }

    public RNAInteractionType invert() {
        return new RNAInteractionType(right, left, isPairing);
    }
}
