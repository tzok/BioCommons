package pl.poznan.put.notation;

import org.junit.Test;

import static org.junit.Assert.*;

public class BPhTest {

  @Test
  public void fromString() {
    assertEquals(BPh.UNKNOWN, BPh.fromString("testing"));
    assertEquals(BPh._0, BPh.fromString("0BPh"));
  }
}