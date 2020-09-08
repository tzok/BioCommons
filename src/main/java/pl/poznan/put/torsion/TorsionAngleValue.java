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
    return longDisplayName();
  }

  @Override
  public final String longDisplayName() {
    return String.format(
        "%s %s",
        angleType.longDisplayName(), AngleFormat.degreesRoundedToHundredth(value.radians()));
  }

  @Override
  public final String shortDisplayName() {
    return String.format(
        "%s %s", angleType.shortDisplayName(), AngleFormat.degreesRoundedToOne(value.radians()));
  }

  @Override
  public final String exportName() {
    return String.format("%s %s", angleType.exportName(), AngleFormat.degrees(value.radians()));
  }
}
