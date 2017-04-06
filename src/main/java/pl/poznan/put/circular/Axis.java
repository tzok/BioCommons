package pl.poznan.put.circular;

import pl.poznan.put.circular.enums.ValueType;

/**
 * A class of measurements where no direction can be specified (i.e. [0..180)).
 * When you analyze Axes, you should always look also at the second pole eg. 30
 * degrees -> also check 210 degrees.
 *
 * @author tzok
 */
public class Axis extends Circular {
    public Axis(final double value, final ValueType valueType) {
        super(value, valueType);
    }
}
