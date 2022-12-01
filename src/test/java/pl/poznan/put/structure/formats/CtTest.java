package pl.poznan.put.structure.formats;

import org.junit.Test;
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
          + "3 U 0 4 1 12314\n"
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

  private static final String INPUT_BAD_INDEX_3 =
      "4 inputBad\n"
          + "1 A 0 2 3 12313\n"
          + "3 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_BEFORE_1 =
      "4 inputBad\n"
          + "1 A 0 2 3 12313\n"
          + "2 C 1 0 0 12313\n"
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
          + "1 A 0 2 3 12313\n"
          + "2 C 1 0 0 12313\n"
          + "3 U 1 2 1 12314\n"
          + "4 C 1 0 0 12315\n";
  private static final String INPUT_BAD_AFTER_1 =
      "4 inputBad\n"
          + "1 A 0 xyz 3 12313\n"
          + "2 C 1 3 0 12313\n"
          + "3 U 2 4 1 12314\n"
          + "4 C 3 0 0 12315\n";
  private static final String INPUT_BAD_AFTER_2 =
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
  public final void testGood() {
    Ct.fromString(CtTest.INPUT_GOOD);
  }

  @Test
  public final void testGoodMulti() {
    Ct.fromString(CtTest.INPUT_GOOD_MULTI);
  }

  @Test
  public final void testCRW00528() throws Exception {
    Ct.fromString(ResourcesHelper.loadResource("CRW_00528.ct"));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidTooFew() {
    Ct.fromString(CtTest.INPUT_BAD_TOO_FEW);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidTooMany() {
    Ct.fromString(CtTest.INPUT_BAD_TOO_MANY);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadIndex1() {
    Ct.fromString(CtTest.INPUT_BAD_INDEX_1);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadIndex2() {
    Ct.fromString(CtTest.INPUT_BAD_INDEX_2);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadIndex3() {
    Ct.fromString(CtTest.INPUT_BAD_INDEX_3);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadBefore1() {
    Ct.fromString(CtTest.INPUT_BAD_BEFORE_1);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadBefore2() {
    Ct.fromString(CtTest.INPUT_BAD_BEFORE_2);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadBefore3() {
    Ct.fromString(CtTest.INPUT_BAD_BEFORE_3);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadAfter1() {
    Ct.fromString(CtTest.INPUT_BAD_AFTER_1);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadAfter2() {
    Ct.fromString(CtTest.INPUT_BAD_AFTER_2);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadPair1() {
    Ct.fromString(CtTest.INPUT_BAD_PAIR_1);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadPair2() {
    Ct.fromString(CtTest.INPUT_BAD_PAIR_2);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadOriginal() {
    Ct.fromString(CtTest.INPUT_BAD_ORIGINAL);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadNumbering() {
    Ct.fromString(CtTest.INPUT_BAD_NUMBERING);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testInvalidBadMapping() {
    Ct.fromString(CtTest.INPUT_BAD_MAPPING);
  }

  @Test
  public final void fromDotBracket() {
    Ct.fromDotBracket(DefaultDotBracket.fromString(DefaultDotBracketTest.FROM_2Z74));
  }

  @Test
  public final void test4UG0() throws Exception {
    final String dbn4UG0 = ResourcesHelper.loadResource("4UG0-dotbracket.txt");
    final DefaultDotBracket dotBracket = DefaultDotBracket.fromString(dbn4UG0);
    Ct.fromDotBracket(dotBracket);
  }

  @Test
  public final void test2Z74() throws Exception {
    final String ct2Z74 = ResourcesHelper.loadResource("2Z74.ct");
    Ct.fromString(ct2Z74);
  }

  @Test
  public final void testNDB00001() throws Exception {
    Ct.fromString(ResourcesHelper.loadResource("NDB_00001.ct"));
  }

  @Test
  public final void test3G78() throws Exception {
    Ct.fromString(ResourcesHelper.loadResource("3G78.ct"));
  }
}
