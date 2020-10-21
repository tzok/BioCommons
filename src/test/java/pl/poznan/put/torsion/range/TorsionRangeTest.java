package pl.poznan.put.torsion.range;

import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.torsion.range.RangeDifference;
import pl.poznan.put.torsion.range.TorsionRange;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TorsionRangeTest {
  @Test
  public final void fromAngle() {
    assertThat(
        TorsionRange.rangeProvider().fromAngle(ImmutableAngle.of(FastMath.toRadians(15.0))),
        is(TorsionRange.SYN_CIS));
    assertThat(
        TorsionRange.rangeProvider().fromAngle(ImmutableAngle.of(FastMath.toRadians(60.0))),
        is(TorsionRange.SYNCLINAL_GAUCHE_PLUS));
    assertThat(
        TorsionRange.rangeProvider().fromAngle(ImmutableAngle.of(FastMath.toRadians(120.0))),
        is(TorsionRange.ANTICLINAL_PLUS));
    assertThat(
        TorsionRange.rangeProvider().fromAngle(ImmutableAngle.of(FastMath.toRadians(180.0))),
        is(TorsionRange.ANTI_TRANS));
    assertThat(
        TorsionRange.rangeProvider().fromAngle(ImmutableAngle.of(FastMath.toRadians(-120.0))),
        is(TorsionRange.ANTICLINAL_MINUS));
    assertThat(
        TorsionRange.rangeProvider().fromAngle(ImmutableAngle.of(FastMath.toRadians(240.0))),
        is(TorsionRange.ANTICLINAL_MINUS));
    assertThat(
        TorsionRange.rangeProvider().fromAngle(ImmutableAngle.of(FastMath.toRadians(-60.0))),
        is(TorsionRange.SYNCLINAL_GAUCHE_MINUS));
    assertThat(
        TorsionRange.rangeProvider().fromAngle(ImmutableAngle.of(FastMath.toRadians(300.0))),
        is(TorsionRange.SYNCLINAL_GAUCHE_MINUS));
    assertThat(
        TorsionRange.rangeProvider().fromAngle(ImmutableAngle.of(FastMath.toRadians(-15.0))),
        is(TorsionRange.SYN_CIS));
    assertThat(
        TorsionRange.rangeProvider().fromAngle(ImmutableAngle.of(FastMath.toRadians(345.0))),
        is(TorsionRange.SYN_CIS));
  }

  @Test
  public final void testDistance() {
    // @formatter:off
    assertThat(TorsionRange.SYN_CIS.compare(TorsionRange.SYN_CIS), is(RangeDifference.EQUAL));
    assertThat(
        TorsionRange.SYN_CIS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS),
        is(RangeDifference.SIMILAR));
    assertThat(
        TorsionRange.SYN_CIS.compare(TorsionRange.ANTICLINAL_PLUS), is(RangeDifference.DIFFERENT));
    assertThat(TorsionRange.SYN_CIS.compare(TorsionRange.ANTI_TRANS), is(RangeDifference.OPPOSITE));
    assertThat(
        TorsionRange.SYN_CIS.compare(TorsionRange.ANTICLINAL_MINUS), is(RangeDifference.DIFFERENT));
    assertThat(
        TorsionRange.SYN_CIS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS),
        is(RangeDifference.SIMILAR));

    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS),
        is(RangeDifference.EQUAL));
    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.ANTICLINAL_PLUS),
        is(RangeDifference.SIMILAR));
    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.ANTI_TRANS),
        is(RangeDifference.DIFFERENT));
    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.ANTICLINAL_MINUS),
        is(RangeDifference.OPPOSITE));
    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS),
        is(RangeDifference.DIFFERENT));
    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.SYN_CIS),
        is(RangeDifference.SIMILAR));

    assertThat(
        TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.ANTICLINAL_PLUS),
        is(RangeDifference.EQUAL));
    assertThat(
        TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.ANTI_TRANS), is(RangeDifference.SIMILAR));
    assertThat(
        TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.ANTICLINAL_MINUS),
        is(RangeDifference.DIFFERENT));
    assertThat(
        TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS),
        is(RangeDifference.OPPOSITE));
    assertThat(
        TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.SYN_CIS), is(RangeDifference.DIFFERENT));
    assertThat(
        TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS),
        is(RangeDifference.SIMILAR));

    assertThat(TorsionRange.ANTI_TRANS.compare(TorsionRange.ANTI_TRANS), is(RangeDifference.EQUAL));
    assertThat(
        TorsionRange.ANTI_TRANS.compare(TorsionRange.ANTICLINAL_MINUS),
        is(RangeDifference.SIMILAR));
    assertThat(
        TorsionRange.ANTI_TRANS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS),
        is(RangeDifference.DIFFERENT));
    assertThat(TorsionRange.ANTI_TRANS.compare(TorsionRange.SYN_CIS), is(RangeDifference.OPPOSITE));
    assertThat(
        TorsionRange.ANTI_TRANS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS),
        is(RangeDifference.DIFFERENT));
    assertThat(
        TorsionRange.ANTI_TRANS.compare(TorsionRange.ANTICLINAL_PLUS), is(RangeDifference.SIMILAR));

    assertThat(
        TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.ANTICLINAL_MINUS),
        is(RangeDifference.EQUAL));
    assertThat(
        TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS),
        is(RangeDifference.SIMILAR));
    assertThat(
        TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.SYN_CIS), is(RangeDifference.DIFFERENT));
    assertThat(
        TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS),
        is(RangeDifference.OPPOSITE));
    assertThat(
        TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.ANTICLINAL_PLUS),
        is(RangeDifference.DIFFERENT));
    assertThat(
        TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.ANTI_TRANS),
        is(RangeDifference.SIMILAR));

    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS),
        is(RangeDifference.EQUAL));
    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.SYN_CIS),
        is(RangeDifference.SIMILAR));
    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS),
        is(RangeDifference.DIFFERENT));
    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.ANTICLINAL_PLUS),
        is(RangeDifference.OPPOSITE));
    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.ANTI_TRANS),
        is(RangeDifference.DIFFERENT));
    assertThat(
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.ANTICLINAL_MINUS),
        is(RangeDifference.SIMILAR));
    // @formatter:on
  }
}
