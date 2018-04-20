package pl.poznan.put.structure.tertiary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StructureInfoTest {
  @Test
  public final void compareTo() {
    final StructureInfo a = new StructureInfo(null, null, "A");
    final StructureInfo b = new StructureInfo(null, null, "B");
    final StructureInfo c = new StructureInfo(null, null, "C");
    assertEquals(0, a.compareTo(a));
    assertEquals(0, b.compareTo(b));
    assertEquals(0, c.compareTo(c));

    assertTrue(a.compareTo(b) < 0);
    assertTrue(a.compareTo(c) < 0);

    assertTrue(b.compareTo(a) > 0);
    assertTrue(b.compareTo(c) < 0);

    assertTrue(c.compareTo(a) > 0);
    assertTrue(c.compareTo(b) > 0);
  }
}
