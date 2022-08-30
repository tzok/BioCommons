package pl.poznan.put.circular.samples;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.FastMath;
import org.junit.Before;
import org.junit.Test;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;

public class AngleSampleTest {
  private AngleSample angleSample;

  @Before
  public final void before() {
    /* Example 1.1 from Mardia & Jupp "Directional Statistics". */
    final double[] degrees = {43.0, 45.0, 52.0, 61.0, 75.0, 88.0, 88.0, 279.0, 357.0};

    final List<Angle> data =
        Arrays.stream(degrees)
            .mapToObj(v -> ImmutableAngle.of(FastMath.toRadians(v)))
            .collect(Collectors.toList());
    angleSample = ImmutableAngleSample.of(data);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testEmptySample() {
    ImmutableAngleSample.of(Collections.emptyList());
  }

  @Test
  public final void testMeanDirection() {
    /* Example 2.1 from Mardia & Jupp "Directional Statistics". */
    assertThat(angleSample.meanDirection(), is(ImmutableAngle.of(FastMath.toRadians(51.05))));
    assertThat(FastMath.abs(angleSample.meanResultantLength() - 0.711) < 1.0e-3, is(true));
  }

  @Test
  public final void testMedian() {
    /* Example 2.2 from Mardia & Jupp "Directional Statistics". */
    assertThat(angleSample.medianDirection(), is(ImmutableAngle.of(FastMath.toRadians(52))));
  }

  @Test
  public final void testMedianWithEvenPositiveObserations() {
    final double[] degrees = {0.0, 10.0, 20.0, 30.0};
    final List<Angle> data =
        Arrays.stream(degrees)
            .mapToObj(v -> ImmutableAngle.of(FastMath.toRadians(v)))
            .collect(Collectors.toList());
    final AngleSample sample = ImmutableAngleSample.of(data);
    assertThat(sample.medianDirection(), is(ImmutableAngle.of(FastMath.toRadians(15.0))));
  }

  @Test
  public final void testMedianWithEvenNegativeObserations() {
    final double[] degrees = {0.0, -10.0, -20.0, -30.0};
    final List<Angle> data =
        Arrays.stream(degrees)
            .mapToObj(v -> ImmutableAngle.of(FastMath.toRadians(v)))
            .collect(Collectors.toList());
    final AngleSample sample = ImmutableAngleSample.of(data);
    assertThat(sample.medianDirection(), is(ImmutableAngle.of(FastMath.toRadians(-15.0))));
  }

  @Test
  public final void testMedianWithEvenMixedObserations() {
    final double[] degrees = {-10.0, 0.0, 10.0, 20.0};
    final List<Angle> data =
        Arrays.stream(degrees)
            .mapToObj(v -> ImmutableAngle.of(FastMath.toRadians(v)))
            .collect(Collectors.toList());
    final AngleSample sample = ImmutableAngleSample.of(data);
    assertThat(sample.medianDirection(), is(ImmutableAngle.of(FastMath.toRadians(5.0))));
  }
}
