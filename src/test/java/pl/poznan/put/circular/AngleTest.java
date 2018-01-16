package pl.poznan.put.circular;

import org.junit.Test;
import pl.poznan.put.circular.enums.ValueType;

import static org.junit.Assert.assertEquals;

public class AngleTest {
  @Test
  public void fromHourMinuteString() throws Exception {
    assertEquals(new Angle(0, ValueType.DEGREES), Angle.fromHourMinuteString("00.00"));
    assertEquals(new Angle(45, ValueType.DEGREES), Angle.fromHourMinuteString("03.00"));
    assertEquals(new Angle(90, ValueType.DEGREES), Angle.fromHourMinuteString("06.00"));
    assertEquals(new Angle(135, ValueType.DEGREES), Angle.fromHourMinuteString("09.00"));
    assertEquals(new Angle(180, ValueType.DEGREES), Angle.fromHourMinuteString("12.00"));
    assertEquals(new Angle(225, ValueType.DEGREES), Angle.fromHourMinuteString("15.00"));
    assertEquals(new Angle(270, ValueType.DEGREES), Angle.fromHourMinuteString("18.00"));
    assertEquals(new Angle(315, ValueType.DEGREES), Angle.fromHourMinuteString("21.00"));
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
            Angle.subtractAsVectors(ri, rj),
            1.0e-6);
      }
    }
  }
}
