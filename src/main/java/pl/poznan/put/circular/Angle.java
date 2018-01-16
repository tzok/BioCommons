package pl.poznan.put.circular;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class of measurements where one can distinguish a direction (i.e. [0..360)
 * degrees)
 *
 * @author tzok
 */
@XmlRootElement
public class Angle extends Circular {
    private static final long serialVersionUID = 7888250116422842913L;

    private static final Angle INVALID =
            new Angle(Double.NaN, ValueType.RADIANS);
    private static final int MINUTES_IN_DAY = 24 * 60;

    public Angle(final double value, final ValueType valueType) {
        super(value, valueType);
    }

    public static Angle invalidInstance() {
        return Angle.INVALID;
    }

    /**
     * Parse string in format HH.MM as a vector on a circular clock. For 'm'
     * minutes after midnight, the vector has value of '360 * m / (24 * 60)'.
     *
     * @param hourMinute String in format HH.MM.
     * @return A vector representation of time on a circular clock.
     * @throws InvalidVectorFormatException  If the input string has an invalid
     *                                       format.
     * @throws InvalidCircularValueException If the input string is parsed to a
     *                                       value outside the range [0..360)
     */
    public static Angle fromHourMinuteString(final String hourMinute) {
        final String[] split = hourMinute.split("[.]");

        if (split.length != 2) {
            throw new InvalidVectorFormatException(
                    "Required format is HH.MM eg. 02.40. The input given was:" +
                    " " + hourMinute);
        }

        try {
            final int hours = Integer.parseInt(split[0]);
            int minutes = Integer.parseInt(split[1]);
            minutes += hours * 60;
            return new Angle(
                    (MathUtils.TWO_PI * minutes) / Angle.MINUTES_IN_DAY,
                    ValueType.RADIANS);
        } catch (final NumberFormatException e) {
            throw new InvalidVectorFormatException(
                    "Required format is HH.MM eg. 02.40. The input given was:" +
                    " " + hourMinute, e);
        }
    }

    /**
     * Return true if this instance is in range [begin; end). For example 45
     * degrees is between 30 degrees and 60 degrees. Also, 15 degrees is
     * between -30 and 30 degrees.
     *
     * @param begin Beginning of the range of values.
     * @param end   Ending of the range of values.
     * @return true if object is between [begin; end)
     */
    public final boolean isBetween(final Angle begin, final Angle end) {
        final double degrees360 = getDegrees360();
        final double begin360 = begin.getDegrees360();
        final double end360 = end.getDegrees360();

        if (begin360 < end360) {
            return (degrees360 >= begin360) && (degrees360 < end360);
        } else {
            return (degrees360 >= begin360) || (degrees360 < end360);
        }

    }

    public final Angle multiply(final double n) {
        return new Angle((getRadians() * n) % MathUtils.TWO_PI,
                         ValueType.RADIANS);
    }

    public final Angle subtract(final Angle other) {
        return new Angle(
                Angle.subtractByMinimum(getRadians(), other.getRadians()),
                ValueType.RADIANS);
    }

    public static double subtractByMinimum(final double left,
                                           final double right) {
        final double d = Math.abs(left - right);
        return Math.min(d, MathUtils.TWO_PI - d);
    }

    public static double subtractAsVectors(final double left,
                                           final double right) {
        double v = FastMath.sin(left) * FastMath.sin(right);
        v += FastMath.cos(left) * FastMath.cos(right);
        v = Math.min(1, v);
        v = Math.max(-1, v);
        return FastMath.acos(v);
    }
}
