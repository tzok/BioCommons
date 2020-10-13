package pl.poznan.put;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark465Line;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PdbRemark465LineTest {
  // @formatter:off
  private static final String VALID_LINE =
      "REMARK 465       C 0   126                                                      ";
  private static final String COMMENT_LINE_1 =
      "REMARK 465 THE FOLLOWING RESIDUES WERE NOT LOCATED IN THE                       ";
  private static final String COMMENT_LINE_2 =
      "REMARK 465 IDENTIFIER; SSSEQ=SEQUENCE NUMBER; I=INSERTION CODE.)                ";
  // @formatter:on

  private static final String TOO_SHORT_LINE =
      PdbRemark465LineTest.VALID_LINE.substring(0, PdbRemark465LineTest.VALID_LINE.length() - 10);
  private static final String MISALIGNED_LINE =
      StringUtils.normalizeSpace(PdbRemark465LineTest.VALID_LINE);

  @Test
  public final void testParseToString() {
    final PdbRemark465Line parsed = PdbRemark465Line.parse(PdbRemark465LineTest.VALID_LINE);
    final String parsedToString = parsed.toString();
    assertThat(PdbRemark465Line.isCommentLine(PdbRemark465LineTest.VALID_LINE), is(false));
    assertThat(parsedToString, is(PdbRemark465LineTest.VALID_LINE));
  }

  @Test(expected = PdbParsingException.class)
  public final void testShortLine() {
    assertThat(PdbRemark465Line.isCommentLine(PdbRemark465LineTest.TOO_SHORT_LINE), is(false));
    PdbRemark465Line.parse(PdbRemark465LineTest.TOO_SHORT_LINE);
  }

  @Test(expected = PdbParsingException.class)
  public final void testMisalignedLine() {
    assertThat(PdbRemark465Line.isCommentLine(PdbRemark465LineTest.MISALIGNED_LINE), is(false));
    PdbRemark465Line.parse(PdbRemark465LineTest.MISALIGNED_LINE);
  }

  @Test
  public final void testCommentLines() {
    assertThat(PdbRemark465Line.isCommentLine(PdbRemark465LineTest.COMMENT_LINE_1), is(true));
    assertThat(PdbRemark465Line.isCommentLine(PdbRemark465LineTest.COMMENT_LINE_2), is(true));
  }
}
