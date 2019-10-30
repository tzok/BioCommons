package pl.poznan.put.atom;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class BondTest {
  @Test
  public final void length() {
    final Bond.Length infinite = Bond.length(AtomType.H, AtomType.H);
    Assert.assertThat(Double.isInfinite(infinite.getMin()), is(true));
    Assert.assertThat(Double.isInfinite(infinite.getMax()), is(true));
    Assert.assertThat(Double.isInfinite(infinite.getAvg()), is(true));

    final Bond.Length carbonHydrogen = Bond.length(AtomType.H, AtomType.C);
    assertEquals(1.07, carbonHydrogen.getMin(), 1.0e-3);
    assertEquals(1.111, carbonHydrogen.getMax(), 1.0e-3);
    assertEquals(1.098, carbonHydrogen.getAvg(), 1.0e-3);
  }
}
