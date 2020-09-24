package pl.poznan.put.torsion.range;

import pl.poznan.put.circular.Angle;

/** A named range of torsion angle values. */
public interface Range {
  /** @return The name of this torsion angle range. */
  String displayName();

  /** @return The beginning value of the range. */
  Angle begin();

  /** @return The ending value of the range. */
  Angle end();

  /**
   * Compares with another range object.
   *
   * @param other The other range object.
   * @return An enumerated value showing how much does these two ranges really differ.
   */
  RangeDifference compare(Range other);
}
