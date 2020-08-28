package pl.poznan.put.notation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BRTest {

  @Test
  public final void fromString() {
    assertThat(BR.fromString("testing"), is(BR.UNKNOWN));
    assertThat(BR.fromString("0BR"), is(BR._0));
  }
}
