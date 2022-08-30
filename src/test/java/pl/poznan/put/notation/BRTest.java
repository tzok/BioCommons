package pl.poznan.put.notation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class BRTest {

  @Test
  public final void fromString() {
    assertThat(BR.fromString("testing"), is(BR.UNKNOWN));
    assertThat(BR.fromString("0BR"), is(BR._0));
  }
}
