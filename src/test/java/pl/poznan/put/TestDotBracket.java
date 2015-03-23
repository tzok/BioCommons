package pl.poznan.put;

import org.junit.Assert;
import org.junit.Test;

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
    // @formatter:off
    
    @SuppressWarnings("static-method")
    @Test
    public void from2Z74() throws InvalidSecondaryStructureException {
        DotBracket dotBracket = DotBracket.fromString(TestDotBracket.FROM_2Z74);
        Assert.assertEquals(2, dotBracket.getStrands().size());
    }
}
