package pl.poznan.put.circular;

import org.junit.Test;
import pl.poznan.put.circular.enums.Range;
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
}