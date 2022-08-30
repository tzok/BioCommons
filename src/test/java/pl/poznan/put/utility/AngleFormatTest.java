package pl.poznan.put.utility;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import pl.poznan.put.constant.Unicode;

public class AngleFormatTest {
  @Test
  public final void degreesRoundedToOne() {
    assertThat(AngleFormat.degreesRoundedToOne(Double.NaN), is("NaN"));
    assertThat(AngleFormat.degreesRoundedToOne(Double.POSITIVE_INFINITY), is(Unicode.INFINITY));
    assertThat(AngleFormat.degreesRoundedToOne(0.0), is("0"));
    assertThat(AngleFormat.degreesRoundedToOne(1.0), is("57" + Unicode.DEGREE));
    assertThat(AngleFormat.degreesRoundedToOne(2.0), is("115" + Unicode.DEGREE));
    assertThat(AngleFormat.degreesRoundedToOne(Math.PI), is("180" + Unicode.DEGREE));
  }

  @Test
  public final void degreesRoundedToHundredth() {
    assertThat(AngleFormat.degreesRoundedToHundredth(Double.NaN), is("NaN"));
    assertThat(
        AngleFormat.degreesRoundedToHundredth(Double.POSITIVE_INFINITY), is(Unicode.INFINITY));
    assertThat(AngleFormat.degreesRoundedToHundredth(0.0), is("0"));
    assertThat(AngleFormat.degreesRoundedToHundredth(1.0), is("57.296" + Unicode.DEGREE));
    assertThat(AngleFormat.degreesRoundedToHundredth(2.0), is("114.592" + Unicode.DEGREE));
    assertThat(AngleFormat.degreesRoundedToHundredth(Math.PI), is("180.0" + Unicode.DEGREE));
  }

  @Test
  public final void degrees() {
    assertThat(AngleFormat.degrees(Double.NaN), is("NaN"));
    assertThat(AngleFormat.degrees(Double.POSITIVE_INFINITY), is("Infinity"));
    assertThat(AngleFormat.degrees(0.0), is("0.0"));
    assertThat(AngleFormat.degrees(1.0), is("57.29577951308232"));
    assertThat(AngleFormat.degrees(2.0), is("114.59155902616465"));
    assertThat(AngleFormat.degrees(Math.PI), is("180.0"));
  }
}
