package pl.poznan.put.circular;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;

import java.util.regex.Pattern;

/**
 * A class of measurements where one can distinguish a direction (i.e. [0..360) degrees)
 *
 * @author tzok
 */
public class Angle extends Circular {
  private static final Angle INVALID = new Angle(Double.NaN, ValueType.RADIANS);
  private static final Pattern DOT = Pattern.compile("[.]");
  private static final int MINUTES_IN_DAY = 24 * 60;

  public Angle(final double value, final ValueType valueType) {
    super(value, valueType);
  }

  public static void main(final String[] args) {
    for (int count = 100000; count < 1000000; count += 100000) {
      final double xs[] = new double[count];
      final double ys[] = new double[count];

      for (int i = 0; i < count; i++) {
        xs[i] = RandomUtils.nextDouble(0, 2 * FastMath.PI);
        ys[i] = RandomUtils.nextDouble(0, 2 * FastMath.PI);
      }

      System.out.println("Count: " + count);

      final StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      for (int i = 0; i < count; i++) {
        Angle.subtractByMinimum(xs[i], ys[i]);
        Angle.subtractByMinimum(ys[i], xs[i]);
      }
      stopWatch.stop();
      System.out.println("Angle.subtractByMinimum(x,y):   " + stopWatch);

      stopWatch.reset();
      stopWatch.start();
      for (int i = 0; i < count; i++) {
        Angle.subtractByAbsolutes(xs[i], ys[i]);
        Angle.subtractByAbsolutes(ys[i], xs[i]);
      }
      stopWatch.stop();
      System.out.println("Angle.subtractByAbsolutes(x,y): " + stopWatch);

      stopWatch.reset();
      stopWatch.start();
      for (int i = 0; i < count; i++) {
        Angle.subtractAsVectors(xs[i], ys[i]);
        Angle.subtractAsVectors(ys[i], xs[i]);
      }
      stopWatch.stop();
      System.out.println("Angle.subtractAsVectors(x,y):   " + stopWatch);
    }
  }

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
  public static Angle fromHourMinuteString(final String hourMinute) {
    final String[] split = Angle.DOT.split(hourMinute);

    if (split.length != 2) {
      throw new InvalidVectorFormatException(
          "Required format is HH.MM eg. 02.40. The input given was: " + hourMinute);
    }

    try {
      final int hours = Integer.parseInt(split[0]);
      int minutes = Integer.parseInt(split[1]);
      minutes += hours * 60;
      return new Angle(
          (MathUtils.TWO_PI * (double) minutes) / (double) Angle.MINUTES_IN_DAY, ValueType.RADIANS);
    } catch (final NumberFormatException e) {
      throw new InvalidVectorFormatException(
          "Required format is HH.MM eg. 02.40. The input given was: " + hourMinute, e);
    }
  }

  /**
   * Calculate angles' difference using formula min(|left - right|, 360 - |left - right|).
   *
   * @param left Minuend in radians.
   * @param right Subtrahend in radians.
   * @return The result of minuend - subtrahend in angular space.
   */
  public static double subtractByMinimum(final double left, final double right) {
    final double d = FastMath.abs(left - right);
    return FastMath.min(d, MathUtils.TWO_PI - d);
  }

  /**
   * Calculate angles' difference using formula: pi - |pi - |left - right||.
   *
   * @param left Minuend in radians.
   * @param right Subtrahend in radians.
   * @return The result of minuend - subtrahend in angular space.
   */
  public static double subtractByAbsolutes(final double left, final double right) {
    return FastMath.PI - FastMath.abs(FastMath.PI - FastMath.abs(left - right));
  }

  /**
   * Calculate angles' difference using formula acos(dot(left, right)).
   *
   * @param left Minuend in radians.
   * @param right Subtrahend in radians.
   * @return The result of minuend - subtrahend in angular space.
   */
  public static double subtractAsVectors(final double left, final double right) {
    double v = FastMath.sin(left) * FastMath.sin(right);
    v += FastMath.cos(left) * FastMath.cos(right);
    v = FastMath.min(1.0, v);
    v = FastMath.max(-1.0, v);
    return FastMath.acos(v);
  }

  /**
   * Return true if this instance is in range [begin; end). For example 45 degrees is between 30
   * degrees and 60 degrees. Also, 15 degrees is between -30 and 30 degrees.
   *
   * @param begin Beginning of the range of values.
   * @param end Ending of the range of values.
   * @return true if object is between [begin; end)
   */
  public final boolean isBetween(final Angle begin, final Angle end) {
    final double radians2PI = getRadians2PI();
    final double begin2PI = begin.getRadians2PI();
    final double end2PI = end.getRadians2PI();

    return (begin2PI < end2PI)
        ? ((radians2PI >= begin2PI) && (radians2PI < end2PI))
        : ((radians2PI >= begin2PI) || (radians2PI < end2PI));
  }

  /**
   * Multiply the angular value by a constant.
   *
   * @param v Multiplier.
   * @return Angular value multiplied.
   */
  public final Angle multiply(final double v) {
    return new Angle((getRadians() * v), ValueType.RADIANS);
  }

  /**
   * Subtract another angular value from this.
   *
   * @param other Another angular value.
   * @return Result of this - other in angular space.
   */
  public final Angle subtract(final Angle other) {
    return new Angle(
        Angle.subtractByAbsolutes(getRadians(), other.getRadians()), ValueType.RADIANS);
  }

  /**
   * Return an ordered difference between angles. It describes a rotation from one angle to another
   * one and is therefore in range [-180; 180) degrees.
   *
   * @param other The other angle which value should be subtracted from this one.
   * @return An ordered difference from first to second angle in range [-180; 180) degrees.
   */
  public final Angle orderedSubtract(final Angle other) {
    double d = getRadians() - other.getRadians();
    while (Precision.compareTo(d, -FastMath.PI, 1.0e-3) < 0) {
      d += MathUtils.TWO_PI;
    }
    while (Precision.compareTo(d, FastMath.PI, 1.0e-3) > 0) {
      d -= MathUtils.TWO_PI;
    }
    return new Angle(d, ValueType.RADIANS);
  }

  /**
   * Compute a useful distance in range [0; 2] between two angular values.
   *
   * @param other The other angle.
   * @return Value in range [0; 2] denoting distance between two angles.
   */
  public final double distance(final Angle other) {
    return 1 - FastMath.cos(getRadians() - other.getRadians());
  }
}
