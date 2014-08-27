package pl.poznan.put.torsion;

import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.utility.CommonNumberFormat;
import pl.poznan.put.utility.FractionAngleFormat;

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
    public String getDisplayName() {
        return angle.getDisplayName() + " "
                + FractionAngleFormat.formatDouble(value);
    }

    @Override
    public String getExportName() {
        return angle.getExportName() + " "
                + CommonNumberFormat.formatDouble(value);
    }
}
