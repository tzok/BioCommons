package pl.poznan.put.torsion.range;

import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;

/** A default torsion angle range as defined in Saenger's "Principles...". */
public enum TorsionRange implements Range {
  SYN_CIS("sp", -30.0, 30.0),
  ANTI_TRANS("ap", 150.0, -150.0),
  SYNCLINAL_GAUCHE_PLUS("+sc", 30.0, 90.0),
  SYNCLINAL_GAUCHE_MINUS("-sc", -90.0, -30.0),
  ANTICLINAL_PLUS("+ac", 90.0, 150.0),
  ANTICLINAL_MINUS("-ac", -150.0, -90.0),
  INVALID("invalid", Double.NaN, Double.NaN);

  private final String displayName;
  private final Angle begin;
  private final Angle end;

  TorsionRange(final String displayName, final double begin, final double end) {
    this.displayName = displayName;
    this.begin = ImmutableAngle.of(FastMath.toRadians(begin));
    this.end = ImmutableAngle.of(FastMath.toRadians(end));
  }

  /**
   * @return An instance of {@link RangeProvider} which will provide this ranges for angle values.
   */
  public static RangeProvider rangeProvider() {
    return angle ->
        Arrays.stream(TorsionRange.values())
            .filter(torsionRange -> angle.isBetween(torsionRange.begin, torsionRange.end))
            .findFirst()
            .orElse(TorsionRange.INVALID);
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

  /**
   * Calculate difference between two angle ranges. It will be either 0 (equal), 1 (neighbour), 2
   * (next to neighbour) or 3 (opposite). Because each range is exactly 60 degree wide, then
   * difference between beginnings is also always a multiple of 60.
   *
   * @param other An object to compare to.
   * @return RangeDifference object.
   */
  @Override
  public RangeDifference compare(final Range other) {
    if (!(other instanceof TorsionRange)) {
      throw new IllegalArgumentException(
          "A Range object can be compared only with other Range object");
    }

    if ((this == TorsionRange.INVALID) || (other == TorsionRange.INVALID)) {
      return RangeDifference.INVALID;
    }

    final int delta = (int) Math.round(begin.subtract(other.begin()).degrees360());
    return RangeDifference.fromValue(delta / 60);
  }
}
