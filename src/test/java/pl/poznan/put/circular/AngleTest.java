package pl.poznan.put.circular;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AngleTest {
    @Test
    public void fromHourMinuteString() throws Exception {
        assertEquals(new Angle(0), Angle.fromHourMinuteString("00.00"));
        assertEquals(new Angle(Math.toRadians(45)),
                     Angle.fromHourMinuteString("03.00"));
        assertEquals(new Angle(Math.toRadians(90)),
                     Angle.fromHourMinuteString("06.00"));
        assertEquals(new Angle(Math.toRadians(135)),
                     Angle.fromHourMinuteString("09.00"));
        assertEquals(new Angle(Math.toRadians(180)),
                     Angle.fromHourMinuteString("12.00"));
        assertEquals(new Angle(Math.toRadians(225)),
                     Angle.fromHourMinuteString("15.00"));
        assertEquals(new Angle(Math.toRadians(270)),
                     Angle.fromHourMinuteString("18.00"));
        assertEquals(new Angle(Math.toRadians(315)),
                     Angle.fromHourMinuteString("21.00"));
    }

    @Test
    public final void subtract() throws Exception {
        for (int i = 0; i < 360; i++) {
            double ri = Math.toRadians(i);
            for (int j = 0; j < 360; j++) {
                double rj = Math.toRadians(j);
                assertEquals(
                        "Difference in subtraction for: " + i + " and " + j,
                        Angle.subtractByMinimum(ri, rj),
                        Angle.subtractAsVectors(ri, rj), 1.0e-6);
            }
        }
    }
}