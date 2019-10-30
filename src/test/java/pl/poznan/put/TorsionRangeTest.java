package pl.poznan.put;

import static org.hamcrest.Matchers.*;

import org.junit.Assert;
import org.junit.Test;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.torsion.range.RangeDifference;
import pl.poznan.put.torsion.range.TorsionRange;

public class TorsionRangeTest {
  @Test
  public final void fromAngle() {
    // @formatter:off
    Assert.assertThat(TorsionRange.getProvider().fromAngle(new Angle(15.0, ValueType.DEGREES)), is(TorsionRange.SYN_CIS));
    Assert.assertThat(TorsionRange.getProvider().fromAngle(new Angle(60.0, ValueType.DEGREES)), is(TorsionRange.SYNCLINAL_GAUCHE_PLUS));
    Assert.assertThat(TorsionRange.getProvider().fromAngle(new Angle(120.0, ValueType.DEGREES)), is(TorsionRange.ANTICLINAL_PLUS));
    Assert.assertThat(TorsionRange.getProvider().fromAngle(new Angle(180.0, ValueType.DEGREES)), is(TorsionRange.ANTI_TRANS));
    Assert.assertThat(TorsionRange.getProvider().fromAngle(new Angle(-120.0, ValueType.DEGREES)), is(TorsionRange.ANTICLINAL_MINUS));
    Assert.assertThat(TorsionRange.getProvider().fromAngle(new Angle(240.0, ValueType.DEGREES)), is(TorsionRange.ANTICLINAL_MINUS));
    Assert.assertThat(TorsionRange.getProvider().fromAngle(new Angle(-60.0, ValueType.DEGREES)), is(TorsionRange.SYNCLINAL_GAUCHE_MINUS));
    Assert.assertThat(TorsionRange.getProvider().fromAngle(new Angle(300.0, ValueType.DEGREES)), is(TorsionRange.SYNCLINAL_GAUCHE_MINUS));
    Assert.assertThat(TorsionRange.getProvider().fromAngle(new Angle(-15.0, ValueType.DEGREES)), is(TorsionRange.SYN_CIS));
    Assert.assertThat(TorsionRange.getProvider().fromAngle(new Angle(345.0, ValueType.DEGREES)), is(TorsionRange.SYN_CIS));
    // @formatter:on
  }

  @Test
  public final void testDistance() {
    // @formatter:off
    Assert.assertThat(TorsionRange.SYN_CIS.compare(TorsionRange.SYN_CIS), is(RangeDifference.EQUAL));
    Assert.assertThat(TorsionRange.SYN_CIS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS), is(RangeDifference.SIMILAR));
    Assert.assertThat(TorsionRange.SYN_CIS.compare(TorsionRange.ANTICLINAL_PLUS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.SYN_CIS.compare(TorsionRange.ANTI_TRANS), is(RangeDifference.OPPOSITE));
    Assert.assertThat(TorsionRange.SYN_CIS.compare(TorsionRange.ANTICLINAL_MINUS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.SYN_CIS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS), is(RangeDifference.SIMILAR));

    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS), is(RangeDifference.EQUAL));
    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.ANTICLINAL_PLUS), is(RangeDifference.SIMILAR));
    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.ANTI_TRANS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.ANTICLINAL_MINUS), is(RangeDifference.OPPOSITE));
    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.SYN_CIS), is(RangeDifference.SIMILAR));

    Assert.assertThat(TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.ANTICLINAL_PLUS), is(RangeDifference.EQUAL));
    Assert.assertThat(TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.ANTI_TRANS), is(RangeDifference.SIMILAR));
    Assert.assertThat(TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.ANTICLINAL_MINUS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS), is(RangeDifference.OPPOSITE));
    Assert.assertThat(TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.SYN_CIS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS), is(RangeDifference.SIMILAR));

    Assert.assertThat(TorsionRange.ANTI_TRANS.compare(TorsionRange.ANTI_TRANS), is(RangeDifference.EQUAL));
    Assert.assertThat(TorsionRange.ANTI_TRANS.compare(TorsionRange.ANTICLINAL_MINUS), is(RangeDifference.SIMILAR));
    Assert.assertThat(TorsionRange.ANTI_TRANS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.ANTI_TRANS.compare(TorsionRange.SYN_CIS), is(RangeDifference.OPPOSITE));
    Assert.assertThat(TorsionRange.ANTI_TRANS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.ANTI_TRANS.compare(TorsionRange.ANTICLINAL_PLUS), is(RangeDifference.SIMILAR));

    Assert.assertThat(TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.ANTICLINAL_MINUS), is(RangeDifference.EQUAL));
    Assert.assertThat(TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS), is(RangeDifference.SIMILAR));
    Assert.assertThat(TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.SYN_CIS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS), is(RangeDifference.OPPOSITE));
    Assert.assertThat(TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.ANTICLINAL_PLUS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.ANTI_TRANS), is(RangeDifference.SIMILAR));

    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS), is(RangeDifference.EQUAL));
    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.SYN_CIS), is(RangeDifference.SIMILAR));
    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.ANTICLINAL_PLUS), is(RangeDifference.OPPOSITE));
    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.ANTI_TRANS), is(RangeDifference.DIFFERENT));
    Assert.assertThat(TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.ANTICLINAL_MINUS), is(RangeDifference.SIMILAR));
    // @formatter:on
  }
}
