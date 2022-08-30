package pl.poznan.put.circular;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;

public class AngleTest {
  private static final Angle DEGREES_0 = ImmutableAngle.of(FastMath.toRadians(0));
  private static final Angle DEGREES_45 = ImmutableAngle.of(FastMath.toRadians(45.0));
  private static final Angle DEGREES_90 = ImmutableAngle.of(FastMath.toRadians(90.0));
  private static final Angle DEGREES_135 = ImmutableAngle.of(FastMath.toRadians(135.0));
  private static final Angle DEGREES_180 = ImmutableAngle.of(FastMath.toRadians(180.0));
  private static final Angle DEGREES_225 = ImmutableAngle.of(FastMath.toRadians(225.0));
  private static final Angle DEGREES_270 = ImmutableAngle.of(FastMath.toRadians(270.0));
  private static final Angle DEGREES_315 = ImmutableAngle.of(FastMath.toRadians(315.0));
  private static final Angle[] ANGLES = {
    AngleTest.DEGREES_0, AngleTest.DEGREES_45, AngleTest.DEGREES_90,
    AngleTest.DEGREES_135, AngleTest.DEGREES_180, AngleTest.DEGREES_225,
    AngleTest.DEGREES_270, AngleTest.DEGREES_315
  };
  private static final double EPS = 1.0e-6;

  @Test
  public final void fromHourMinuteString() {
    assertThat(Angle.fromHourMinuteString("00.00"), is(AngleTest.DEGREES_0));
    assertThat(Angle.fromHourMinuteString("03.00"), is(AngleTest.DEGREES_45));
    assertThat(Angle.fromHourMinuteString("06.00"), is(AngleTest.DEGREES_90));
    assertThat(Angle.fromHourMinuteString("09.00"), is(AngleTest.DEGREES_135));
    assertThat(Angle.fromHourMinuteString("12.00"), is(AngleTest.DEGREES_180));
    assertThat(Angle.fromHourMinuteString("15.00"), is(AngleTest.DEGREES_225));
    assertThat(Angle.fromHourMinuteString("18.00"), is(AngleTest.DEGREES_270));
    assertThat(Angle.fromHourMinuteString("21.00"), is(AngleTest.DEGREES_315));
  }

  @Test(expected = InvalidVectorFormatException.class)
  public final void fromHourMinuteStringInvalidDots() {
    Angle.fromHourMinuteString("00.00.00");
  }

  @Test(expected = InvalidVectorFormatException.class)
  public final void fromHourMinuteStringInvalidNumber() {
    Angle.fromHourMinuteString("aa.bb");
  }

  @Test
  public final void subtract() {
    for (int i = 0; i < 360; i++) {
      final double ri = FastMath.toRadians(i);
      for (int j = 0; j < 360; j++) {
        final double rj = FastMath.toRadians(j);
        assertThat(
            String.format("Difference in subtraction for: %d and %d", i, j),
            AngleTest.isBelowEpsilon(
                Angle.subtractAsVectors(ri, rj) - Angle.subtractByMinimum(ri, rj)),
            is(true));
        assertThat(
            String.format("Difference in subtraction for: %d and %d", i, j),
            AngleTest.isBelowEpsilon(
                Angle.subtractByAbsolutes(ri, rj) - Angle.subtractByMinimum(ri, rj)),
            is(true));
      }
    }
  }

  private static boolean isBelowEpsilon(final double value) {
    return FastMath.abs(value) < AngleTest.EPS;
  }

  @Test
  public final void invalidInstance() {
    final Angle invalidInstance = ImmutableAngle.of(Double.NaN);
    assertThat(invalidInstance.isValid(), is(false));

    // whatever operation you do, the result remains invalid
    for (final Angle angle : AngleTest.ANGLES) {
      assertThat(invalidInstance.subtract(angle).isValid(), is(false));
    }

    // all values are NaN
    assertThat(Double.isNaN(invalidInstance.degrees()), is(true));
    assertThat(Double.isNaN(invalidInstance.degrees360()), is(true));
    assertThat(Double.isNaN(invalidInstance.radians()), is(true));
    assertThat(Double.isNaN(invalidInstance.radians2PI()), is(true));
  }

  @Test
  public final void testIsBetween() {
    // 0 <= 45 < 90
    assertThat(AngleTest.DEGREES_45.isBetween(AngleTest.DEGREES_0, AngleTest.DEGREES_90), is(true));
    // 45 <= 45 < 90
    assertThat(
        AngleTest.DEGREES_45.isBetween(AngleTest.DEGREES_45, AngleTest.DEGREES_90), is(true));
    // not (0 <= 45 < 45)
    assertThat(
        AngleTest.DEGREES_45.isBetween(AngleTest.DEGREES_0, AngleTest.DEGREES_45), is(false));
    // not (45 <= 0 < 90)
    assertThat(
        AngleTest.DEGREES_0.isBetween(AngleTest.DEGREES_45, AngleTest.DEGREES_90), is(false));
    // 315 <= 0 < 45
    assertThat(
        AngleTest.DEGREES_0.isBetween(AngleTest.DEGREES_315, AngleTest.DEGREES_45), is(true));
    // 270 <= 315 < 45
    assertThat(
        AngleTest.DEGREES_315.isBetween(AngleTest.DEGREES_270, AngleTest.DEGREES_45), is(true));
  }

  @Test
  public final void multiply() {
    // multiplied by 1.0 does not change the value
    for (final Angle angle : AngleTest.ANGLES) {
      assertThat(angle.multiply(1.0), is(angle));
    }

    // multiplied by 2.0, the distance is equal to the value itself
    for (final Angle angle : AngleTest.ANGLES) {
      assertThat(angle.multiply(2.0).orderedSubtract(angle), is(angle));
    }
  }

  @Test
  public final void orderedSubtract() {
    final int length = AngleTest.ANGLES.length;
    for (int i = 1; i < length; i++) {
      final Angle ai = AngleTest.ANGLES[i];

      for (int j = i + 1; j < length; j++) {
        final Angle aj = AngleTest.ANGLES[j];
        final Angle dij = ai.orderedSubtract(aj);
        final Angle dji = ImmutableAngle.of(-aj.orderedSubtract(ai).radians());
        assertThat(String.format("Test failed for: %s and %s", ai, aj), dij, is(dji));
      }
    }
  }
}
