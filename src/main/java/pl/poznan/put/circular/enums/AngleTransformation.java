package pl.poznan.put.circular.enums;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.math3.util.MathUtils;

/** Different transformations used to display circular diagrams. */
public enum AngleTransformation {
  /** 0 degree is on top, 90 degree is on the right. */
  CLOCK,
  /** 0 degree is on the right, 90 degree is on top. */
  MATH;

  /**
   * Transform an angle value in radians to represent it on circle. In {@link
   * AngleTransformation#MATH} the value is returned as it is, so value 0 is on X-axis and
   * increasing values are drawn counter-clockwise. In {@link AngleTransformation#CLOCK} the value
   * is transformed such that 0 is at the top of the circle and the increasing values are drawn
   * clockwise.
   *
   * @param radians Value in radians to be transformed.
   * @return A transformed value in radians to be drawn on a circle.
   */
  public double transform(final double radians) {
    switch (this) {
      case CLOCK:
        return (-radians + (Math.PI / 2.0)) % MathUtils.TWO_PI;
      case MATH:
        return radians;
      default:
        throw new NotImplementedException("Transformation not implemented for: " + this);
    }
  }
}
