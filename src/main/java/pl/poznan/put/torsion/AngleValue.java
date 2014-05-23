package pl.poznan.put.torsion;

import pl.poznan.put.common.DisplayableExportable;
import pl.poznan.put.helper.CommonNumberFormat;
import pl.poznan.put.helper.FractionAngleFormat;

public class AngleValue implements DisplayableExportable {
    private final TorsionAngle angle;
    private final double value;

    public static AngleValue invalidInstance(TorsionAngle angle) {
        return new AngleValue(angle, Double.NaN);
    }

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
