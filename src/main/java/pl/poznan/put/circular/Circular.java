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

    @Override
    public String toString() {
        return String.valueOf(getRadians()) + " rad\t" + String.valueOf(getDegrees()) + " deg";
    }
}
