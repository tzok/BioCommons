package pl.poznan.put.circular;

/**
 * A class of measurements where no direction can be specified. When you analyze
 * Axes, you should always look at the second pole eg. 30 degrees -> also check
 * 210 degrees.
 * 
 * @author tzok
 */
public class Axis extends Circular {
    public Axis(double radians) {
        super(radians);
    }

    @Override
    public double getDegreesPositive() {
        double degrees = (Math.toDegrees(radians) + 360.0) % 360.0;
        if (degrees > 180.0) {
            degrees -= 180.0;
        }
        return degrees;
    }
}
