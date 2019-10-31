package pl.poznan.put.circular.samples;

import org.junit.Test;

import java.util.Collections;

public class AngleSampleTest {
  @Test(expected = IllegalArgumentException.class)
  public final void testEmptySample() {
    new AngleSample(Collections.emptyList());
  }
}
