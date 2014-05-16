package pl.poznan.put.common;


public class TorsionAngleValue {
    private final TorsionAngle torsionAngle;
    private final double value;

    public static TorsionAngleValue invalidInstance(TorsionAngle torsionAngle) {
        return new TorsionAngleValue(torsionAngle, Double.NaN);
    }

    public TorsionAngleValue(TorsionAngle torsionAngle, double value) {
        super();
        this.torsionAngle = torsionAngle;
        this.value = value;
    }

    public TorsionAngle getTorsionAngle() {
        return torsionAngle;
    }

    public double getValue() {
        return value;
    }

    public boolean isValid() {
        return !Double.isNaN(value);
    }

    @Override
    public String toString() {
        return torsionAngle + " " + value;
    }
}
