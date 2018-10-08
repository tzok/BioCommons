package pl.poznan.put.rna.torsion.range;

import lombok.Getter;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.RangeDifference;
import pl.poznan.put.torsion.range.RangeProvider;

@Getter
public enum Pseudorotation implements Range {
  TWIST_3_2("C2'-exo-C3'-endo", 0 * 18.0),
  ENVELOPE_3_ENDO("C3'-endo", 1 * 18.0),
  TWIST_3_4("C3'-endo-C4'-exo", 2 * 18.0),
  ENVELOPE_4_EXO("C4'-exo", 3 * 18.0),
  TWIST_O_4("C4'-exo-O4'-endo", 4 * 18.0),
  ENVELOPE_O_ENDO("O4'-endo", 5 * 18.0),
  TWIST_O_1("O4'-endo-C1'-exo", 6 * 18.0),
  ENVELOPE_1_EXO("C1'-exo", 7 * 18.0),
  TWIST_2_1("C1'-exo-C2'-endo", 8 * 18.0),
  ENVELOPE_2_ENDO("C2'-endo", 9 * 18.0),
  TWIST_2_3("C2'-endo-C3'-exo", 10 * 18.0),
  ENVELOPE_3_EXO("C3'-exo", 11 * 18.0),
  TWIST_4_3("C3'-exo-C4'-endo", 12 * 18.0),
  ENVELOPE_4_ENDO("C4'-endo", 13 * 18.0),
  TWIST_4_O("C4'-endo-O4'-exo", 14 * 18.0),
  ENVELOPE_O_EXO("O4'-exo", 15 * 18.0),
  TWIST_1_O("O4'-exo-C1'-endo", 16 * 18.0),
  ENVELOPE_1_ENDO("C1'-endo", 17 * 18.0),
  TWIST_1_2("C1'-endo-C2'-exo", 18 * 18.0),
  ENVELOPE_2_EXO("C2'-exo", 19 * 18.0),
  INVALID("invalid", Double.NaN);

  private static final RangeProvider PROVIDER =
      angle -> {
        if (angle.isValid()) {
          for (final Pseudorotation candidate : Pseudorotation.values()) {
            if (angle.isBetween(candidate.begin, candidate.end)) {
              return candidate;
            }
          }
        }
        return Pseudorotation.INVALID;
      };

  public static RangeProvider getProvider() {
    return Pseudorotation.PROVIDER;
  }

  private final String displayName;
  private final Angle begin;
  private final Angle end;

  Pseudorotation(final String displayName, final double degrees) {
    this.displayName = displayName;
    begin = new Angle(degrees - 9.0, ValueType.DEGREES);
    end = new Angle(degrees + 9.0, ValueType.DEGREES);
  }

  @Override
  public RangeDifference compare(final Range other) {
    if (!(other instanceof Pseudorotation)) {
      throw new IllegalArgumentException(
          "A Pseudorotation object can be compared only with other " + "Pseudorotation object");
    }

    if ((this == Pseudorotation.INVALID) || (other == Pseudorotation.INVALID)) {
      return RangeDifference.INVALID;
    }

    if (this == other) {
      return RangeDifference.EQUAL;
    }

    double difference = begin.subtract(other.getBegin()).getDegrees();
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
