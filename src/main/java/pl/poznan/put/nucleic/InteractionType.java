package pl.poznan.put.nucleic;

import java.io.Serializable;

public class InteractionType implements Serializable, Comparable<InteractionType> {
    public static final InteractionType BASE_BASE = new InteractionType(NucleotideFragmentType.BASE, NucleotideFragmentType.BASE, true);
    public static final InteractionType BASE_BASE_1H = new InteractionType(NucleotideFragmentType.BASE, NucleotideFragmentType.BASE, false, "base - base (1H)");
    public static final InteractionType BASE_PHOSPHATE = new InteractionType(NucleotideFragmentType.BASE, NucleotideFragmentType.PHOSPHATE, false);
    public static final InteractionType BASE_SUGAR = new InteractionType(NucleotideFragmentType.BASE, NucleotideFragmentType.SUGAR, false);
    public static final InteractionType SUGAR_SUGAR = new InteractionType(NucleotideFragmentType.SUGAR, NucleotideFragmentType.SUGAR, false);
    public static final InteractionType STACKING = new InteractionType(NucleotideFragmentType.BASE, NucleotideFragmentType.BASE, false, "stacking");
    public static final InteractionType OTHER = new InteractionType(NucleotideFragmentType.UNKNOWN, NucleotideFragmentType.UNKNOWN, false, "other");

    private final NucleotideFragmentType left;
    private final NucleotideFragmentType right;
    private final boolean isPairing;
    private final String description;

    public InteractionType(NucleotideFragmentType left, NucleotideFragmentType right, boolean isPairing) {
        super();
        this.left = left;
        this.right = right;
        this.isPairing = isPairing;
        description = left.name().toLowerCase() + " - " + right.name().toLowerCase();
    }

    public InteractionType(NucleotideFragmentType left, NucleotideFragmentType right, boolean isPairing, String description) {
        super();
        this.left = left;
        this.right = right;
        this.isPairing = isPairing;
        this.description = description;
    }

    public NucleotideFragmentType getLeft() {
        return left;
    }

    public NucleotideFragmentType getRight() {
        return right;
    }

    public boolean isPairing() {
        return isPairing;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + (isPairing ? 1231 : 1237);
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InteractionType other = (InteractionType) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (isPairing != other.isPairing)
            return false;
        if (left != other.left)
            return false;
        if (right != other.right)
            return false;
        return true;
    }

    @Override
    public int compareTo(InteractionType o) {
        if (equals(o)) {
            return 0;
        }

        return new Integer(getInternalValue()).compareTo(new Integer(o.getInternalValue()));
    }

    /*
     * The internal value ranks fragments like this: base, sugar, phosphate and
     * rest. This allows to sort interactions in ascending order. The top will
     * be taken by pairing base-base interactions, then non-pairing, then
     * base-sugar, etc. Also, 'stacking' interactions should be the last
     */
    private int getInternalValue() {
        if (this.equals(STACKING)) {
            return Integer.MAX_VALUE;
        }

        int value = 0;
        value += getNucleotideFragmentInternalValue(left);
        value += getNucleotideFragmentInternalValue(right);
        if (isPairing) {
            value = -value;
        }
        return value;
    }

    private static int getNucleotideFragmentInternalValue(NucleotideFragmentType type) {
        switch (type) {
        case BASE:
            return 1;
        case SUGAR:
            return 10;
        case PHOSPHATE:
            return 100;
        case UNKNOWN:
        default:
            return 1000;
        }
    }
}
