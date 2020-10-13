package pl.poznan.put.circular;

import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import org.immutables.value.Value;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.regex.Pattern;

/** A measurement for which one can distinguish a direction (i.e. [0..360) degrees) */
@Value.Immutable
public abstract class Angle implements Comparable<Angle> {
  private static final Pattern DOT = Pattern.compile("[.]");
  private static final int MINUTES_IN_DAY = 24 * 60;

  /**
   * Calculates angle ABC.
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
    return ImmutableAngle.of(Vector3D.angle(vectorAB, vectorCB));
  }

  /**
   * Calculates torsion angle given four points. Uses cos^-1 and a check for pseudovector.
   *
   * @param a1 Atom 1.
   * @param a2 Atom 2.
   * @param a3 Atom 3.
   * @param a4 Atom 4.
   * @return A torsion angle (rotation around vector B-C).
   */
  public static Angle torsionAngleByAcos(
      final Vector3D a1, final Vector3D a2, final Vector3D a3, final Vector3D a4) {
    final Vector3D d1 = a1.subtract(a2);
    final Vector3D d2 = a2.subtract(a3);
    final Vector3D d3 = a3.subtract(a4);

    final Vector3D u1 = d1.crossProduct(d2);
    final Vector3D u2 = d2.crossProduct(d3);

    final double ctor = u1.dotProduct(u2) / FastMath.sqrt(u1.dotProduct(u1) * u2.dotProduct(u2));
    final double torp = FastMath.acos(ctor < -1.0 ? -1.0 : Math.min(ctor, 1.0));
    return ImmutableAngle.of(u1.dotProduct(u2.crossProduct(d2)) >= 0 ? torp : -torp);
  }

  /**
   * Calculates torsion angle given four points. Uses atan2 method.
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
    return ImmutableAngle.of(FastMath.atan2(tmp3.dotProduct(tmp2), tmp1.dotProduct(tmp2)));
  }

  /**
   * Parses a string in format HH.MM as a vector on a circular clock. For 'm' minutes after
   * midnight, the vector has value of '360 * m / (24 * 60)'.
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
      return ImmutableAngle.of(
          (MathUtils.TWO_PI * (double) minutes) / (double) Angle.MINUTES_IN_DAY);
    } catch (final NumberFormatException e) {
      throw new InvalidVectorFormatException(
          "Required format is HH.MM eg. 02.40. The input given was: " + hourMinute, e);
    }
  }

  /**
   * Calculates angles' difference using formula min(|left - right|, 360 - |left - right|).
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
   * Calculates angles' difference using formula acos(dot(left, right)).
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
   * Calculates angles' difference using formula: pi - |pi - |left - right||.
   *
   * @param left Minuend in radians.
   * @param right Subtrahend in radians.
   * @return The result of minuend - subtrahend in angular space.
   */
  public static double subtractByAbsolutes(final double left, final double right) {
    return FastMath.PI - FastMath.abs(FastMath.PI - FastMath.abs(left - right));
  }

  /** @return Value in radians in range (-pi; pi]. */
  @Value.Parameter(order = 1)
  public abstract double radians();

  /** @return Value in degrees in range (-180; 180]. */
  public final double degrees() {
    return FastMath.toDegrees(radians());
  }

  /** @return Value in degrees in range [0; 360). */
  public final double degrees360() {
    return FastMath.toDegrees(radians2PI());
  }

  /** @return Value in radians in range [0; 2pi). */
  public final double radians2PI() {
    return (radians() < 0.0) ? (radians() + MathUtils.TWO_PI) : radians();
  }

  /** @return True if value is not NaN. */
  public final boolean isValid() {
    return !Double.isNaN(radians());
  }

  /**
   * Return strue if this instance is in range [begin; end). For example 45 degrees is between 30
   * degrees and 60 degrees. Also, 15 degrees is between -30 and 30 degrees.
   *
   * @param begin Beginning of the range of values.
   * @param end Ending of the range of values.
   * @return true if object is between [begin; end)
   */
  public final boolean isBetween(final Angle begin, final Angle end) {
    final double radians2PI = radians2PI();
    final double begin2PI = begin.radians2PI();
    final double end2PI = end.radians2PI();

    return (begin2PI < end2PI)
        ? ((radians2PI >= begin2PI) && (radians2PI < end2PI))
        : ((radians2PI >= begin2PI) || (radians2PI < end2PI));
  }

  /**
   * Multiplies the angular value by a constant.
   *
   * @param v Multiplier.
   * @return Angular value multiplied.
   */
  public final Angle multiply(final double v) {
    return ImmutableAngle.of((radians() * v));
  }

  /**
   * Subtracts another angular value from this.
   *
   * @param other Another angular value.
   * @return Result of this - other in angular space.
   */
  public final Angle subtract(final Angle other) {
    return ImmutableAngle.of(Angle.subtractByAbsolutes(radians(), other.radians()));
  }

  /**
   * Returns an ordered difference between angles. It describes a rotation from one angle to another
   * one and is therefore in range [-180; 180) degrees.
   *
   * @param other The other angle which value should be subtracted from this one.
   * @return An ordered difference from first to second angle in range [-180; 180) degrees.
   */
  public final Angle orderedSubtract(final Angle other) {
    double d = radians() - other.radians();
    while (Precision.compareTo(d, -FastMath.PI, 1.0e-3) < 0) {
      d += MathUtils.TWO_PI;
    }
    while (Precision.compareTo(d, FastMath.PI, 1.0e-3) > 0) {
      d -= MathUtils.TWO_PI;
    }
    return ImmutableAngle.of(d);
  }

  /**
   * Computes a useful distance in range [0; 2] between two angular values.
   *
   * @param other The other angle.
   * @return Value in range [0; 2] denoting distance between two angles.
   */
  public final double distance(final Angle other) {
    return 1 - FastMath.cos(radians() - other.radians());
  }

  @Override
  public final int compareTo(final Angle t) {
    return Double.compare(radians(), t.radians());
  }

  @Override
  public final boolean equals(@Nullable final Object obj) {
    if (this == obj) return true;
    return obj instanceof Angle && Precision.equals(radians(), ((Angle) obj).radians(), 1.0e-3);
  }

  @Override
  public final String toString() {
    return String.format(Locale.US, "Angle{degrees=%.2f}", degrees());
  }

  @Value.Check
  protected Angle normalize() {
    double value = radians();

    if (Double.isNaN(value)) {
      return this;
    }

    Validate.finite(value);

    if ((value > -FastMath.PI && value <= FastMath.PI)) {
      return this;
    }

    while (value <= -FastMath.PI) {
      value += MathUtils.TWO_PI;
    }
    while (value > FastMath.PI) {
      value -= MathUtils.TWO_PI;
    }
    return ImmutableAngle.of(value);
  }
}
