package pl.poznan.put.notation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class BPhTest {

  @Test
  public final void fromString() {
    assertThat(BPh.fromString("testing"), is(BPh.UNKNOWN));
    assertThat(BPh.fromString("0BPh"), is(BPh._0));
  }
}
