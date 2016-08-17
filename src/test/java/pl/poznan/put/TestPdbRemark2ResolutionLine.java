package pl.poznan.put;

import org.junit.Test;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPdbRemark2ResolutionLine {
    private final String validXrayLine =
            "REMARK   2 RESOLUTION.    1.89 ANGSTROMS.                       "
            + "                ";
    private final String validNmrLine =
            "REMARK   2 RESOLUTION. NOT APPLICABLE.                          "
            + "                ";

    @Test
    public void testParseXray() throws PdbParsingException {
        PdbRemark2Line parsed = PdbRemark2Line.parse(validXrayLine);
        assertEquals(1.89, parsed.getResolution(), 0.001);

        String parsedToString = parsed.toString();
        assertEquals(validXrayLine, parsedToString);
    }

    @Test
    public void testParseNmr() throws PdbParsingException {
        PdbRemark2Line parsed = PdbRemark2Line.parse(validNmrLine);
        assertTrue(Double.isNaN(parsed.getResolution()));

        String parsedToString = parsed.toString();
        assertEquals(validNmrLine, parsedToString);
    }
}
