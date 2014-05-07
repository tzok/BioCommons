package pl.poznan.put.notations;

public enum Saenger {
    I, II, III, IV, V, VI, VII, VIII, IX, X, XI, XII, XIII, XIV, XV, XVI, XVII, XVIII, XIX, XX, XXI, XXII, XXIII, XXIV, XXV, XXVI, XXVII, XXVIII, UNKNOWN;

    public static Saenger fromString(String str) {
        for (Saenger s : Saenger.values()) {
            if (s.toString().equals(str)) {
                return s;
            }
        }
        return UNKNOWN;
    }

    public static boolean isCanonical(Saenger s) {
        return s == XIX || s == XX || s == XXVIII;
    }

    @Override
    public String toString() {
        if (this == UNKNOWN) {
            return "n/a";
        }
        return name();
    }
}
