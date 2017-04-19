package pl.poznan.put;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.DotBracket;
import pl.poznan.put.structure.secondary.formats.InvalidStructureException;
import pl.poznan.put.structure.secondary.formats.LevelByLevelConverter;
import pl.poznan.put.structure.secondary.pseudoknots.elimination.MinGain;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class TestDotBracket {
    // @formatter:off
    public static final String FROM_2Z74 = 
            ">strand_A\n" +
            "aGCGCCuGGACUUAAAGCCAUUGCACU\n" + 
            "..[[[[.[(((((((((((..------\n" +
            ">strand_B\n" +
            "CCGGCUUUAAGUUGACGAGGGCAGGGUUuAUCGAGACAUCGGCGGGUGCCCUGCGGUCUUCCUGCGACCGUUAGAGGACUGGuAAAACCACAGGCGACUGUGGCAUAGAGCAGUCCGGGCAGGAA\n" +
            "--)))))))))))..[[[...((((((...]]]......]]]]]...))))))[[[[[.((((((]]]]].....((((((......((((((....)))))).......))))))..)))))).";
    private static final String BPSEQ =
            "1 C 11\n" +
            "2 C 9\n" +
            "3 C 0\n" +
            "4 C 13\n" +
            "5 C 10\n" +
            "6 C 0\n" +
            "7 C 12\n" +
            "8 C 0\n" +
            "9 C 2\n" +
            "10 C 5\n" +
            "11 C 1\n" +
            "12 C 7\n" +
            "13 C 4";
    private static final String DOTBRACKET =
            ">strand_1" +
            "CCCCCCCCCCCCC\n" +
            "((.[[.{.)])}]";
    // @formatter:on

    private String bpseq1EHZ;
    private String dotBracket1EHZ;

    @Before
    public void loadPdbFile() throws URISyntaxException, IOException {
        URI uri = getClass().getClassLoader().getResource(".").toURI();
        File dir = new File(uri);
        bpseq1EHZ = FileUtils.readFileToString(
                new File(dir, "../../src/test/resources/1EHZ-2D-bpseq.txt"),
                "utf-8");
        dotBracket1EHZ = FileUtils.readFileToString(new File(dir,
                                                             "../../src/test/resources/1EHZ-2D-dotbracket.txt"),
                                                    "utf-8");
    }

    @SuppressWarnings("static-method")
    @Test
    public void from2Z74() throws InvalidStructureException {
        DotBracket dotBracket = DotBracket.fromString(TestDotBracket.FROM_2Z74);
        assertEquals(2, dotBracket.getStrands().size());
    }

    @SuppressWarnings("static-method")
    @Test
    public void fromBpSeq() throws InvalidStructureException {
        LevelByLevelConverter converter =
                new LevelByLevelConverter(new MinGain(), 1);
        BpSeq bpSeq = BpSeq.fromString(TestDotBracket.BPSEQ);
        DotBracket dotBracketFromBpSeq = converter.convert(bpSeq);
        DotBracket dotBracketFromString =
                DotBracket.fromString(TestDotBracket.DOTBRACKET);
        assertEquals(dotBracketFromString, dotBracketFromBpSeq);
    }

    @Test
    public void fromBpSeq1EHZ() throws InvalidStructureException {
        LevelByLevelConverter converter =
                new LevelByLevelConverter(new MinGain(), 1);
        BpSeq bpSeq = BpSeq.fromString(bpseq1EHZ);
        DotBracket dotBracketFromBpSeq = converter.convert(bpSeq);
        DotBracket dotBracketFromString = DotBracket.fromString(dotBracket1EHZ);
        assertEquals(dotBracketFromString, dotBracketFromBpSeq);
    }
}
