package pl.poznan.put.atom;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BondTest {
  @Test
  public final void length() {
    final Bond.Length infinite = Bond.length(AtomType.H, AtomType.H);
    assertTrue(Double.isInfinite(infinite.getMin()));
    assertTrue(Double.isInfinite(infinite.getMax()));
    assertTrue(Double.isInfinite(infinite.getAvg()));

    final Bond.Length carbonHydrogen = Bond.length(AtomType.H, AtomType.C);
    assertEquals(1.07, carbonHydrogen.getMin(), 1.0e-3);
    assertEquals(1.111, carbonHydrogen.getMax(), 1.0e-3);
    assertEquals(1.098, carbonHydrogen.getAvg(), 1.0e-3);
  }
}
