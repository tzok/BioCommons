package pl.poznan.put.nucleic;

public class InteractionType {
    public static final InteractionType BASE_BASE = new InteractionType(NucleotideFragmentType.BASE, NucleotideFragmentType.BASE, true);
    public static final InteractionType BASE_BASE_NONPAIRING = new InteractionType(NucleotideFragmentType.BASE, NucleotideFragmentType.BASE, false);
    public static final InteractionType BASE_PHOSPHATE = new InteractionType(NucleotideFragmentType.BASE, NucleotideFragmentType.PHOSPHATE, false);
    public static final InteractionType BASE_SUGAR = new InteractionType(NucleotideFragmentType.BASE, NucleotideFragmentType.SUGAR, false);
    public static final InteractionType SUGAR_SUGAR = new InteractionType(NucleotideFragmentType.SUGAR, NucleotideFragmentType.SUGAR, false);
    public static final InteractionType STACKING = new InteractionType(BASE_BASE_NONPAIRING, "stacking");

    private final NucleotideFragmentType left;
    private final NucleotideFragmentType right;
    private final boolean isPairing;
    private final String description;

    public InteractionType(NucleotideFragmentType left, NucleotideFragmentType right, boolean isPairing) {
        super();
        this.left = left;
        this.right = right;
        this.isPairing = isPairing;
        description = isPairing ? "pairing" : "non-pairing";
    }

    private InteractionType(InteractionType existing, String description) {
        super();
        this.left = existing.left;
        this.right = existing.right;
        this.isPairing = existing.isPairing;
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
        return left + "-" + right + (isPairing ? "" : " (" + description + ")");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        if (isPairing != other.isPairing)
            return false;
        if (left != other.left)
            return false;
        if (right != other.right)
            return false;
        return true;
    }
}
