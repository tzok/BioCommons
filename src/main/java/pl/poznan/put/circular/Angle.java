package pl.poznan.put.circular;

import java.util.regex.Pattern;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement
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
   * Parse string in format HH.MM as a vector on a circular clock. For 'm' minutes after midnight,
   * the vector has value of '360 * m / (24 * 60)'.
   *
   * @param hourMinute String in format HH.MM.
   * @return A vector representation of time on a circular clock.
   * @throws InvalidVectorFormatException If the input string has an invalid format.
   * @throws InvalidCircularValueException If the input string is parsed to a value outside the
   *     range [0..360)
   */
  @NotNull
  public static Angle fromHourMinuteString(@NotNull final String hourMinute) {
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
  public final boolean isBetween(@NotNull final Angle begin, @NotNull final Angle end) {
    final double degrees360 = getDegrees360();
    final double begin360 = begin.getDegrees360();
    final double end360 = end.getDegrees360();

    return (begin360 < end360)
        ? ((degrees360 >= begin360) && (degrees360 < end360))
        : ((degrees360 >= begin360) || (degrees360 < end360));
  }

  @NotNull
  public final Angle multiply(final double v) {
    return new Angle((getRadians() * v) % MathUtils.TWO_PI, ValueType.RADIANS);
  }

  @NotNull
  public final Angle subtract(@NotNull final Angle other) {
    return new Angle(Angle.subtractByMinimum(getRadians(), other.getRadians()), ValueType.RADIANS);
  }

  public static double subtractByMinimum(final double left, final double right) {
    final double d = Math.abs(left - right);
    return Math.min(d, MathUtils.TWO_PI - d);
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
    v = Math.min(1, v);
    v = Math.max(-1, v);
    return FastMath.acos(v);
  }

  /**
   * Return an ordered difference between angles. It describes a rotation from one angle to another
   * one and is therefore in range [-180; 180) degrees.
   *
   * @param other The other angle which value should be subtracted from this one.
   * @return An ordered difference from first to second angle in range [-180; 180) degrees.
   */
  @NotNull
  public final Angle orderedSubtract(@NotNull final Angle other) {
    double d = getRadians() - other.getRadians();
    while (Precision.compareTo(d, -Math.PI, 1.0e-3) < 0) {
      d += 2.0 * Math.PI;
    }
    while (Precision.compareTo(d, Math.PI, 1.0e-3) > 0) {
      d -= 2.0 * Math.PI;
    }
    return new Angle(d, ValueType.RADIANS);
  }
}
