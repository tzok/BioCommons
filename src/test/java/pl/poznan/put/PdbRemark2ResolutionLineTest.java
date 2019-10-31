package pl.poznan.put;

import org.junit.Assert;
import org.junit.Test;
import pl.poznan.put.pdb.PdbRemark2Line;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

public class PdbRemark2ResolutionLineTest {
  // @formatter:off
  private static final String VALID_XRAY_LINE =
      "REMARK   2 RESOLUTION.    1.89 ANGSTROMS.                                       ";
  private static final String VALID_NMR_LINE =
      "REMARK   2 RESOLUTION. NOT APPLICABLE.                                          ";
  // @formatter:on

  @Test
  public final void testParseXray() {
    final PdbRemark2Line parsed =
        PdbRemark2Line.parse(PdbRemark2ResolutionLineTest.VALID_XRAY_LINE);
    assertEquals(1.89, parsed.getResolution(), 0.001);

    final String parsedToString = parsed.toString();
    Assert.assertThat(parsedToString, is(PdbRemark2ResolutionLineTest.VALID_XRAY_LINE));
  }

  @Test
  public final void testParseNmr() {
    final PdbRemark2Line parsed = PdbRemark2Line.parse(PdbRemark2ResolutionLineTest.VALID_NMR_LINE);
    Assert.assertThat(Double.isNaN(parsed.getResolution()), is(true));

    final String parsedToString = parsed.toString();
    Assert.assertThat(parsedToString, is(PdbRemark2ResolutionLineTest.VALID_NMR_LINE));
  }
}
