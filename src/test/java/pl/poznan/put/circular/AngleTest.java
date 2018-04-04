package pl.poznan.put.circular;

import static org.junit.Assert.*;

import org.junit.Test;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;

public class AngleTest {

  private static final Angle DEGREES_0 = new Angle(0, ValueType.DEGREES);
  private static final Angle DEGREES_45 = new Angle(45, ValueType.DEGREES);
  private static final Angle DEGREES_90 = new Angle(90, ValueType.DEGREES);
  private static final Angle DEGREES_135 = new Angle(135, ValueType.DEGREES);
  private static final Angle DEGREES_180 = new Angle(180, ValueType.DEGREES);
  private static final Angle DEGREES_225 = new Angle(225, ValueType.DEGREES);
  private static final Angle DEGREES_270 = new Angle(270, ValueType.DEGREES);
  private static final Angle DEGREES_315 = new Angle(315, ValueType.DEGREES);
  private static final Angle[] ANGLES = {
    AngleTest.DEGREES_0, AngleTest.DEGREES_45, AngleTest.DEGREES_90,
    AngleTest.DEGREES_135, AngleTest.DEGREES_180, AngleTest.DEGREES_225,
    AngleTest.DEGREES_270, AngleTest.DEGREES_315
  };

  @Test
  public void fromHourMinuteString() {
    assertEquals(AngleTest.DEGREES_0, Angle.fromHourMinuteString("00.00"));
    assertEquals(AngleTest.DEGREES_45, Angle.fromHourMinuteString("03.00"));
    assertEquals(AngleTest.DEGREES_90, Angle.fromHourMinuteString("06.00"));
    assertEquals(AngleTest.DEGREES_135, Angle.fromHourMinuteString("09.00"));
    assertEquals(AngleTest.DEGREES_180, Angle.fromHourMinuteString("12.00"));
    assertEquals(AngleTest.DEGREES_225, Angle.fromHourMinuteString("15.00"));
    assertEquals(AngleTest.DEGREES_270, Angle.fromHourMinuteString("18.00"));
    assertEquals(AngleTest.DEGREES_315, Angle.fromHourMinuteString("21.00"));
  }

  @Test(expected = InvalidVectorFormatException.class)
  public void fromHourMinuteStringInvalidDots() {
    Angle.fromHourMinuteString("00.00.00");
  }

  @Test(expected = InvalidVectorFormatException.class)
  public void fromHourMinuteStringInvalidNumber() {
    Angle.fromHourMinuteString("aa.bb");
  }

  @Test
  public final void subtract() {
    for (int i = 0; i < 360; i++) {
      final double ri = Math.toRadians(i);
      for (int j = 0; j < 360; j++) {
        final double rj = Math.toRadians(j);
        assertEquals(
            String.format("Difference in subtraction for: %d and %d", i, j),
            Angle.subtractByMinimum(ri, rj),
            Angle.subtractAsVectors(ri, rj),
            1.0e-6);
      }
    }
  }

  @Test
  public void invalidInstance() {
    final Angle invalidInstance = Angle.invalidInstance();
    assertFalse(invalidInstance.isValid());

    // whatever operation you do, the result remains invalid
    for (final Angle angle : AngleTest.ANGLES) {
      assertFalse(invalidInstance.subtract(angle).isValid());
    }

    // all values are NaN
    assertTrue(Double.isNaN(invalidInstance.getDegrees()));
    assertTrue(Double.isNaN(invalidInstance.getDegrees360()));
    assertTrue(Double.isNaN(invalidInstance.getRadians()));
    assertTrue(Double.isNaN(invalidInstance.getRadians2PI()));
  }

  @Test
  public void isBetween() {
    // 0 <= 45 < 90
    assertTrue(AngleTest.DEGREES_45.isBetween(AngleTest.DEGREES_0, AngleTest.DEGREES_90));
    // 45 <= 45 < 90
    assertTrue(AngleTest.DEGREES_45.isBetween(AngleTest.DEGREES_45, AngleTest.DEGREES_90));
    // not (0 <= 45 < 45)
    assertFalse(AngleTest.DEGREES_45.isBetween(AngleTest.DEGREES_0, AngleTest.DEGREES_45));
    // not (45 <= 0 < 90)
    assertFalse(AngleTest.DEGREES_0.isBetween(AngleTest.DEGREES_45, AngleTest.DEGREES_90));
    // 315 <= 0 < 45
    assertTrue(AngleTest.DEGREES_0.isBetween(AngleTest.DEGREES_315, AngleTest.DEGREES_45));
    // 270 <= 315 < 45
    assertTrue(AngleTest.DEGREES_315.isBetween(AngleTest.DEGREES_270, AngleTest.DEGREES_45));
  }

  @Test
  public void multiply() {
    // multiplied by 1.0 does not change the value
    for (final Angle angle : AngleTest.ANGLES) {
      assertEquals(angle, angle.multiply(1.0));
    }

    // multiplied by 2.0, the distance is equal to the value itself
    for (final Angle angle : AngleTest.ANGLES) {
      assertEquals(angle, angle.multiply(2.0).orderedSubtract(angle));
    }
  }

  @Test
  public void orderedSubtract() {
    final int length = AngleTest.ANGLES.length;
    for (int i = 1; i < length; i++) {
      final Angle ai = AngleTest.ANGLES[i];

      for (int j = i + 1; j < length; j++) {
        final Angle aj = AngleTest.ANGLES[j];
        final double dij = ai.orderedSubtract(aj).getRadians();
        final double dji = aj.orderedSubtract(ai).getRadians();
        assertEquals(String.format("Test failed for: %s and %s", ai, aj), dij, -dji, 1.0e-3);
      }
    }
  }
}
