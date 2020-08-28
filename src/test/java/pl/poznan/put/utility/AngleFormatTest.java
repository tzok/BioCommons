package pl.poznan.put.utility;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Assert;
import org.junit.Test;
import pl.poznan.put.constant.Unicode;

public class AngleFormatTest {
  @Test
  public final void degreesRoundedToOne() {
    Assert.assertThat(AngleFormat.degreesRoundedToOne(Double.NaN), is("NaN"));
    Assert.assertThat(AngleFormat.degreesRoundedToOne(Double.POSITIVE_INFINITY), is(Unicode.INFINITY));
    Assert.assertThat(AngleFormat.degreesRoundedToOne(0.0), is("0"));
    Assert.assertThat(AngleFormat.degreesRoundedToOne(1.0), is("57" + Unicode.DEGREE));
    Assert.assertThat(AngleFormat.degreesRoundedToOne(2.0), is("115" + Unicode.DEGREE));
    Assert.assertThat(AngleFormat.degreesRoundedToOne(Math.PI), is("180" + Unicode.DEGREE));
  }

  @Test
  public final void degreesRoundedToHundredth() {
    Assert.assertThat(AngleFormat.degreesRoundedToHundredth(Double.NaN), is("NaN"));
    Assert.assertThat(AngleFormat.degreesRoundedToHundredth(Double.POSITIVE_INFINITY), is(Unicode.INFINITY));
    Assert.assertThat(AngleFormat.degreesRoundedToHundredth(0.0), is("0"));
    Assert.assertThat(AngleFormat.degreesRoundedToHundredth(1.0), is("57.296" + Unicode.DEGREE));
    Assert.assertThat(AngleFormat.degreesRoundedToHundredth(2.0), is("114.592" + Unicode.DEGREE));
    Assert.assertThat(AngleFormat.degreesRoundedToHundredth(Math.PI), is("180.0" + Unicode.DEGREE));
  }

  @Test
  public final void degrees() {
    Assert.assertThat(AngleFormat.degrees(Double.NaN), is("NaN"));
    Assert.assertThat(AngleFormat.degrees(Double.POSITIVE_INFINITY), is("Infinity"));
    Assert.assertThat(AngleFormat.degrees(0.0), is("0.0"));
    Assert.assertThat(AngleFormat.degrees(1.0), is("57.29577951308232"));
    Assert.assertThat(AngleFormat.degrees(2.0), is("114.59155902616465"));
    Assert.assertThat(AngleFormat.degrees(Math.PI), is("180.0"));
  }
}
