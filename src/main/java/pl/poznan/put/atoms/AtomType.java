package pl.poznan.put.atoms;

public enum AtomType {
    C(true), H(false), N(true), O(true), P(true), S(true);

    private final boolean isHeavy;

    AtomType(boolean isHeavy) {
        this.isHeavy = isHeavy;
    }

    public boolean isHeavy() {
        return isHeavy;
    }
}
