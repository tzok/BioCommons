package pl.poznan.put;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark465Line;

import static org.junit.Assert.assertEquals;

public class TestPdbRemark465Line {
    private final String validLine =
            "REMARK 465       C 0   126                                      "
            + "                ";
    private final String tooShortLine =
            validLine.substring(0, validLine.length() - 10);
    private final String misalignedLine = StringUtils.normalizeSpace(validLine);
    private final String commentLine1 =
            "REMARK 465 THE FOLLOWING RESIDUES WERE NOT LOCATED IN THE       "
            + "                ";
    private final String commentLine2 =
            "REMARK 465 IDENTIFIER; SSSEQ=SEQUENCE NUMBER; I=INSERTION CODE.)"
            + "                ";

    @Test
    public void testParseToString() throws PdbParsingException {
        PdbRemark465Line parsed = PdbRemark465Line.parse(validLine);
        String parsedToString = parsed.toString();
        assertEquals(false, PdbRemark465Line.isCommentLine(validLine));
        assertEquals(validLine, parsedToString);
    }

    @Test(expected = PdbParsingException.class)
    public void testShortLine() throws PdbParsingException {
        assertEquals(false, PdbRemark465Line.isCommentLine(tooShortLine));
        PdbRemark465Line.parse(tooShortLine);
    }

    @Test(expected = PdbParsingException.class)
    public void testMisalignedLine() throws PdbParsingException {
        assertEquals(false, PdbRemark465Line.isCommentLine(misalignedLine));
        PdbRemark465Line.parse(misalignedLine);
    }

    @Test
    public void testCommentLines() {
        assertEquals(true, PdbRemark465Line.isCommentLine(commentLine1));
        assertEquals(true, PdbRemark465Line.isCommentLine(commentLine2));
    }
}
