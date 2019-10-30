package pl.poznan.put.notation;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class BPhTest {

  @Test
  public final void fromString() {
    assertThat(BPh.fromString("testing"), is(BPh.UNKNOWN));
    assertThat(BPh.fromString("0BPh"), is(BPh._0));
  }
}