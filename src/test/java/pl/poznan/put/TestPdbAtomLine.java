package pl.poznan.put;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbParsingException;

public class TestPdbAtomLine {
    private final String validLine = "ATOM      1  OP3   G A   1      50.193  51.190  50.534  1.00 99.85           O  ";
    private final String tooShortLine = validLine.substring(0, validLine.length() - 10);
    private final String misalignedLine = StringUtils.normalizeSpace(validLine);

    @Test
    public void testParseToString() throws PdbParsingException {
        PdbAtomLine atomLine = PdbAtomLine.parse(validLine);
        String atomLineString = atomLine.toString();
        assertEquals(validLine, atomLineString);
    }

    @Test(expected = PdbParsingException.class)
    public void testShortLine() throws PdbParsingException {
        PdbAtomLine.parse(tooShortLine);
    }

    @Test(expected = PdbParsingException.class)
    public void testMisalignedLine() throws PdbParsingException {
        PdbAtomLine.parse(misalignedLine);
    }
}
