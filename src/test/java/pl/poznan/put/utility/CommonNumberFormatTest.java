package pl.poznan.put.utility;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Assert;
import org.junit.Test;

public class CommonNumberFormatTest {
  @Test
  public final void formatDouble() {
    Assert.assertThat(NumberFormatUtils.threeDecimalDigits().format(123.0), is("123.0"));
    Assert.assertThat(NumberFormatUtils.threeDecimalDigits().format(123.4), is("123.4"));
    Assert.assertThat(NumberFormatUtils.threeDecimalDigits().format(123.45), is("123.45"));
    Assert.assertThat(NumberFormatUtils.threeDecimalDigits().format(123.454), is("123.454"));
    Assert.assertThat(NumberFormatUtils.threeDecimalDigits().format(123.455), is("123.455"));
    Assert.assertThat(NumberFormatUtils.threeDecimalDigits().format(123.4554), is("123.455"));
    Assert.assertThat(NumberFormatUtils.threeDecimalDigits().format(123.4556), is("123.456"));
  }
}
