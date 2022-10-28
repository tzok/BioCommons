package pl.poznan.put.notation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class StackingTopologyTest {
  @Test
  public void fromString() {
    assertThat(StackingTopology.fromString("testing"), is(StackingTopology.UNKNOWN));
    assertThat(StackingTopology.fromString("upward"), is(StackingTopology.UPWARD));
    assertThat(StackingTopology.fromString("downward"), is(StackingTopology.DOWNWARD));
    assertThat(StackingTopology.fromString("inward"), is(StackingTopology.INWARD));
    assertThat(StackingTopology.fromString("outward"), is(StackingTopology.OUTWARD));
  }

  @Test
  public void invert() {
    assertThat(StackingTopology.UPWARD.invert(), is(StackingTopology.DOWNWARD));
    assertThat(StackingTopology.UPWARD.invert().invert(), is(StackingTopology.UPWARD));
    assertThat(StackingTopology.INWARD.invert(), is(StackingTopology.OUTWARD));
    assertThat(StackingTopology.INWARD.invert().invert(), is(StackingTopology.INWARD));
  }
}
