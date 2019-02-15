package pl.poznan.put.notation;

import org.junit.Test;

import static org.junit.Assert.*;

public class LeontisWesthofTest {
  @Test
  public void fromString() {
    assertEquals(LeontisWesthof.CWW, LeontisWesthof.fromString("cww"));
    assertEquals(LeontisWesthof.CWW, LeontisWesthof.fromString("CwW"));
  }

  @Test
  public final void invert() {
    assertEquals(LeontisWesthof.CSH, LeontisWesthof.CHS.invert());
  }
}