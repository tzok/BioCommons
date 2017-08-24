package pl.poznan.put.circular.enums;

import pl.poznan.put.circular.Angle;

/**
 * Torsion angle ranges as defined in Saenger's "Principles...".
 */
public enum Range {
    SYN_CIS("sp", -30, 30),
    ANTI_TRANS("ap", 150, -150),
    SYNCLINAL_GAUCHE_PLUS("+sc", 30, 90),
    SYNCLINAL_GAUCHE_MINUS("-sc", -90, -30),
    ANTICLINAL_PLUS("+ac", 90, 150),
    ANTICLINAL_MINUS("-ac", -150, -90),
    INVALID("invalid", Double.NaN, Double.NaN);

    private final String displayName;
    private final Angle begin;
    private final Angle end;

    Range(final String displayName, final double begin, final double end) {
        this.displayName = displayName;
        this.begin = new Angle(begin, ValueType.DEGREES);
        this.end = new Angle(end, ValueType.DEGREES);
    }

    public String getDisplayName() {
        return displayName;
    }

    public Angle getBegin() {
        return begin;
    }

    public Angle getEnd() {
        return end;
    }

    public static Range fromAngle(final Angle angle) {
        if (!angle.isValid()) {
            return Range.INVALID;
        }

        final double degrees360 = angle.getDegrees360();

        for (final Range range : Range.values()) {
            final double begin360 = range.begin.getDegrees360();
            final double end360 = range.end.getDegrees360();

            if (begin360 < end360) {
                if ((degrees360 >= begin360) && (degrees360 < end360)) {
                    return range;
                }
            } else {
                if ((degrees360 >= begin360) || (degrees360 < end360)) {
                    return range;
                }
            }
        }

        throw new IllegalArgumentException(
                "Invalid input value: " + degrees360);
    }

    /**
     * Calculate difference between two angle ranges. It will be either 0
     * (equal), 1 (neighbour), 2 (next to neighbour) or 3 (opposite). Because
     * each range is exactly 60 degree wide, then difference between beginnings
     * is also always a multiple of 60.
     *
     * @param other An object to compare to.
     * @return RangeDifference object.
     */
    public RangeDifference difference(final Range other) {
        final int delta =
                (int) Math.round(begin.subtract(other.begin).getDegrees360());
        return RangeDifference.fromValue(delta / 60);
    }
}
