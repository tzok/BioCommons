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
    ANTICLINAL_MINUS("-ac", -150, -90);

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
        double degrees360 = angle.getDegrees360();

        for (final Range range : Range.values()) {
            double begin360 = range.begin.getDegrees360();
            double end360 = range.end.getDegrees360();

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
