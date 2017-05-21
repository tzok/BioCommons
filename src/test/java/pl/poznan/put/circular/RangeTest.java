package pl.poznan.put.circular;

import org.junit.Test;
import pl.poznan.put.circular.enums.Range;
import pl.poznan.put.circular.enums.RangeDifference;
import pl.poznan.put.circular.enums.ValueType;

import static org.junit.Assert.assertEquals;

public class RangeTest {
    @Test
    public void fromAngle() throws Exception {
        assertEquals(Range.SYN_CIS,
                     Range.fromAngle(new Angle(15, ValueType.DEGREES)));
        assertEquals(Range.SYNCLINAL_GAUCHE_PLUS,
                     Range.fromAngle(new Angle(60, ValueType.DEGREES)));
        assertEquals(Range.ANTICLINAL_PLUS,
                     Range.fromAngle(new Angle(120, ValueType.DEGREES)));
        assertEquals(Range.ANTI_TRANS,
                     Range.fromAngle(new Angle(180, ValueType.DEGREES)));
        assertEquals(Range.ANTICLINAL_MINUS,
                     Range.fromAngle(new Angle(-120, ValueType.DEGREES)));
        assertEquals(Range.ANTICLINAL_MINUS,
                     Range.fromAngle(new Angle(240, ValueType.DEGREES)));
        assertEquals(Range.SYNCLINAL_GAUCHE_MINUS,
                     Range.fromAngle(new Angle(-60, ValueType.DEGREES)));
        assertEquals(Range.SYNCLINAL_GAUCHE_MINUS,
                     Range.fromAngle(new Angle(300, ValueType.DEGREES)));
        assertEquals(Range.SYN_CIS,
                     Range.fromAngle(new Angle(-15, ValueType.DEGREES)));
        assertEquals(Range.SYN_CIS,
                     Range.fromAngle(new Angle(345, ValueType.DEGREES)));
    }

    @Test
    public void testDistance() {
        // @formatter:off
        assertEquals(RangeDifference.EQUAL, Range.SYN_CIS.difference(Range.SYN_CIS));
        assertEquals(RangeDifference.SIMILAR, Range.SYN_CIS.difference(Range.SYNCLINAL_GAUCHE_PLUS));
        assertEquals(RangeDifference.DIFFERENT, Range.SYN_CIS.difference(Range.ANTICLINAL_PLUS));
        assertEquals(RangeDifference.OPPOSITE, Range.SYN_CIS.difference(Range.ANTI_TRANS));
        assertEquals(RangeDifference.DIFFERENT, Range.SYN_CIS.difference(Range.ANTICLINAL_MINUS));
        assertEquals(RangeDifference.SIMILAR, Range.SYN_CIS.difference(Range.SYNCLINAL_GAUCHE_MINUS));

        assertEquals(RangeDifference.EQUAL, Range.SYNCLINAL_GAUCHE_PLUS.difference(Range.SYNCLINAL_GAUCHE_PLUS));
        assertEquals(RangeDifference.SIMILAR, Range.SYNCLINAL_GAUCHE_PLUS.difference(Range.ANTICLINAL_PLUS));
        assertEquals(RangeDifference.DIFFERENT, Range.SYNCLINAL_GAUCHE_PLUS.difference(Range.ANTI_TRANS));
        assertEquals(RangeDifference.OPPOSITE, Range.SYNCLINAL_GAUCHE_PLUS.difference(Range.ANTICLINAL_MINUS));
        assertEquals(RangeDifference.DIFFERENT, Range.SYNCLINAL_GAUCHE_PLUS.difference(Range.SYNCLINAL_GAUCHE_MINUS));
        assertEquals(RangeDifference.SIMILAR, Range.SYNCLINAL_GAUCHE_PLUS.difference(Range.SYN_CIS));

        assertEquals(RangeDifference.EQUAL, Range.ANTICLINAL_PLUS.difference(Range.ANTICLINAL_PLUS));
        assertEquals(RangeDifference.SIMILAR, Range.ANTICLINAL_PLUS.difference(Range.ANTI_TRANS));
        assertEquals(RangeDifference.DIFFERENT, Range.ANTICLINAL_PLUS.difference(Range.ANTICLINAL_MINUS));
        assertEquals(RangeDifference.OPPOSITE, Range.ANTICLINAL_PLUS.difference(Range.SYNCLINAL_GAUCHE_MINUS));
        assertEquals(RangeDifference.DIFFERENT, Range.ANTICLINAL_PLUS.difference(Range.SYN_CIS));
        assertEquals(RangeDifference.SIMILAR, Range.ANTICLINAL_PLUS.difference(Range.SYNCLINAL_GAUCHE_PLUS));

        assertEquals(RangeDifference.EQUAL, Range.ANTI_TRANS.difference(Range.ANTI_TRANS));
        assertEquals(RangeDifference.SIMILAR, Range.ANTI_TRANS.difference(Range.ANTICLINAL_MINUS));
        assertEquals(RangeDifference.DIFFERENT, Range.ANTI_TRANS.difference(Range.SYNCLINAL_GAUCHE_MINUS));
        assertEquals(RangeDifference.OPPOSITE, Range.ANTI_TRANS.difference(Range.SYN_CIS));
        assertEquals(RangeDifference.DIFFERENT, Range.ANTI_TRANS.difference(Range.SYNCLINAL_GAUCHE_PLUS));
        assertEquals(RangeDifference.SIMILAR, Range.ANTI_TRANS.difference(Range.ANTICLINAL_PLUS));

        assertEquals(RangeDifference.EQUAL, Range.ANTICLINAL_MINUS.difference(Range.ANTICLINAL_MINUS));
        assertEquals(RangeDifference.SIMILAR, Range.ANTICLINAL_MINUS.difference(Range.SYNCLINAL_GAUCHE_MINUS));
        assertEquals(RangeDifference.DIFFERENT, Range.ANTICLINAL_MINUS.difference(Range.SYN_CIS));
        assertEquals(RangeDifference.OPPOSITE, Range.ANTICLINAL_MINUS.difference(Range.SYNCLINAL_GAUCHE_PLUS));
        assertEquals(RangeDifference.DIFFERENT, Range.ANTICLINAL_MINUS.difference(Range.ANTICLINAL_PLUS));
        assertEquals(RangeDifference.SIMILAR, Range.ANTICLINAL_MINUS.difference(Range.ANTI_TRANS));

        assertEquals(RangeDifference.EQUAL, Range.SYNCLINAL_GAUCHE_MINUS.difference(Range.SYNCLINAL_GAUCHE_MINUS));
        assertEquals(RangeDifference.SIMILAR, Range.SYNCLINAL_GAUCHE_MINUS.difference(Range.SYN_CIS));
        assertEquals(RangeDifference.DIFFERENT, Range.SYNCLINAL_GAUCHE_MINUS.difference(Range.SYNCLINAL_GAUCHE_PLUS));
        assertEquals(RangeDifference.OPPOSITE, Range.SYNCLINAL_GAUCHE_MINUS.difference(Range.ANTICLINAL_PLUS));
        assertEquals(RangeDifference.DIFFERENT, Range.SYNCLINAL_GAUCHE_MINUS.difference(Range.ANTI_TRANS));
        assertEquals(RangeDifference.SIMILAR, Range.SYNCLINAL_GAUCHE_MINUS.difference(Range.ANTICLINAL_MINUS));
        // @formatter:on
    }
}
