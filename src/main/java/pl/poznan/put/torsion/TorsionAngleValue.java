package pl.poznan.put.torsion;

import pl.poznan.put.circular.Angle;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.torsion.type.TorsionAngleType;
import pl.poznan.put.utility.AngleFormat;

public class TorsionAngleValue implements DisplayableExportable {
    private final TorsionAngleType angle;
    private final Angle value;

    public TorsionAngleValue(TorsionAngleType angle, Angle value) {
        super();
        this.angle = angle;
        this.value = value;
    }

    public TorsionAngleType getAngle() {
        return angle;
    }

    public Angle getValue() {
        return value;
    }

    @Override
    public String getLongDisplayName() {
        return angle.getLongDisplayName() + " " + AngleFormat.formatDisplayLong(value.getRadians());
    }

    @Override
    public String getShortDisplayName() {
        return angle.getShortDisplayName() + " " + AngleFormat.formatDisplayShort(value.getRadians());
    }

    @Override
    public String getExportName() {
        return angle.getExportName() + " " + AngleFormat.formatExport(value.getRadians());
    }

    @Override
    public String toString() {
        return getLongDisplayName();
    }
}
