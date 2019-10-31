package pl.poznan.put.notation;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BPhTest {

  @Test
  public final void fromString() {
    assertThat(BPh.fromString("testing"), is(BPh.UNKNOWN));
    assertThat(BPh.fromString("0BPh"), is(BPh._0));
  }
}
