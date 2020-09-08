package pl.poznan.put.torsion;

import org.immutables.value.Value;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.utility.AngleFormat;

@Value.Immutable
public abstract class TorsionAngleValue implements DisplayableExportable {
  @Value.Parameter(order = 1)
  public abstract TorsionAngleType angleType();

  @Value.Parameter(order = 2)
  public abstract Angle value();

  @Override
  public final String toString() {
    return longDisplayName();
  }

  @Override
  public final String shortDisplayName() {
    return String.format(
        "%s %s",
        angleType().shortDisplayName(), AngleFormat.degreesRoundedToOne(value().radians()));
  }

  @Override
  public final String longDisplayName() {
    return String.format(
        "%s %s",
        angleType().longDisplayName(), AngleFormat.degreesRoundedToHundredth(value().radians()));
  }

  @Override
  public final String exportName() {
    return String.format("%s %s", angleType().exportName(), AngleFormat.degrees(value().radians()));
  }
}
