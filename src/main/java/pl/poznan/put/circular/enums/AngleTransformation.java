package pl.poznan.put.circular.enums;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.math3.util.MathUtils;

/**
 * Different transformations used to display circular diagrams.
 */
public enum AngleTransformation {
    /** 0 degree is on top, 90 degree is on the right. */
    CLOCK,
    /** 0 degree is on the right, 90 degree is on top. */
    MATH;

    public double transform(final double radians) {
        switch (this) {
            case CLOCK:
                return (-radians + (Math.PI / 2)) % MathUtils.TWO_PI;
            case MATH:
                return radians;
            default:
                throw new NotImplementedException(
                        "Transformation not implemented for: " + this);
        }
    }
}
