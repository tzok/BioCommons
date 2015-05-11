package pl.poznan.put.circular;

import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;

/**
 * A class of measurements where one can distinguish a direction (i.e. [0..360)
 * degrees)
 * 
 * @author tzok
 */
public class Angle extends Circular {
    private static final Angle INVALID = new Angle(Double.NaN);
    private static final int MINUTES_IN_DAY = 24 * 60;

    public static Angle invalidInstance() {
        return Angle.INVALID;
    }

    public static double subtract(double radians1, double radians2) {
        double d = Math.abs(radians1 - radians2);
        return Math.min(d, 2 * Math.PI - d);
    }

    /**
     * Parse string in format HH.MM as a vector on a circular clock. For 'm'
     * minutes after midnight, the vector has value of '360 * m / (24 * 60)'.
     * 
     * @param hourMinute
     *            String in format HH.MM.
     * @return A vector representation of time on a circular clock.
     * @throws InvalidVectorFormatException
     *             If the input string has an invalid format.
     * @throws InvalidCircularValueException
     *             If the input string is parsed to a value outside the range
     *             [0..360)
     */
    public static Angle fromHourMinuteString(String hourMinute) throws InvalidVectorFormatException, InvalidCircularValueException {
        String[] split = hourMinute.split("\\.");

        if (split.length != 2) {
            throw new InvalidVectorFormatException("Required format is HH.MM eg. 02.40. The input given was: " + hourMinute);
        }

        try {
            int hours = Integer.parseInt(split[0]);
            int minutes = Integer.parseInt(split[1]);
            minutes += hours * 60;
            return new Angle(2 * Math.PI * minutes / MINUTES_IN_DAY);
        } catch (NumberFormatException e) {
            throw new InvalidVectorFormatException("Required format is HH.MM eg. 02.40. The input given was: " + hourMinute, e);
        }
    }

    public Angle(double radians) {
        super(radians);
    }

    public Angle multiply(double n) throws InvalidCircularValueException {
        return new Angle((radians * n) % (2 * Math.PI));
    }

    public Angle subtract(Angle other) throws InvalidCircularValueException {
        return new Angle(Angle.subtract(radians, other.radians));
    }
}
