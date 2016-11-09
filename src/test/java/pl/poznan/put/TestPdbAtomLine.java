package pl.poznan.put;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbParsingException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestPdbAtomLine {
    // @formatter:off
    private static final String VALID_LINE =
            "ATOM      1  OP3   G A   1      50.193  51.190  50.534  1.00 99.85           O  ";
    private static final String VALID_LINE_WITH_ONE_LETTER_ATOM =
            "ATOM      2  P     G A   1      50.626  49.730  50.573  1.00100.19           P  ";
    private static final String VALID_LINE_WITH_FOUR_LETTER_ATOM =
            "ATOM      5 H5''   G A   1       1.706  -1.125  -0.755  1.00  0.00           H  ";
    private static final String MISSING_TEMP_FACTOR =
            "ATOM      1  N   GLU     1      42.189  22.849  47.437  1.00    N  ";
    // @formatter:on

    private final String tooShortLine = TestPdbAtomLine.VALID_LINE
            .substring(0, TestPdbAtomLine.VALID_LINE.length() - 10);
    private final String misalignedLine =
            StringUtils.normalizeSpace(TestPdbAtomLine.VALID_LINE);

    @Test
    public final void testParseToString() throws PdbParsingException {
        PdbAtomLine atomLine = PdbAtomLine.parse(TestPdbAtomLine.VALID_LINE);
        String atomLineString = atomLine.toString();
        assertThat(TestPdbAtomLine.VALID_LINE, is(atomLineString));
    }

    @Test
    public final void testParseToStringOneLetterAtom()
            throws PdbParsingException {
        PdbAtomLine atomLine = PdbAtomLine
                .parse(TestPdbAtomLine.VALID_LINE_WITH_ONE_LETTER_ATOM);
        String atomLineString = atomLine.toString();
        assertThat(TestPdbAtomLine.VALID_LINE_WITH_ONE_LETTER_ATOM,
                   is(atomLineString));
    }

    @Test(expected = PdbParsingException.class)
    public final void testShortLine() throws PdbParsingException {
        PdbAtomLine.parse(tooShortLine);
    }

    @Test(expected = PdbParsingException.class)
    public final void testMisalignedLine() throws PdbParsingException {
        PdbAtomLine.parse(misalignedLine);
    }

    @Test(expected = PdbParsingException.class)
    public final void testMissingTempFactor() throws PdbParsingException {
        PdbAtomLine.parse(TestPdbAtomLine.MISSING_TEMP_FACTOR);
    }

    @Test
    public final void testParseToStringFourLetterAtom()
            throws PdbParsingException {
        PdbAtomLine atomLine = PdbAtomLine
                .parse(TestPdbAtomLine.VALID_LINE_WITH_FOUR_LETTER_ATOM);
        String atomLineString = atomLine.toString();
        assertThat(TestPdbAtomLine.VALID_LINE_WITH_FOUR_LETTER_ATOM,
                   is(atomLineString));
    }
}
