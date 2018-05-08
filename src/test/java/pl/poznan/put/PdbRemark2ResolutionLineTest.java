package pl.poznan.put;

import org.junit.Test;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PdbRemark2ResolutionLineTest {
  // @formatter:off
  private static final String VALID_XRAY_LINE =
      "REMARK   2 RESOLUTION.    1.89 ANGSTROMS.                                       ";
  private static final String VALID_NMR_LINE =
      "REMARK   2 RESOLUTION. NOT APPLICABLE.                                          ";
  // @formatter:on

  @Test
  public final void testParseXray() throws PdbParsingException {
    final PdbRemark2Line parsed =
        PdbRemark2Line.parse(PdbRemark2ResolutionLineTest.VALID_XRAY_LINE);
    assertEquals(1.89, parsed.getResolution(), 0.001);

    final String parsedToString = parsed.toString();
    assertEquals(PdbRemark2ResolutionLineTest.VALID_XRAY_LINE, parsedToString);
  }

  @Test
  public final void testParseNmr() throws PdbParsingException {
    final PdbRemark2Line parsed = PdbRemark2Line.parse(PdbRemark2ResolutionLineTest.VALID_NMR_LINE);
    assertTrue(Double.isNaN(parsed.getResolution()));

    final String parsedToString = parsed.toString();
    assertEquals(PdbRemark2ResolutionLineTest.VALID_NMR_LINE, parsedToString);
  }
}
