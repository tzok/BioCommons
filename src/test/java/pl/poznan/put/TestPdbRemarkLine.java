package pl.poznan.put;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemarkLine;

public class TestPdbRemarkLine {
    private final String validLine = "REMARK   3   CROSS-VALIDATION METHOD          : THROUGHOUT                      ";
    private final String tooShortLine = validLine.substring(0, validLine.length() - 10);
    private final String misalignedLine = StringUtils.normalizeSpace(validLine);

    @Test
    public void testParseToString() throws PdbParsingException {
        PdbRemarkLine parsed = PdbRemarkLine.parse(validLine);
        String parsedToString = parsed.toString();
        assertEquals(validLine, parsedToString);
    }

    @Test(expected = PdbParsingException.class)
    public void testShortLine() throws PdbParsingException {
        PdbRemarkLine.parse(tooShortLine);
    }

    @Test(expected = PdbParsingException.class)
    public void testMisalignedLine() throws PdbParsingException {
        PdbRemarkLine.parse(misalignedLine);
    }
}
