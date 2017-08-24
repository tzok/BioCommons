package pl.poznan.put.circular.enums;

import pl.poznan.put.circular.Angle;

/**
 * Torsion angle ranges for CHI as defined in Saenger's "Principles...".
 */
public enum ChiRange {
    SYN("syn", -75, 105),
    ANTI("anti", 105, -75),
    INVALID("invalid", Double.NaN, Double.NaN);

    private final String displayName;
    private final Angle begin;
    private final Angle end;

    ChiRange(final String displayName, final double begin, final double end) {
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

    public static ChiRange fromAngle(final Angle angle) {
        if (!angle.isValid()) {
            return ChiRange.INVALID;
        }

        final double degrees360 = angle.getDegrees360();

        for (final ChiRange range : ChiRange.values()) {
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

        throw new IllegalArgumentException("Invalid input value: " + angle);
    }
}
