package pl.poznan.put;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.DotBracket;
import pl.poznan.put.structure.secondary.formats.InvalidSecondaryStructureException;

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
    // @formatter:off
    
    @SuppressWarnings("static-method")
    @Test
    public void from2Z74() throws InvalidSecondaryStructureException {
        DotBracket dotBracket = DotBracket.fromString(TestDotBracket.FROM_2Z74);
        assertEquals(2, dotBracket.getStrands().size());
    }

    @SuppressWarnings("static-method")
    @Test
    public void fromBpSeq() throws InvalidSecondaryStructureException {
        BpSeq bpSeq = BpSeq.fromString(TestDotBracket.BPSEQ);
        DotBracket dotBracketFromBpSeq = DotBracket.fromBpSeq(bpSeq);
        DotBracket dotBracketFromString = DotBracket.fromString(TestDotBracket.DOTBRACKET);
        assertEquals(dotBracketFromString, dotBracketFromBpSeq);
    }
}
