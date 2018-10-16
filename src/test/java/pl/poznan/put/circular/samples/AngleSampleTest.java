package pl.poznan.put.circular.samples;

import java.util.Collections;
import org.junit.Test;

public class AngleSampleTest {
  @Test(expected = IllegalArgumentException.class)
  public final void testEmptySample() {
    new AngleSample(Collections.emptyList());
  }
}
