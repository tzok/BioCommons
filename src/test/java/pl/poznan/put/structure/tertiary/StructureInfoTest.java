package pl.poznan.put.structure.tertiary;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class StructureInfoTest {
  @Test
  public final void compareTo() {
    final StructureInfo a = new StructureInfo(null, null, "A");
    final StructureInfo b = new StructureInfo(null, null, "B");
    final StructureInfo c = new StructureInfo(null, null, "C");
    Assert.assertThat(a.compareTo(a), is(0));
    Assert.assertThat(b.compareTo(b), is(0));
    Assert.assertThat(c.compareTo(c), is(0));

    Assert.assertThat(a.compareTo(b), lessThan(0));
    Assert.assertThat(a.compareTo(c), lessThan(0));

    Assert.assertThat(b.compareTo(a), greaterThan(0));
    Assert.assertThat(b.compareTo(c), lessThan(0));

    Assert.assertThat(c.compareTo(a), greaterThan(0));
    Assert.assertThat(c.compareTo(b), greaterThan(0));
  }
}
