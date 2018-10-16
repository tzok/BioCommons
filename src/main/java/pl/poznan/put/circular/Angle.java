package pl.poznan.put.circular;

import java.util.regex.Pattern;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;

/**
 * A class of measurements where one can distinguish a direction (i.e. [0..360) degrees)
 *
 * @author tzok
 */
public class Angle extends Circular {
  private static final long serialVersionUID = 7888250116422842913L;

  private static final Angle INVALID = new Angle(Double.NaN, ValueType.RADIANS);
  private static final Pattern DOT = Pattern.compile("[.]");
  private static final int MINUTES_IN_DAY = 24 * 60;

  public Angle(final double value, final ValueType valueType) {
    super(value, valueType);
  }

  @Contract(pure = true)
  public static Angle invalidInstance() {
    return Angle.INVALID;
  }

  /**
   * Calculate angle ABC.
   *
   * @param coordA Coordinate of point A.
   * @param coordB Coordinate of point B.
   * @param coordC Coordinate of point C.
   * @return An angle between points A, B and C.
   */
  public static Angle betweenPoints(
      final Vector3D coordA, final Vector3D coordB, final Vector3D coordC) {
    final Vector3D vectorAB = coordB.subtract(coordA);
    final Vector3D vectorCB = coordB.subtract(coordC);
    return new Angle(Vector3D.angle(vectorAB, vectorCB), ValueType.RADIANS);
  }

  /**
   * Calculate torsion angle given four points.
   *
   * @param coordA Coordinate of point A.
   * @param coordB Coordinate of point B.
   * @param coordC Coordinate of point C.
   * @param coordD Coordinate of point D.
   * @return A torsion angle (rotation around vector B-C).
   */
  public static Angle torsionAngle(
      final Vector3D coordA, final Vector3D coordB, final Vector3D coordC, final Vector3D coordD) {
    final Vector3D v1 = coordB.subtract(coordA);
    final Vector3D v2 = coordC.subtract(coordB);
    final Vector3D v3 = coordD.subtract(coordC);

    final Vector3D tmp1 = v1.crossProduct(v2);
    final Vector3D tmp2 = v2.crossProduct(v3);
    final Vector3D tmp3 = v1.scalarMultiply(v2.getNorm());
    return new Angle(
        FastMath.atan2(tmp3.dotProduct(tmp2), tmp1.dotProduct(tmp2)), ValueType.RADIANS);
  }

  /**
   * Parse string in format HH.MM as a vector on a circular clock. For 'm' minutes after midnight,
   * the vector has value of '360 * m / (24 * 60)'.
   *
   * @param hourMinute String in format HH.MM.
   * @return A vector representation of time on a circular clock.
   * @throws InvalidVectorFormatException If the input string has an invalid format.
   * @throws InvalidCircularValueException If the input string is parsed to a value outside the
   *     range [0..360)
   */
  public static @NotNull Angle fromHourMinuteString(final @NotNull String hourMinute) {
    final String[] split = Angle.DOT.split(hourMinute);

    if (split.length != 2) {
      throw new InvalidVectorFormatException(
          "Required format is HH.MM eg. 02.40. The input given was: " + hourMinute);
    }

    try {
      final int hours = Integer.parseInt(split[0]);
      int minutes = Integer.parseInt(split[1]);
      minutes += hours * 60;
      return new Angle((MathUtils.TWO_PI * minutes) / Angle.MINUTES_IN_DAY, ValueType.RADIANS);
    } catch (final NumberFormatException e) {
      throw new InvalidVectorFormatException(
          "Required format is HH.MM eg. 02.40. The input given was: " + hourMinute, e);
    }
  }

  /**
   * Return true if this instance is in range [begin; end). For example 45 degrees is between 30
   * degrees and 60 degrees. Also, 15 degrees is between -30 and 30 degrees.
   *
   * @param begin Beginning of the range of values.
   * @param end Ending of the range of values.
   * @return true if object is between [begin; end)
   */
  public final boolean isBetween(final @NotNull Angle begin, final @NotNull Angle end) {
    final double degrees360 = getDegrees360();
    final double begin360 = begin.getDegrees360();
    final double end360 = end.getDegrees360();

    return (begin360 < end360)
        ? ((degrees360 >= begin360) && (degrees360 < end360))
        : ((degrees360 >= begin360) || (degrees360 < end360));
  }

  public final @NotNull Angle multiply(final double v) {
    return new Angle((getRadians() * v) % MathUtils.TWO_PI, ValueType.RADIANS);
  }

  public final @NotNull Angle subtract(final @NotNull Angle other) {
    return new Angle(Angle.subtractByMinimum(getRadians(), other.getRadians()), ValueType.RADIANS);
  }

  public static double subtractByMinimum(final double left, final double right) {
    final double d = FastMath.abs(left - right);
    return FastMath.min(d, MathUtils.TWO_PI - d);
  }

  /**
   * Calculate angles' difference using formula acos(dot(left, right)).
   *
   * @param left Minuend.
   * @param right Subtrahend.
   * @return The result of minuend - subtrahend in angular space.
   */
  public static double subtractAsVectors(final double left, final double right) {
    double v = FastMath.sin(left) * FastMath.sin(right);
    v += FastMath.cos(left) * FastMath.cos(right);
    v = FastMath.min(1, v);
    v = FastMath.max(-1, v);
    return FastMath.acos(v);
  }

  /**
   * Return an ordered difference between angles. It describes a rotation from one angle to another
   * one and is therefore in range [-180; 180) degrees.
   *
   * @param other The other angle which value should be subtracted from this one.
   * @return An ordered difference from first to second angle in range [-180; 180) degrees.
   */
  public final @NotNull Angle orderedSubtract(final @NotNull Angle other) {
    double d = getRadians() - other.getRadians();
    while (Precision.compareTo(d, -FastMath.PI, 1.0e-3) < 0) {
      d += MathUtils.TWO_PI;
    }
    while (Precision.compareTo(d, FastMath.PI, 1.0e-3) > 0) {
      d -= MathUtils.TWO_PI;
    }
    return new Angle(d, ValueType.RADIANS);
  }
}
