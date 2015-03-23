package pl.poznan.put.structure.secondary;

public enum HelixOrigin {
    UNKNOWN, TRUE, FALSE;

    @Override
    public String toString() {
        switch (this) {
        case FALSE:
            return "false";
        case TRUE:
            return "true";
        default:
        case UNKNOWN:
            return "unknown";
        }
    }

    public String toOneLetter() {
        switch (this) {
        case FALSE:
            return "N";
        case TRUE:
            return "Y";
        case UNKNOWN:
        default:
            return "?";
        }
    }
}
