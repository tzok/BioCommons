package pl.poznan.put.circular.samples;

import org.apache.commons.math3.util.FastMath;
import org.junit.Before;
import org.junit.Test;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.ValueType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AngleSampleTest {
  private AngleSample angleSample;

  @Before
  public final void before() {
    /* Example 1.1 from Mardia & Jupp "Directional Statistics". */
    final double[] degrees = {43.0, 45.0, 52.0, 61.0, 75.0, 88.0, 88.0, 279.0, 357.0};

    final List<Angle> data =
        Arrays.stream(degrees)
            .mapToObj(v -> new Angle(v, ValueType.DEGREES))
            .collect(Collectors.toList());
    angleSample = new AngleSample(data);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testEmptySample() {
    new AngleSample(Collections.emptyList());
  }

  @Test
  public final void testMeanDirection() {
    /* Example 2.1 from Mardia & Jupp "Directional Statistics". */
    assertThat(angleSample.getMeanDirection(), is(new Angle(51, ValueType.DEGREES)));
    assertThat(FastMath.abs(angleSample.getMeanResultantLength() - 0.711) < 1.0e-3, is(true));
  }

  @Test
  public final void testMedian() {
    /* Example 2.2 from Mardia & Jupp "Directional Statistics". */
    assertThat(angleSample.getMedianDirection(), is(new Angle(52, ValueType.DEGREES)));
  }

  @Test
  public final void testMedianWithEvenPositiveObserations() {
    final double[] degrees = {0.0, 10.0, 20.0, 30.0};
    final List<Angle> data =
        Arrays.stream(degrees)
            .mapToObj(v -> new Angle(v, ValueType.DEGREES))
            .collect(Collectors.toList());
    final AngleSample sample = new AngleSample(data);
    assertThat(sample.getMedianDirection(), is(new Angle(15.0, ValueType.DEGREES)));
  }

  @Test
  public final void testMedianWithEvenNegativeObserations() {
    final double[] degrees = {0.0, -10.0, -20.0, -30.0};
    final List<Angle> data =
        Arrays.stream(degrees)
            .mapToObj(v -> new Angle(v, ValueType.DEGREES))
            .collect(Collectors.toList());
    final AngleSample sample = new AngleSample(data);
    assertThat(sample.getMedianDirection(), is(new Angle(-15.0, ValueType.DEGREES)));
  }

  @Test
  public final void testMedianWithEvenMixedObserations() {
    final double[] degrees = {-10.0, 0.0, 10.0, 20.0};
    final List<Angle> data =
        Arrays.stream(degrees)
            .mapToObj(v -> new Angle(v, ValueType.DEGREES))
            .collect(Collectors.toList());
    final AngleSample sample = new AngleSample(data);
    assertThat(sample.getMedianDirection(), is(new Angle(5.0, ValueType.DEGREES)));
  }
}
