package pl.poznan.put;

import org.junit.Test;
import pl.poznan.put.structure.secondary.formats.Ct;
import pl.poznan.put.structure.secondary.formats.DotBracket;
import pl.poznan.put.structure.secondary.formats.InvalidStructureException;
import pl.poznan.put.utility.ResourcesHelper;

public class CtTest {
  // @formatter:off
  private static final String INPUT_GOOD =
      "4 inputGood\n"
          + "1 A 0 2 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_GOOD_MULTI =
      "4 inputGood\n"
          + "1 A 0 2 3 12313\n"
          + "2 C 1 0 0 12313\n"
          + "3 U 0 2 1 12314\n"
          + "4 C 1 0 0 12315\n";
  private static final String INPUT_BAD_FIRST_LINE_1 =
      "-100 inputBad\n"
          + "1 A 0 2 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_FIRST_LINE_2 =
      "xyz inputBad\n"
          + "1 A 0 2 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_TOO_FEW =
      "4 inputBad\n"
          + "1 A 0 2 3\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_TOO_MANY =
      "4 inputBad\n"
          + "1 A 0 2 3 12313 50\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_INDEX_1 =
      "4 inputBad\n"
          + "-10 A 0 2 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_INDEX_2 =
      "4 inputBad\n"
          + "xyz A 0 2 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_BEFORE_1 =
      "4 inputBad\n"
          + "1 A -1 2 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_BEFORE_2 =
      "4 inputBad\n"
          + "1 A xyz 2 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_BEFORE_3 =
      "4 inputBad\n"
          + "1 A 1 2 3 12313\n"
          + "2 C 1 0 0 12313\n"
          + "3 U 0 2 1 12314\n"
          + "4 C 1 0 0 12315\n";
  private static final String INPUT_BAD_BEFORE_4 =
      "4 inputBad\n"
          + "1 A 0 2 3 12313\n"
          + "2 C 1 0 0 12313\n"
          + "3 U 1 2 1 12314\n"
          + "4 C 1 0 0 12315\n";
  private static final String INPUT_BAD_AFTER_1 =
      "4 inputBad\n"
          + "1 A 0 -1 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_AFTER_2 =
      "4 inputBad\n"
          + "1 A 0 xyz 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_AFTER_3 =
      "4 inputBad\n"
          + "1 A 0 2 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 0 2 1 12314\n"
          + "4 C 1 0 0 12315\n";
  private static final String INPUT_BAD_PAIR_1 =
      "4 inputBad\n"
          + "1 A 0 2 -1 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_PAIR_2 =
      "4 inputBad\n"
          + "1 A 0 2 xyz 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_ORIGINAL =
      "4 inputBad\n"
          + "1 A 0 2 3 xyz\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_NUMBERING =
      "4 inputBad\n"
          + "1 A 0 2 3 12313\n"
          + "3 C 1 3 0 12313\n"
          + "2 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_MAPPING =
      "4 inputBad\n"
          + "1 A 0 2 2 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  // @formatter:on

  @Test
  public final void testGood() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_GOOD);
  }

  @Test
  public final void testGoodMulti() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_GOOD_MULTI);
  }

  @Test
  public final void testRnaStrand() throws Exception {
    final String data = ResourcesHelper.loadResource("CRW_00528.ct");
    Ct.fromString(data);
  }

  @Test
  public final void testX3Dna() throws Exception {
    final String data = ResourcesHelper.loadResource("3CC2-x3dna.ct");
    Ct.fromString(data);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidFirstLine1() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_FIRST_LINE_1);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidFirstLine2() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_FIRST_LINE_2);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidTooFew() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_TOO_FEW);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidTooMany() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_TOO_MANY);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadIndex1() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_INDEX_1);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadIndex2() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_INDEX_2);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadBefore1() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_BEFORE_1);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadBefore2() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_BEFORE_2);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadBefore3() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_BEFORE_3);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadBefore4() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_BEFORE_4);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadAfter1() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_AFTER_1);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadAfter2() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_AFTER_2);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadAfter3() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_AFTER_3);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadPair1() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_PAIR_1);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadPair2() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_PAIR_2);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadOriginal() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_ORIGINAL);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadNumbering() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_NUMBERING);
  }

  @Test(expected = InvalidStructureException.class)
  public final void testInvalidBadMapping() throws InvalidStructureException {
    Ct.fromString(CtTest.INPUT_BAD_MAPPING);
  }

  @Test
  public final void fromDotBracket() throws InvalidStructureException {
    Ct.fromDotBracket(DotBracket.fromString(DotBracketTest.FROM_2Z74));
  }

  @Test
  public final void test4UG0() throws Exception {
    final String dbn4UG0 = ResourcesHelper.loadResource("4UG0-dotbracket.txt");
    final DotBracket dotBracket = DotBracket.fromString(dbn4UG0);
    Ct.fromDotBracket(dotBracket);
  }
}