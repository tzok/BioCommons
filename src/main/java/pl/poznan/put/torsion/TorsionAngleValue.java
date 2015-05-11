package pl.poznan.put.torsion;

import pl.poznan.put.circular.Angle;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.torsion.type.TorsionAngleType;
import pl.poznan.put.utility.AngleFormat;

public class TorsionAngleValue implements DisplayableExportable {
    public static TorsionAngleValue invalidInstance(TorsionAngleType type) {
        return new TorsionAngleValue(type, Angle.invalidInstance());
    }

    private final TorsionAngleType angleType;
    private final Angle value;

    public TorsionAngleValue(TorsionAngleType angleType, Angle value) {
        super();
        this.angleType = angleType;
        this.value = value;
    }

    public TorsionAngleType getAngleType() {
        return angleType;
    }

    public Angle getValue() {
        return value;
    }

    @Override
    public String getLongDisplayName() {
        return angleType.getLongDisplayName() + " " + AngleFormat.formatDisplayLong(value.getRadians());
    }

    @Override
    public String getShortDisplayName() {
        return angleType.getShortDisplayName() + " " + AngleFormat.formatDisplayShort(value.getRadians());
    }

    @Override
    public String getExportName() {
        return angleType.getExportName() + " " + AngleFormat.formatExport(value.getRadians());
    }

    @Override
    public String toString() {
        return getLongDisplayName();
    }

    public boolean isValid() {
        return value.isValid();
    }
}
