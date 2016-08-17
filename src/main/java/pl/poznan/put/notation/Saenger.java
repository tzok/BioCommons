package pl.poznan.put.notation;

public enum Saenger {
    I,
    II,
    III,
    IV,
    V,
    VI,
    VII,
    VIII,
    IX,
    X,
    XI,
    XII,
    XIII,
    XIV,
    XV,
    XVI,
    XVII,
    XVIII,
    XIX,
    XX,
    XXI,
    XXII,
    XXIII,
    XXIV,
    XXV,
    XXVI,
    XXVII,
    XXVIII,
    UNKNOWN;

    public static Saenger fromString(String str) {
        for (Saenger s : Saenger.values()) {
            if (s.toString().equals(str)) {
                return s;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        if (this == UNKNOWN) {
            return "n/a";
        }
        return name();
    }

    public static Saenger fromOrdinal(int ordinal) {
        if (ordinal >= 1 && ordinal <= 28) {
            return Saenger.values()[ordinal];
        }
        return Saenger.UNKNOWN;
    }

    public boolean isCanonical() {
        return Saenger.isCanonical(this);
    }

    public static boolean isCanonical(Saenger s) {
        return s == XIX || s == XX || s == XXVIII;
    }
}
