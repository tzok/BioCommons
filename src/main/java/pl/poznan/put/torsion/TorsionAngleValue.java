package pl.poznan.put.torsion;

import org.immutables.value.Value;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.utility.AngleFormat;

/** A torsion angle with its value calculated. */
@Value.Immutable
public abstract class TorsionAngleValue implements DisplayableExportable {
  /**
   * @return The type of this torsion angle.
   */
  @Value.Parameter(order = 1)
  public abstract TorsionAngleType angleType();

  /**
   * @return The value of this torsion angle.
   */
  @Value.Parameter(order = 2)
  public abstract Angle value();

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
