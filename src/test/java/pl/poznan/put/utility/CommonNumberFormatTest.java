package pl.poznan.put.utility;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CommonNumberFormatTest {
  @Test
  public final void formatDouble() {
    assertEquals("123.0", NumberFormatUtils.threeDecimalDigits().format(123.0));
    assertEquals("123.4", NumberFormatUtils.threeDecimalDigits().format(123.4));
    assertEquals("123.45", NumberFormatUtils.threeDecimalDigits().format(123.45));
    assertEquals("123.454", NumberFormatUtils.threeDecimalDigits().format(123.454));
    assertEquals("123.455", NumberFormatUtils.threeDecimalDigits().format(123.455));
    assertEquals("123.455", NumberFormatUtils.threeDecimalDigits().format(123.4554));
    assertEquals("123.456", NumberFormatUtils.threeDecimalDigits().format(123.4556));
  }
}
