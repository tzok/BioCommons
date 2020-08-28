package pl.poznan.put.atom;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BondTest {
  @Test
  public final void length() {
    final Bond.Length infinite = Bond.length(AtomType.H, AtomType.H);
    assertThat(Double.isInfinite(infinite.getMin()), is(true));
    assertThat(Double.isInfinite(infinite.getMax()), is(true));
    assertThat(Double.isInfinite(infinite.getAvg()), is(true));

    final Bond.Length carbonHydrogen = Bond.length(AtomType.H, AtomType.C);
    assertThat(carbonHydrogen.getMin(), is(1.07));
    assertThat(carbonHydrogen.getMax(), is(1.111));
    assertThat(carbonHydrogen.getAvg(), is(1.098));
  }
}
