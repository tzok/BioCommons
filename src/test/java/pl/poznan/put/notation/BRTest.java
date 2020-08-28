package pl.poznan.put.notation;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class BRTest {

  @Test
  public final void fromString() {
    Assert.assertThat(BR.fromString("testing"), is(BR.UNKNOWN));
    Assert.assertThat(BR.fromString("0BR"), is(BR._0));
  }
}
