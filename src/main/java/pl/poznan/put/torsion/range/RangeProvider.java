package pl.poznan.put.torsion.range;

import pl.poznan.put.circular.Angle;

@FunctionalInterface
public interface RangeProvider {
  Range fromAngle(Angle angle);
}
