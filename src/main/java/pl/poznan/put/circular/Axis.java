package pl.poznan.put.circular;

import org.apache.commons.math3.util.MathUtils;
import pl.poznan.put.circular.exception.InvalidCircularValueException;

/**
 * A class of measurements where no direction can be specified (i.e. [0..180)).
 * When you analyze Axes, you should always look also at the second pole eg. 30
 * degrees -> also check 210 degrees.
 *
 * @author tzok
 */
public class Axis extends Circular {
    public Axis(final double radians) {
        super((radians > MathUtils.TWO_PI) ? (radians - MathUtils.TWO_PI)
                                           : radians);

        if ((radians < 0) || (radians >= (2 * Math.PI))) {
            throw new InvalidCircularValueException(
                    "An axis must be in range [0..360). Note that inputs "
                    + "[180..360) are mapped automatically into [0..180)");
        }
    }
}
