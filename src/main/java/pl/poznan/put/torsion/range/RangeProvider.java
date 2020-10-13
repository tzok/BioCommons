package pl.poznan.put.torsion.range;

import pl.poznan.put.circular.Angle;

/**
 * A provider of torsion angle ranges (different ranges are used for chi angle and for pseudophase
 * pucker).
 */
@FunctionalInterface
public interface RangeProvider {
  /**
   * Provdes a {@link Range} instance for given angle value.
   *
   * @param angle The angle value.
   * @return An instance of {@link Range} which incorporates this value.
   */
  Range fromAngle(Angle angle);
}
