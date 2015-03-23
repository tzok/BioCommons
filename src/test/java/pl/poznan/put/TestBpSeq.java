package pl.poznan.put;

import org.junit.Test;

import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.DotBracket;
import pl.poznan.put.structure.secondary.formats.InvalidSecondaryStructureException;

public class TestBpSeq {
    private static final String INPUT_GOOD_1 = "1 A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_GOOD_2 = "# Comment line\n" + "1 A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0 # Comment inline";
    private static final String INPUT_TOO_FEW = "1 A 0\n" + "2 C \n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_TOO_MANY = "1 A 0\n" + "2 C 3 1\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_TOO_LONG_SEQ = "1 ADE 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_INDEX_1 = "-1 A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_INDEX_2 = "xyz A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_PAIR_1 = "1 A -1\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_PAIR_2 = "1 A xyz\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_NUMBERING = "1 A 0\n" + "3 C 4\n" + "4 G 3\n" + "5 U 0";
    private static final String INPUT_SELF_PAIRED = "1 A 0\n" + "2 C 2\n" + "3 G 0\n" + "4 U 0";
    private static final String INPUT_MAPPING_1 = "1 A 0\n" + "2 C 5\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_MAPPING_2 = "1 A 0\n" + "2 C 3\n" + "3 G 4\n" + "4 U 3";

    @SuppressWarnings("static-method")
    @Test
    public void testGood() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_GOOD_1);
        BpSeq.fromString(INPUT_GOOD_2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testFew() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_TOO_FEW);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testMany() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_TOO_MANY);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testLongSeq() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_TOO_LONG_SEQ);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testIndex1() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_INDEX_1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testIndex2() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_INDEX_2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testPair1() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_PAIR_1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testPair2() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_PAIR_2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testNumbering() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_NUMBERING);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testSelfPaired() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_SELF_PAIRED);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testMapping1() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_MAPPING_1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testMapping2() throws InvalidSecondaryStructureException {
        BpSeq.fromString(INPUT_MAPPING_2);
    }

    @SuppressWarnings("static-method")
    @Test
    public void fromDotBracket() throws InvalidSecondaryStructureException {
        DotBracket db = DotBracket.fromString(TestDotBracket.FROM_2Z74);
        BpSeq.fromDotBracket(db);
    }
}
