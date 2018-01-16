package pl.poznan.put;

import org.junit.Test;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.torsion.range.RangeDifference;
import pl.poznan.put.torsion.range.TorsionRange;

import static org.junit.Assert.assertEquals;

public class TorsionRangeTest {
  @Test
  public void fromAngle() throws Exception {
    // @formatter:off
    assertEquals(
        TorsionRange.SYN_CIS,
        TorsionRange.getProvider().fromAngle(new Angle(15, ValueType.DEGREES)));
    assertEquals(
        TorsionRange.SYNCLINAL_GAUCHE_PLUS,
        TorsionRange.getProvider().fromAngle(new Angle(60, ValueType.DEGREES)));
    assertEquals(
        TorsionRange.ANTICLINAL_PLUS,
        TorsionRange.getProvider().fromAngle(new Angle(120, ValueType.DEGREES)));
    assertEquals(
        TorsionRange.ANTI_TRANS,
        TorsionRange.getProvider().fromAngle(new Angle(180, ValueType.DEGREES)));
    assertEquals(
        TorsionRange.ANTICLINAL_MINUS,
        TorsionRange.getProvider().fromAngle(new Angle(-120, ValueType.DEGREES)));
    assertEquals(
        TorsionRange.ANTICLINAL_MINUS,
        TorsionRange.getProvider().fromAngle(new Angle(240, ValueType.DEGREES)));
    assertEquals(
        TorsionRange.SYNCLINAL_GAUCHE_MINUS,
        TorsionRange.getProvider().fromAngle(new Angle(-60, ValueType.DEGREES)));
    assertEquals(
        TorsionRange.SYNCLINAL_GAUCHE_MINUS,
        TorsionRange.getProvider().fromAngle(new Angle(300, ValueType.DEGREES)));
    assertEquals(
        TorsionRange.SYN_CIS,
        TorsionRange.getProvider().fromAngle(new Angle(-15, ValueType.DEGREES)));
    assertEquals(
        TorsionRange.SYN_CIS,
        TorsionRange.getProvider().fromAngle(new Angle(345, ValueType.DEGREES)));
    // @formatter:on
  }

  @Test
  public void testDistance() {
    // @formatter:off
    assertEquals(RangeDifference.EQUAL, TorsionRange.SYN_CIS.compare(TorsionRange.SYN_CIS));
    assertEquals(
        RangeDifference.SIMILAR, TorsionRange.SYN_CIS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS));
    assertEquals(
        RangeDifference.DIFFERENT, TorsionRange.SYN_CIS.compare(TorsionRange.ANTICLINAL_PLUS));
    assertEquals(RangeDifference.OPPOSITE, TorsionRange.SYN_CIS.compare(TorsionRange.ANTI_TRANS));
    assertEquals(
        RangeDifference.DIFFERENT, TorsionRange.SYN_CIS.compare(TorsionRange.ANTICLINAL_MINUS));
    assertEquals(
        RangeDifference.SIMILAR, TorsionRange.SYN_CIS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS));

    assertEquals(
        RangeDifference.EQUAL,
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS));
    assertEquals(
        RangeDifference.SIMILAR,
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.ANTICLINAL_PLUS));
    assertEquals(
        RangeDifference.DIFFERENT,
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.ANTI_TRANS));
    assertEquals(
        RangeDifference.OPPOSITE,
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.ANTICLINAL_MINUS));
    assertEquals(
        RangeDifference.DIFFERENT,
        TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS));
    assertEquals(
        RangeDifference.SIMILAR, TorsionRange.SYNCLINAL_GAUCHE_PLUS.compare(TorsionRange.SYN_CIS));

    assertEquals(
        RangeDifference.EQUAL, TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.ANTICLINAL_PLUS));
    assertEquals(
        RangeDifference.SIMILAR, TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.ANTI_TRANS));
    assertEquals(
        RangeDifference.DIFFERENT,
        TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.ANTICLINAL_MINUS));
    assertEquals(
        RangeDifference.OPPOSITE,
        TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS));
    assertEquals(
        RangeDifference.DIFFERENT, TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.SYN_CIS));
    assertEquals(
        RangeDifference.SIMILAR,
        TorsionRange.ANTICLINAL_PLUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS));

    assertEquals(RangeDifference.EQUAL, TorsionRange.ANTI_TRANS.compare(TorsionRange.ANTI_TRANS));
    assertEquals(
        RangeDifference.SIMILAR, TorsionRange.ANTI_TRANS.compare(TorsionRange.ANTICLINAL_MINUS));
    assertEquals(
        RangeDifference.DIFFERENT,
        TorsionRange.ANTI_TRANS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS));
    assertEquals(RangeDifference.OPPOSITE, TorsionRange.ANTI_TRANS.compare(TorsionRange.SYN_CIS));
    assertEquals(
        RangeDifference.DIFFERENT,
        TorsionRange.ANTI_TRANS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS));
    assertEquals(
        RangeDifference.SIMILAR, TorsionRange.ANTI_TRANS.compare(TorsionRange.ANTICLINAL_PLUS));

    assertEquals(
        RangeDifference.EQUAL,
        TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.ANTICLINAL_MINUS));
    assertEquals(
        RangeDifference.SIMILAR,
        TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS));
    assertEquals(
        RangeDifference.DIFFERENT, TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.SYN_CIS));
    assertEquals(
        RangeDifference.OPPOSITE,
        TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS));
    assertEquals(
        RangeDifference.DIFFERENT,
        TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.ANTICLINAL_PLUS));
    assertEquals(
        RangeDifference.SIMILAR, TorsionRange.ANTICLINAL_MINUS.compare(TorsionRange.ANTI_TRANS));

    assertEquals(
        RangeDifference.EQUAL,
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_MINUS));
    assertEquals(
        RangeDifference.SIMILAR, TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.SYN_CIS));
    assertEquals(
        RangeDifference.DIFFERENT,
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.SYNCLINAL_GAUCHE_PLUS));
    assertEquals(
        RangeDifference.OPPOSITE,
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.ANTICLINAL_PLUS));
    assertEquals(
        RangeDifference.DIFFERENT,
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.ANTI_TRANS));
    assertEquals(
        RangeDifference.SIMILAR,
        TorsionRange.SYNCLINAL_GAUCHE_MINUS.compare(TorsionRange.ANTICLINAL_MINUS));
    // @formatter:on
  }
}
