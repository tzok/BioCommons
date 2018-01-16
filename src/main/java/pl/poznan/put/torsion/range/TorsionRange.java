package pl.poznan.put.torsion.range;

import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.ValueType;

/**
 * Torsion angle ranges as defined in Saenger's "Principles...".
 */
public enum TorsionRange implements Range {
    SYN_CIS("sp", -30, 30),
    ANTI_TRANS("ap", 150, -150),
    SYNCLINAL_GAUCHE_PLUS("+sc", 30, 90),
    SYNCLINAL_GAUCHE_MINUS("-sc", -90, -30),
    ANTICLINAL_PLUS("+ac", 90, 150),
    ANTICLINAL_MINUS("-ac", -150, -90),
    INVALID("invalid", Double.NaN, Double.NaN);

    private static final RangeProvider PROVIDER = angle -> {
        if (angle.isValid()) {
            for (final TorsionRange torsionRange : TorsionRange.values()) {
                if (angle.isBetween(torsionRange.begin, torsionRange.end)) {
                    return torsionRange;
                }
            }
        }
        return TorsionRange.INVALID;
    };

    public static RangeProvider getProvider() {
        return TorsionRange.PROVIDER;
    }

    private final String displayName;
    private final Angle begin;
    private final Angle end;

    TorsionRange(final String displayName, final double begin,
                 final double end) {
        this.displayName = displayName;
        this.begin = new Angle(begin, ValueType.DEGREES);
        this.end = new Angle(end, ValueType.DEGREES);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Angle getBegin() {
        return begin;
    }

    @Override
    public Angle getEnd() {
        return end;
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
    @Override
    public RangeDifference compare(final Range other) {
        if (!(other instanceof TorsionRange)) {
            throw new IllegalArgumentException(
                    "A Range object can be compared only with other Range " +
                    "object");
        }

        if ((this == TorsionRange.INVALID) || (other == TorsionRange.INVALID)) {
            return RangeDifference.INVALID;
        }

        final int delta = (int) Math
                .round(begin.subtract(other.getBegin()).getDegrees360());
        return RangeDifference.fromValue(delta / 60);
    }
}
