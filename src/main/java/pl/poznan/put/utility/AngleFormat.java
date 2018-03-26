package pl.poznan.put.utility;

import org.apache.commons.math3.util.Precision;
import pl.poznan.put.constant.Unicode;

public final class AngleFormat {
  /**
   * Format an angle value given in radians. The method detects NaN and infinity. Values is rounded
   * to the nearest degree.
   *
   * @param radians Input value in radians to be formatted.
   * @return A {@link String} with the value of input in degrees and a unicode degree symbol.
   */
  public static String degreesRoundedToOne(final double radians) {
    if (Double.isNaN(radians)) {
      return "NaN";
    }
    if (Double.isInfinite(radians)) {
      return Unicode.INFINITY;
    }
    if (Precision.equals(radians, 0)) {
      return "0";
    }

    final double degrees = Math.toDegrees(radians);
    final long rounded = Math.round(degrees);
    return Long.toString(rounded) + Unicode.DEGREE;
  }

  /**
   * Format an angle value given in radians. The method detects NaN and infinity. Value in degrees
   * is displayed with two digits after comma.
   *
   * @param radians Input value in radians to be formatted.
   * @return A {@link String} with the value of input in degrees (two digits after comma precision)
   *     and a unicode degree symbol.
   */
  public static String degreesRoundedToHundredth(final double radians) {
    if (Double.isNaN(radians)) {
      return "NaN";
    }
    if (Double.isInfinite(radians)) {
      return Unicode.INFINITY;
    }
    if (Precision.equals(radians, 0)) {
      return "0";
    }

    final double degrees = Math.toDegrees(radians);
    return TwoDigitsAfterDotNumberFormat.formatDouble(degrees) + Unicode.DEGREE;
  }

  /**
   * Format an angle value given in radians. The method uses no rounding and displays raw result of
   * {@link Double#toString()}.
   *
   * @param radians Input value in radians to be formatted.
   * @return A {@link String} with the value of input in degrees.
   */
  public static String degrees(final double radians) {
    final double degrees = Math.toDegrees(radians);
    return Double.toString(degrees);
  }

  private AngleFormat() {
    super();
  }
}
