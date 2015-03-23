package pl.poznan.put;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import pl.poznan.put.structure.secondary.formats.Ct;
import pl.poznan.put.structure.secondary.formats.DotBracket;
import pl.poznan.put.structure.secondary.formats.InvalidSecondaryStructureException;

public class TestCt {
    //@formatter:off
    private static String inputGood = 
                    "4 inputGood\n" + 
                    "1 A 0 2 3 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputGoodMulti = 
                    "4 inputGood\n" + 
                    "1 A 0 2 3 12313\n" + 
                    "2 C 1 0 0 12313\n" + 
                    "3 U 0 2 1 12314\n" + 
                    "4 C 1 0 0 12315\n";    
    private static String inputBadFirstLine1 = 
                    "-100 inputBad\n" + 
                    "1 A 0 2 3 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadFirstLine2 = 
                    "xyz inputBad\n" + 
                    "1 A 0 2 3 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadTooFew = 
                    "4 inputBad\n" + 
                    "1 A 0 2 3\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadTooMany = 
                    "4 inputBad\n" + 
                    "1 A 0 2 3 12313 50\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadIndex1 = 
                    "4 inputBad\n" + 
                    "-10 A 0 2 3 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadIndex2 = 
                    "4 inputBad\n" + 
                    "xyz A 0 2 3 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadBefore1 = 
                    "4 inputBad\n" + 
                    "1 A -1 2 3 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadBefore2 = 
                    "4 inputBad\n" + 
                    "1 A xyz 2 3 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadBefore3 = 
                    "4 inputBad\n" + 
                    "1 A 1 2 3 12313\n" + 
                    "2 C 1 0 0 12313\n" + 
                    "3 U 0 2 1 12314\n" + 
                    "4 C 1 0 0 12315\n";    
    private static String inputBadBefore4 = 
                    "4 inputBad\n" + 
                    "1 A 0 2 3 12313\n" + 
                    "2 C 1 0 0 12313\n" + 
                    "3 U 1 2 1 12314\n" + 
                    "4 C 1 0 0 12315\n";    
    private static String inputBadAfter1 = 
                    "4 inputBad\n" + 
                    "1 A 0 -1 3 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadAfter2 = 
                    "4 inputBad\n" + 
                    "1 A 0 xyz 3 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadAfter3 = 
                    "4 inputBad\n" + 
                    "1 A 0 2 3 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 0 2 1 12314\n" + 
                    "4 C 1 0 0 12315\n";    
    private static String inputBadPair1 = 
                    "4 inputBad\n" + 
                    "1 A 0 2 -1 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadPair2 = 
                    "4 inputBad\n" + 
                    "1 A 0 2 xyz 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadOriginal = 
                    "4 inputBad\n" + 
                    "1 A 0 2 3 xyz\n" + 
                    "2 C 1 3 0 12313\n" +
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadNumbering = 
                    "4 inputBad\n" + 
                    "1 A 0 2 3 12313\n" + 
                    "3 C 1 3 0 12313\n" + 
                    "2 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    private static String inputBadMapping = 
                    "4 inputBad\n" + 
                    "1 A 0 2 2 12313\n" + 
                    "2 C 1 3 0 12313\n" + 
                    "3 U 2 4 1 12314\n" + 
                    "4 C 3 0 0 12315\n";
    //@formatter:on

    @SuppressWarnings("static-method")
    @Test
    public void testGood() throws InvalidSecondaryStructureException {
        Ct.fromString(TestCt.inputGood);
    }

    @SuppressWarnings("static-method")
    @Test
    public void testGoodMulti() throws InvalidSecondaryStructureException {
        Ct.fromString(TestCt.inputGoodMulti);
    }

    @Test
    public void testRnaStrand() throws InvalidSecondaryStructureException, URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource(".").toURI();
        File dir = new File(uri);
        File ctFile = new File(dir, "../../src/test/resources/CRW_00528.ct");

        String data = FileUtils.readFileToString(ctFile, "utf-8");
        Ct.fromString(data);
    }

    @Test
    public void testX3Dna() throws InvalidSecondaryStructureException, URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource(".").toURI();
        File dir = new File(uri);
        File ctFile = new File(dir, "../../src/test/resources/3CC2-x3dna.ct");

        String data = FileUtils.readFileToString(ctFile, "utf-8");
        Ct.fromString(data);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidFirstLine1() throws InvalidSecondaryStructureException {
        Ct.fromString(TestCt.inputBadFirstLine1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidFirstLine2() throws InvalidSecondaryStructureException {
        Ct.fromString(TestCt.inputBadFirstLine2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidTooFew() throws InvalidSecondaryStructureException {
        Ct.fromString(TestCt.inputBadTooFew);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidTooMany() throws InvalidSecondaryStructureException {
        Ct.fromString(TestCt.inputBadTooMany);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadIndex1() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadIndex1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadIndex2() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadIndex2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadBefore1() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadBefore1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadBefore2() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadBefore2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadBefore3() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadBefore3);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadBefore4() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadBefore4);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadAfter1() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadAfter1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadAfter2() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadAfter2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadAfter3() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadAfter3);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadPair1() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadPair1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadPair2() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadPair2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadOriginal() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadOriginal);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadNumbering() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadNumbering);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidSecondaryStructureException.class)
    public void testInvalidBadMapping() throws InvalidSecondaryStructureException {
        Ct.fromString(inputBadMapping);
    }

    @SuppressWarnings("static-method")
    @Test
    public void fromDotBracket() throws InvalidSecondaryStructureException {
        DotBracket db = DotBracket.fromString(TestDotBracket.FROM_2Z74);
        db.toCt();
    }
}