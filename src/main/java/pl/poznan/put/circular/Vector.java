package pl.poznan.put.circular;

import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;

/**
 * A class of measurements where one can distinguish a direction (i.e. [0..360)
 * degrees)
 * 
 * @author tzok
 */
public class Vector extends Circular {
    private static final int MINUTES_IN_DAY = 24 * 60;

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
    public static Vector fromHourMinuteString(String hourMinute) throws InvalidVectorFormatException, InvalidCircularValueException {
        String[] split = hourMinute.split("\\.");

        if (split.length != 2) {
            throw new InvalidVectorFormatException("Required format is HH.MM eg. 02.40. The input given was: " + hourMinute);
        }

        try {
            int hours = Integer.parseInt(split[0]);
            int minutes = Integer.parseInt(split[1]);
            minutes += hours * 60;
            return new Vector(2 * Math.PI * minutes / MINUTES_IN_DAY);
        } catch (NumberFormatException e) {
            throw new InvalidVectorFormatException("Required format is HH.MM eg. 02.40. The input given was: " + hourMinute, e);
        }
    }

    public Vector(double radians) throws InvalidCircularValueException {
        super(radians);

        if (radians < 0 || radians >= 360.0) {
            throw new InvalidCircularValueException("A vector value must be in range [0..360)");
        }
    }
}
