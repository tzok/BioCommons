package pl.poznan.put.circular;

import pl.poznan.put.circular.exception.InvalidCircularValueException;

/**
 * A class of measurements where no direction can be specified (i.e. [0..180)).
 * When you analyze Axes, you should always look also at the second pole eg. 30
 * degrees -> also check 210 degrees.
 * 
 * @author tzok
 */
public class Axis extends Circular {
    public Axis(double radians) throws InvalidCircularValueException {
        super(radians > 180.0 ? radians - 180.0 : radians);

        if (radians < 0 || radians >= 2 * Math.PI) {
            throw new InvalidCircularValueException("An axis must be in range [0..360). Note that inputs [180..360) are mapped automatically into [0..180)");
        }
    }
}
