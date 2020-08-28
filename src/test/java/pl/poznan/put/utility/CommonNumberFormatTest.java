package pl.poznan.put.utility;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Assert;
import org.junit.Test;

public class CommonNumberFormatTest {
  @Test
  public final void formatDouble() {
    assertThat(NumberFormatUtils.threeDecimalDigits().format(123.0), is("123.0"));
    assertThat(NumberFormatUtils.threeDecimalDigits().format(123.4), is("123.4"));
    assertThat(NumberFormatUtils.threeDecimalDigits().format(123.45), is("123.45"));
    assertThat(NumberFormatUtils.threeDecimalDigits().format(123.454), is("123.454"));
    assertThat(NumberFormatUtils.threeDecimalDigits().format(123.455), is("123.455"));
    assertThat(NumberFormatUtils.threeDecimalDigits().format(123.4554), is("123.455"));
    assertThat(NumberFormatUtils.threeDecimalDigits().format(123.4556), is("123.456"));
  }
}
