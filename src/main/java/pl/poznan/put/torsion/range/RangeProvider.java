package pl.poznan.put.torsion.range;

import pl.poznan.put.circular.Angle;

public interface RangeProvider {
  Range fromAngle(Angle angle);
}
