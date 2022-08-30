package pl.poznan.put.rna;

import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.RangeDifference;
import pl.poznan.put.torsion.range.RangeProvider;

/** A range of pseudorotation values with their descriptions. */
public enum Pseudorotation implements Range {
  TWIST_3_2("C2'-exo-C3'-endo", 0 * 18.0),
  ENVELOPE_3_ENDO("C3'-endo", 1.0 * 18.0),
  TWIST_3_4("C3'-endo-C4'-exo", 2.0 * 18.0),
  ENVELOPE_4_EXO("C4'-exo", 3.0 * 18.0),
  TWIST_O_4("C4'-exo-O4'-endo", 4.0 * 18.0),
  ENVELOPE_O_ENDO("O4'-endo", 5.0 * 18.0),
  TWIST_O_1("O4'-endo-C1'-exo", 6.0 * 18.0),
  ENVELOPE_1_EXO("C1'-exo", 7.0 * 18.0),
  TWIST_2_1("C1'-exo-C2'-endo", 8.0 * 18.0),
  ENVELOPE_2_ENDO("C2'-endo", 9.0 * 18.0),
  TWIST_2_3("C2'-endo-C3'-exo", 10.0 * 18.0),
  ENVELOPE_3_EXO("C3'-exo", 11.0 * 18.0),
  TWIST_4_3("C3'-exo-C4'-endo", 12.0 * 18.0),
  ENVELOPE_4_ENDO("C4'-endo", 13.0 * 18.0),
  TWIST_4_O("C4'-endo-O4'-exo", 14.0 * 18.0),
  ENVELOPE_O_EXO("O4'-exo", 15.0 * 18.0),
  TWIST_1_O("O4'-exo-C1'-endo", 16.0 * 18.0),
  ENVELOPE_1_ENDO("C1'-endo", 17.0 * 18.0),
  TWIST_1_2("C1'-endo-C2'-exo", 18.0 * 18.0),
  ENVELOPE_2_EXO("C2'-exo", 19.0 * 18.0),
  INVALID("invalid", Double.NaN);

  private final String displayName;
  private final Angle begin;
  private final Angle end;

  Pseudorotation(final String displayName, final double degrees) {
    this.displayName = displayName;
    begin = ImmutableAngle.of(FastMath.toRadians(degrees - 9.0));
    end = ImmutableAngle.of(FastMath.toRadians(degrees + 9.0));
  }

  /**
   * @return An instance of provider which will return a pseudorotation range for a provided angle
   *     value.
   */
  public static RangeProvider getProvider() {
    return angle ->
        Arrays.stream(Pseudorotation.values())
            .filter(candidate -> angle.isBetween(candidate.begin, candidate.end))
            .findFirst()
            .orElse(Pseudorotation.INVALID);
  }

  @Override
  public String displayName() {
    return displayName;
  }

  @Override
  public Angle begin() {
    return begin;
  }

  @Override
  public Angle end() {
    return end;
  }

  @Override
  public RangeDifference compare(final Range other) {
    if (!(other instanceof Pseudorotation)) {
      throw new IllegalArgumentException(
          "A Pseudorotation object can be compared only with other Pseudorotation object");
    }

    if ((this == Pseudorotation.INVALID) || (other == Pseudorotation.INVALID)) {
      return RangeDifference.INVALID;
    }

    if (this == other) {
      return RangeDifference.EQUAL;
    }

    double difference = begin.subtract(other.begin()).degrees();
    if (difference > 90.0) {
      difference = 180.0 - difference;
    }

    if (difference <= 36.0) {
      return RangeDifference.SIMILAR;
    }
    if (difference <= 72.0) {
      return RangeDifference.DIFFERENT;
    }
    return RangeDifference.OPPOSITE;
  }
}
