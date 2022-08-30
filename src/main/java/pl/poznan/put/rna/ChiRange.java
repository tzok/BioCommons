package pl.poznan.put.rna;

import java.util.Arrays;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.math3.util.FastMath;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.RangeDifference;
import pl.poznan.put.torsion.range.RangeProvider;

/** A range description for chi torsion angle type as defined in Saenger's "Principles...". */
public enum ChiRange implements Range {
  HIGH_ANTI("high anti", -90.0, -15.0),
  SYN("syn", -15.0, 110.0),
  ANTI("anti", 110.0, 270.0),
  INVALID("invalid", Double.NaN, Double.NaN);

  private static final MultiKeyMap<ChiRange, RangeDifference> DIFFERENCE_MAP = new MultiKeyMap<>();
  private static final RangeProvider PROVIDER =
      angle ->
          Arrays.stream(ChiRange.values())
              .filter(range -> angle.isBetween(range.begin, range.end))
              .findFirst()
              .orElse(ChiRange.INVALID);

  static {
    ChiRange.DIFFERENCE_MAP.put(ChiRange.ANTI, ChiRange.ANTI, RangeDifference.EQUAL);
    ChiRange.DIFFERENCE_MAP.put(ChiRange.ANTI, ChiRange.HIGH_ANTI, RangeDifference.SIMILAR);
    ChiRange.DIFFERENCE_MAP.put(ChiRange.ANTI, ChiRange.SYN, RangeDifference.OPPOSITE);
    ChiRange.DIFFERENCE_MAP.put(ChiRange.HIGH_ANTI, ChiRange.ANTI, RangeDifference.SIMILAR);
    ChiRange.DIFFERENCE_MAP.put(ChiRange.HIGH_ANTI, ChiRange.HIGH_ANTI, RangeDifference.EQUAL);
    ChiRange.DIFFERENCE_MAP.put(ChiRange.HIGH_ANTI, ChiRange.SYN, RangeDifference.DIFFERENT);
    ChiRange.DIFFERENCE_MAP.put(ChiRange.SYN, ChiRange.ANTI, RangeDifference.OPPOSITE);
    ChiRange.DIFFERENCE_MAP.put(ChiRange.SYN, ChiRange.HIGH_ANTI, RangeDifference.DIFFERENT);
    ChiRange.DIFFERENCE_MAP.put(ChiRange.SYN, ChiRange.SYN, RangeDifference.EQUAL);
  }

  private final String displayName;
  private final Angle begin;
  private final Angle end;

  ChiRange(final String displayName, final double begin, final double end) {
    this.displayName = displayName;
    this.begin = ImmutableAngle.of(FastMath.toRadians(begin));
    this.end = ImmutableAngle.of(FastMath.toRadians(end));
  }

  public static RangeProvider getProvider() {
    return ChiRange.PROVIDER;
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
    if (!(other instanceof ChiRange)) {
      throw new IllegalArgumentException(
          "A ChiRange object can be compared only with other ChiRange object");
    }

    if ((this == ChiRange.INVALID) || (other == ChiRange.INVALID)) {
      return RangeDifference.INVALID;
    }

    return ChiRange.DIFFERENCE_MAP.get(this, other);
  }
}
