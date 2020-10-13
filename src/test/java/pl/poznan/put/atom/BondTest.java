package pl.poznan.put.atom;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BondTest {
  @Test
  public final void length() {
    final BondLength infinite = Bond.length(AtomType.H, AtomType.H);
    assertThat(Double.isInfinite(infinite.min()), is(true));
    assertThat(Double.isInfinite(infinite.max()), is(true));
    assertThat(Double.isInfinite(infinite.avg()), is(true));

    final BondLength carbonHydrogen = Bond.length(AtomType.H, AtomType.C);
    assertThat(carbonHydrogen.min(), is(1.07));
    assertThat(carbonHydrogen.max(), is(1.111));
    assertThat(carbonHydrogen.avg(), is(1.098));
  }
}
