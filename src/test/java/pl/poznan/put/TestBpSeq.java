package pl.poznan.put;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.Ct;
import pl.poznan.put.structure.secondary.formats.DotBracket;
import pl.poznan.put.structure.secondary.formats.InvalidStructureException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestBpSeq {
    private static final String INPUT_GOOD_1 =
            "1 A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_GOOD_2 =
            "# Comment line\n" + "1 A 0\n" + "2 C 3\n" + "3 G 2\n"
            + "4 U 0 # Comment inline";
    private static final String INPUT_TOO_FEW =
            "1 A 0\n" + "2 C \n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_TOO_MANY =
            "1 A 0\n" + "2 C 3 1\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_TOO_LONG_SEQ =
            "1 ADE 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_INDEX_1 =
            "-1 A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_INDEX_2 =
            "xyz A 0\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_PAIR_1 =
            "1 A -1\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_PAIR_2 =
            "1 A xyz\n" + "2 C 3\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_NUMBERING =
            "1 A 0\n" + "3 C 4\n" + "4 G 3\n" + "5 U 0";
    private static final String INPUT_SELF_PAIRED =
            "1 A 0\n" + "2 C 2\n" + "3 G 0\n" + "4 U 0";
    private static final String INPUT_MAPPING_1 =
            "1 A 0\n" + "2 C 5\n" + "3 G 2\n" + "4 U 0";
    private static final String INPUT_MAPPING_2 =
            "1 A 0\n" + "2 C 3\n" + "3 G 4\n" + "4 U 3";

    private String pdb1XPO;
    private String bpseq1XPO;

    @Before
    public void loadPdbFile() throws URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource(".").toURI();
        File dir = new File(uri);
        pdb1XPO = FileUtils.readFileToString(
                new File(dir, "../../src/test/resources/1XPO.pdb"), "utf-8");
        bpseq1XPO = FileUtils.readFileToString(
                new File(dir, "../../src/test/resources/1XPO.bpseq"), "utf-8");
    }

    @SuppressWarnings("static-method")
    @Test
    public void testGood() throws InvalidStructureException {
        BpSeq.fromString(INPUT_GOOD_1);
        BpSeq.fromString(INPUT_GOOD_2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testFew() throws InvalidStructureException {
        BpSeq.fromString(INPUT_TOO_FEW);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testMany() throws InvalidStructureException {
        BpSeq.fromString(INPUT_TOO_MANY);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testLongSeq() throws InvalidStructureException {
        BpSeq.fromString(INPUT_TOO_LONG_SEQ);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testIndex1() throws InvalidStructureException {
        BpSeq.fromString(INPUT_INDEX_1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testIndex2() throws InvalidStructureException {
        BpSeq.fromString(INPUT_INDEX_2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testPair1() throws InvalidStructureException {
        BpSeq.fromString(INPUT_PAIR_1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testPair2() throws InvalidStructureException {
        BpSeq.fromString(INPUT_PAIR_2);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testNumbering() throws InvalidStructureException {
        BpSeq.fromString(INPUT_NUMBERING);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testSelfPaired() throws InvalidStructureException {
        BpSeq.fromString(INPUT_SELF_PAIRED);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testMapping1() throws InvalidStructureException {
        BpSeq.fromString(INPUT_MAPPING_1);
    }

    @SuppressWarnings("static-method")
    @Test(expected = InvalidStructureException.class)
    public void testMapping2() throws InvalidStructureException {
        BpSeq.fromString(INPUT_MAPPING_2);
    }

    @SuppressWarnings("static-method")
    @Test
    public void fromDotBracket() throws InvalidStructureException {
        DotBracket db = DotBracket.fromString(TestDotBracket.FROM_2Z74);
        BpSeq.fromDotBracket(db);
    }

    @Test
    public void testManyChainsWithMissingResidues()
            throws PdbParsingException, InvalidStructureException {
        PdbParser parser = new PdbParser();
        List<PdbModel> models = parser.parse(pdb1XPO);
        assertEquals(1, models.size());
        PdbModel model = models.get(0);

        BpSeq bpSeq = BpSeq.fromString(bpseq1XPO);
        Ct.fromBpSeqAndPdbModel(bpSeq, model);
    }
}
