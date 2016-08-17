package pl.poznan.put;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbParsingException;

import static org.junit.Assert.assertEquals;

public class TestPdbHeaderLine {
    private final String validLine =
            "HEADER    RNA                                     23-FEB-00   "
            + "1EHZ              ";
    private final String tooShortLine =
            validLine.substring(0, validLine.length() - 20);
    private final String misalignedLine = StringUtils.normalizeSpace(validLine);

    @Test
    public void testParseToString() throws PdbParsingException {
        PdbHeaderLine parsed = PdbHeaderLine.parse(validLine);
        String parsedToString = parsed.toString();
        assertEquals(validLine, parsedToString);
    }

    @Test(expected = PdbParsingException.class)
    public void testShortLine() throws PdbParsingException {
        PdbHeaderLine.parse(tooShortLine);
    }

    @Test(expected = PdbParsingException.class)
    public void testMisalignedLine() throws PdbParsingException {
        PdbHeaderLine.parse(misalignedLine);
    }
}
