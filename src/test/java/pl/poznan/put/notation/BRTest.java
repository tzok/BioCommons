package pl.poznan.put.notation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BRTest {

  @Test
  public void fromString() {
    assertEquals(BR.UNKNOWN, BR.fromString("testing"));
    assertEquals(BR._0, BR.fromString("0BR"));
  }
}
