package pl.poznan.put.pdb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

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
  public final void testParseToString() {
    final PdbHeaderLine parsed = PdbHeaderLine.parse(PdbHeaderLineTest.VALID_LINE);
    final String parsedToString = parsed.toString();
    assertThat(parsedToString, is(PdbHeaderLineTest.VALID_LINE));
  }

  @Test(expected = PdbParsingException.class)
  public final void testShortLine() {
    PdbHeaderLine.parse(PdbHeaderLineTest.TOO_SHORT_LINE);
  }

  @Test(expected = PdbParsingException.class)
  public final void testMisalignedLine() {
    PdbHeaderLine.parse(PdbHeaderLineTest.MISALIGNED_LINE);
  }
}
