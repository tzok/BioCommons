package pl.poznan.put.circular;

public abstract class Circular {
    protected final double radians;

    public Circular(double radians) {
        super();
        this.radians = radians;
    }

    public double getRadians() {
        return radians;
    }

    public double getDegrees() {
        return Math.toDegrees(radians);
    }

    /**
     * Return the circular value in [0..360) for Vector or [0..180) for Axis.
     * 
     * @return Number in degrees which represents this datum as its specific
     *         type.
     */
    public abstract double getDegreesPositive();

    @Override
    public String toString() {
        return String.valueOf(getRadians()) + " rad\t" + String.valueOf(getDegrees()) + " deg\t" + String.valueOf(getDegreesPositive()) + " deg";
    }
}
