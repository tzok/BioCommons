package pl.poznan.put.circular;

import pl.poznan.put.circular.exception.InvalidFormatException;

/**
 * A class of measurements where one can distinguisch a direction.
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
     * @throws InvalidFormatException
     *             If the input string has an invalid format.
     */
    public static Vector fromHourMinuteString(String hourMinute) throws InvalidFormatException {
        String[] split = hourMinute.split("\\.");

        if (split.length != 2) {
            throw new InvalidFormatException("Required format is HH.MM eg. 02.40. The input given was: " + hourMinute);
        }

        try {
            int hours = Integer.parseInt(split[0]);
            int minutes = Integer.parseInt(split[1]);
            minutes += hours * 60;
            return new Vector(2 * Math.PI * minutes / MINUTES_IN_DAY);
        } catch (NumberFormatException e) {
            throw new InvalidFormatException("Required format is HH.MM eg. 02.40. The input given was: " + hourMinute, e);
        }
    }

    public Vector(double radians) {
        super(radians);
    }

    @Override
    public double getDegreesPositive() {
        return (Math.toDegrees(radians) + 360.0) % 360.0;
    }
}
