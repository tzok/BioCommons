package pl.poznan.put.circular.enums;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import pl.poznan.put.circular.Angle;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AngleTransformationTest {
  private static final Angle ZERO = new Angle(0.0, ValueType.DEGREES);
  private static final Angle HALF = new Angle(90.0, ValueType.DEGREES);
  private static final Angle ONE = new Angle(180.0, ValueType.DEGREES);
  private static final Angle ONE_AND_HALF = new Angle(270.0, ValueType.DEGREES);

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
                  new Angle(
                      AngleTransformation.MATH.transform(pair.getRight().getRadians()),
                      ValueType.RADIANS);
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
                  new Angle(
                      AngleTransformation.CLOCK.transform(pair.getRight().getRadians()),
                      ValueType.RADIANS);
              assertThat(actual, is(expected));
            });
  }
}
