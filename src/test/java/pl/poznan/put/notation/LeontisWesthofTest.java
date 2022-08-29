package pl.poznan.put.notation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class LeontisWesthofTest {
  @Test
  public final void fromString() {
    assertThat(LeontisWesthof.fromString("cww"), is(LeontisWesthof.CWW));
    assertThat(LeontisWesthof.fromString("CwW"), is(LeontisWesthof.CWW));
  }

  @Test
  public final void invert() {
    assertThat(LeontisWesthof.CHS.invert(), is(LeontisWesthof.CSH));
  }
}
