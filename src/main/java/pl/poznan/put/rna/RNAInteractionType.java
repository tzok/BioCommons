package pl.poznan.put.rna;

import java.io.Serializable;

public class RNAInteractionType implements Serializable, Comparable<RNAInteractionType> {
    public static final RNAInteractionType BASE_BASE = new RNAInteractionType(RNAResidueComponentType.BASE, RNAResidueComponentType.BASE, true);
    public static final RNAInteractionType BASE_BASE_1H = new RNAInteractionType(RNAResidueComponentType.BASE, RNAResidueComponentType.BASE, false, "base - base (1H)");
    public static final RNAInteractionType BASE_PHOSPHATE = new RNAInteractionType(RNAResidueComponentType.BASE, RNAResidueComponentType.PHOSPHATE, false);
    public static final RNAInteractionType BASE_SUGAR = new RNAInteractionType(RNAResidueComponentType.BASE, RNAResidueComponentType.SUGAR, false);
    public static final RNAInteractionType SUGAR_SUGAR = new RNAInteractionType(RNAResidueComponentType.SUGAR, RNAResidueComponentType.SUGAR, false);
    public static final RNAInteractionType STACKING = new RNAInteractionType(RNAResidueComponentType.BASE, RNAResidueComponentType.BASE, false, "stacking");
    public static final RNAInteractionType OTHER = new RNAInteractionType(RNAResidueComponentType.UNKNOWN, RNAResidueComponentType.UNKNOWN, false, "other");

    private final RNAResidueComponentType left;
    private final RNAResidueComponentType right;
    private final boolean isPairing;
    private final String description;

    public RNAInteractionType(RNAResidueComponentType left, RNAResidueComponentType right, boolean isPairing) {
        super();
        this.left = left;
        this.right = right;
        this.isPairing = isPairing;
        description = left.name().toLowerCase() + " - " + right.name().toLowerCase();
    }

    public RNAInteractionType(RNAResidueComponentType left, RNAResidueComponentType right, boolean isPairing, String description) {
        super();
        this.left = left;
        this.right = right;
        this.isPairing = isPairing;
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
    public String toString() {
        return description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (description == null ? 0 : description.hashCode());
        result = prime * result + (isPairing ? 1231 : 1237);
        result = prime * result + (left == null ? 0 : left.hashCode());
        result = prime * result + (right == null ? 0 : right.hashCode());
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
        RNAInteractionType other = (RNAInteractionType) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (isPairing != other.isPairing) {
            return false;
        }
        if (left != other.left) {
            return false;
        }
        if (right != other.right) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(RNAInteractionType o) {
        if (equals(o)) {
            return 0;
        }

        int mine = getInternalValue();
        int theirs = o.getInternalValue();
        return mine < theirs ? -1 : (mine == theirs ? 0 : 1);
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

    private static int getNucleotideFragmentInternalValue(RNAResidueComponentType type) {
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
