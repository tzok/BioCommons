package pl.poznan.put.torsion.range;

import pl.poznan.put.circular.Angle;

public interface Range {
  String getDisplayName();

  Angle getBegin();

  Angle getEnd();

  RangeDifference compare(Range other);
}
