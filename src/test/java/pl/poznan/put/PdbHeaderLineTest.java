package pl.poznan.put;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbParsingException;

public class PdbHeaderLineTest {
  // @formatter:off
  private static final String VALID_LINE =
      "HEADER    RNA                                     23-FEB-00   1EHZ              ";
  // @formatter:on

  private static final String TOO_SHORT_LINE =
      PdbHeaderLineTest.VALID_LINE.substring(0, PdbHeaderLineTest.VALID_LINE.length() - 20);
  private static final String MISALIGNED_LINE =
      StringUtils.normalizeSpace(PdbHeaderLineTest.VALID_LINE);

  @Test
  public final void testParseToString() throws PdbParsingException {
    final PdbHeaderLine parsed = PdbHeaderLine.parse(PdbHeaderLineTest.VALID_LINE);
    final String parsedToString = parsed.toString();
    assertEquals(PdbHeaderLineTest.VALID_LINE, parsedToString);
  }

  @Test(expected = PdbParsingException.class)
  public final void testShortLine() throws PdbParsingException {
    PdbHeaderLine.parse(PdbHeaderLineTest.TOO_SHORT_LINE);
  }

  @Test(expected = PdbParsingException.class)
  public final void testMisalignedLine() throws PdbParsingException {
    PdbHeaderLine.parse(PdbHeaderLineTest.MISALIGNED_LINE);
  }
}
