package pl.poznan.put;

import org.junit.Test;
import pl.poznan.put.pdb.PdbRemark2Line;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
    assertThat(parsed.resolution(), is(1.89));

    final String parsedToString = parsed.toString();
    assertThat(parsedToString, is(PdbRemark2ResolutionLineTest.VALID_XRAY_LINE));
  }

  @Test
  public final void testParseNmr() {
    final PdbRemark2Line parsed = PdbRemark2Line.parse(PdbRemark2ResolutionLineTest.VALID_NMR_LINE);
    assertThat(Double.isNaN(parsed.resolution()), is(true));

    final String parsedToString = parsed.toString();
    assertThat(parsedToString, is(PdbRemark2ResolutionLineTest.VALID_NMR_LINE));
  }
}
