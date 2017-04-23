package pl.poznan.put.circular.enums;

/**
 * Type of value passed as regular double.
 */
public enum ValueType {
    DEGREES,
    RADIANS;

    public double toRadians(final double value) {
        if (this == ValueType.DEGREES) {
            return Math.toRadians(value);
        } else if (this == ValueType.RADIANS) {
            return value;
        } else {
            throw new IllegalArgumentException(
                    "Only degrees and radians are handled");
        }
    }
}
