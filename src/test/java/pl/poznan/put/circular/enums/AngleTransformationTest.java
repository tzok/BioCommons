package pl.poznan.put.circular.enums;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;

public class AngleTransformationTest {
  private static final Angle ZERO = ImmutableAngle.of(FastMath.toRadians(0.0));
  private static final Angle HALF = ImmutableAngle.of(FastMath.toRadians(90.0));
  private static final Angle ONE = ImmutableAngle.of(FastMath.toRadians(180.0));
  private static final Angle ONE_AND_HALF = ImmutableAngle.of(FastMath.toRadians(270.0));

  @Test
  public final void transform() {
    // tests on MATH transformation
    Stream.of(
            Pair.of(AngleTransformationTest.ZERO, AngleTransformationTest.ZERO),
            Pair.of(AngleTransformationTest.HALF, AngleTransformationTest.HALF),
            Pair.of(AngleTransformationTest.ONE, AngleTransformationTest.ONE),
            Pair.of(AngleTransformationTest.ONE_AND_HALF, AngleTransformationTest.ONE_AND_HALF))
        .forEach(
            pair -> {
              final Angle expected = pair.getLeft();
              final Angle actual =
                  ImmutableAngle.of(AngleTransformation.MATH.transform(pair.getRight().radians()));
              assertThat(actual, is(expected));
            });

    // tests on CLOCK transformation
    Stream.of(
            Pair.of(AngleTransformationTest.ZERO, AngleTransformationTest.HALF),
            Pair.of(AngleTransformationTest.HALF, AngleTransformationTest.ZERO),
            Pair.of(AngleTransformationTest.ONE, AngleTransformationTest.ONE_AND_HALF),
            Pair.of(AngleTransformationTest.ONE_AND_HALF, AngleTransformationTest.ONE))
        .forEach(
            pair -> {
              final Angle expected = pair.getLeft();
              final Angle actual =
                  ImmutableAngle.of(AngleTransformation.CLOCK.transform(pair.getRight().radians()));
              assertThat(actual, is(expected));
            });
  }
}
