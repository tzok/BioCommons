package pl.poznan.put;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;

import static org.junit.Assert.assertEquals;

public class TestPdbModresLine {
    private final String validLine =
            "MODRES 1EHZ 2MG A   10    G  2N-METHYLGUANOSINE-5'-MONOPHOSPHATE"
            + "                ";
    private final String tooShortLine =
            validLine.substring(0, validLine.length() - 20);
    private final String misalignedLine = StringUtils.normalizeSpace(validLine);

    @Test
    public void testParseToString() throws PdbParsingException {
        PdbModresLine parsed = PdbModresLine.parse(validLine);
        String parsedToString = parsed.toString();
        assertEquals(validLine, parsedToString);
    }

    @Test(expected = PdbParsingException.class)
    public void testShortLine() throws PdbParsingException {
        PdbModresLine.parse(tooShortLine);
    }

    @Test(expected = PdbParsingException.class)
    public void testMisalignedLine() throws PdbParsingException {
        PdbModresLine.parse(misalignedLine);
    }
}
