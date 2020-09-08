package pl.poznan.put.torsion.range;

import pl.poznan.put.circular.Angle;

public interface Range {
  String displayName();

  Angle begin();

  Angle end();

  RangeDifference compare(Range other);
}
