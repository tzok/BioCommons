package pl.poznan.put.torsion;

import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.utility.AngleFormat;

public class AngleValue implements DisplayableExportable {
    public static AngleValue getInvalidInstance(TorsionAngle angle) {
        return new AngleValue(angle, Double.NaN);
    }

    private final TorsionAngle angle;
    private final double value;

    public AngleValue(TorsionAngle angle, double value) {
        super();
        this.angle = angle;
        this.value = value;
    }

    public TorsionAngle getAngle() {
        return angle;
    }

    public double getValue() {
        return value;
    }

    public boolean isValid() {
        return !Double.isNaN(value);
    }

    @Override
    public String getLongDisplayName() {
        return angle.getLongDisplayName() + " "
                + AngleFormat.formatDisplayLong(value);
    }

    @Override
    public String getShortDisplayName() {
        return angle.getShortDisplayName() + " "
                + AngleFormat.formatDisplayShort(value);
    }

    @Override
    public String getExportName() {
        return angle.getExportName() + " " + AngleFormat.formatExport(value);
    }

    @Override
    public String toString() {
        return getLongDisplayName();
    }
}
