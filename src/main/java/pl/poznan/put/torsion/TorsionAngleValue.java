package pl.poznan.put.torsion;

import lombok.Data;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.utility.AngleFormat;

@Data
public class TorsionAngleValue implements DisplayableExportable {
  private TorsionAngleType angleType;
  private Angle value;

  public TorsionAngleValue(final TorsionAngleType angleType, final Angle value) {
    super();
    this.angleType = angleType;
    this.value = value;
  }

  @Override
  public final String toString() {
    return getLongDisplayName();
  }

  @Override
  public final String getLongDisplayName() {
    return String.format(
        "%s %s",
        angleType.getLongDisplayName(), AngleFormat.degreesRoundedToHundredth(value.radians()));
  }

  @Override
  public final String getShortDisplayName() {
    return String.format(
        "%s %s", angleType.getShortDisplayName(), AngleFormat.degreesRoundedToOne(value.radians()));
  }

  @Override
  public final String getExportName() {
    return String.format("%s %s", angleType.getExportName(), AngleFormat.degrees(value.radians()));
  }
}
