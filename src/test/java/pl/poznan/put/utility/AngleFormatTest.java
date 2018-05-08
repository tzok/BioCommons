package pl.poznan.put.utility;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import pl.poznan.put.constant.Unicode;

public class AngleFormatTest {
  @Test
  public void degreesRoundedToOne() {
    assertEquals("NaN", AngleFormat.degreesRoundedToOne(Double.NaN));
    assertEquals(Unicode.INFINITY, AngleFormat.degreesRoundedToOne(Double.POSITIVE_INFINITY));
    assertEquals("0", AngleFormat.degreesRoundedToOne(0.0));
    assertEquals("57" + Unicode.DEGREE, AngleFormat.degreesRoundedToOne(1.0));
    assertEquals("115" + Unicode.DEGREE, AngleFormat.degreesRoundedToOne(2.0));
    assertEquals("180" + Unicode.DEGREE, AngleFormat.degreesRoundedToOne(Math.PI));
  }

  @Test
  public void degreesRoundedToHundredth() {
    assertEquals("NaN", AngleFormat.degreesRoundedToHundredth(Double.NaN));
    assertEquals(Unicode.INFINITY, AngleFormat.degreesRoundedToHundredth(Double.POSITIVE_INFINITY));
    assertEquals("0", AngleFormat.degreesRoundedToHundredth(0.0));
    assertEquals("57.3" + Unicode.DEGREE, AngleFormat.degreesRoundedToHundredth(1.0));
    assertEquals("114.59" + Unicode.DEGREE, AngleFormat.degreesRoundedToHundredth(2.0));
    assertEquals("180" + Unicode.DEGREE, AngleFormat.degreesRoundedToHundredth(Math.PI));
  }

  @Test
  public void degrees() {
    assertEquals("NaN", AngleFormat.degrees(Double.NaN));
    assertEquals("Infinity", AngleFormat.degrees(Double.POSITIVE_INFINITY));
    assertEquals("0.0", AngleFormat.degrees(0.0));
    assertEquals("57.29577951308232", AngleFormat.degrees(1.0));
    assertEquals("114.59155902616465", AngleFormat.degrees(2.0));
    assertEquals("180.0", AngleFormat.degrees(Math.PI));
  }
}
