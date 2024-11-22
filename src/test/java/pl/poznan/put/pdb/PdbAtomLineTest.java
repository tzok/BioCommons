package pl.poznan.put.pdb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class PdbAtomLineTest {
  // @formatter:off
  private static final String VALID_LINE =
      "ATOM      1  OP3   G A   1      50.193  51.190  50.534  1.00 99.85           O  ";
  private static final String VALID_LINE_WITH_ONE_LETTER_ATOM =
      "ATOM      2  P     G A   1      50.626  49.730  50.573  1.00100.19           P  ";
  private static final String VALID_LINE_WITH_FOUR_LETTER_ATOM =
      "ATOM      5 H5''   G A   1       1.706  -1.125  -0.755  1.00  0.00           H  ";
  // @formatter:on

  private static final String MISALIGNED_LINE =
      StringUtils.normalizeSpace(PdbAtomLineTest.VALID_LINE);

  @Test
  public final void testParseToString() {
    final PdbAtomLine atomLine = PdbAtomLine.parse(PdbAtomLineTest.VALID_LINE);
    final String atomLineString = atomLine.toString();
    assertThat(PdbAtomLineTest.VALID_LINE, is(atomLineString));
  }

  @Test
  public final void testParseToStringOneLetterAtom() {
    final PdbAtomLine atomLine = PdbAtomLine.parse(PdbAtomLineTest.VALID_LINE_WITH_ONE_LETTER_ATOM);
    final String atomLineString = atomLine.toString();
    assertThat(PdbAtomLineTest.VALID_LINE_WITH_ONE_LETTER_ATOM, is(atomLineString));
  }

  @Test(expected = PdbParsingException.class)
  public final void testMisalignedLine() {
    PdbAtomLine.parse(PdbAtomLineTest.MISALIGNED_LINE);
  }

  @Test
  public final void testParseToStringFourLetterAtom() {
    final PdbAtomLine atomLine =
        PdbAtomLine.parse(PdbAtomLineTest.VALID_LINE_WITH_FOUR_LETTER_ATOM);
    final String atomLineString = atomLine.toString();
    assertThat(PdbAtomLineTest.VALID_LINE_WITH_FOUR_LETTER_ATOM, is(atomLineString));
  }
}
