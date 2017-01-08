package pl.poznan.put;

import org.junit.Test;
import pl.poznan.put.pdb.ExperimentalTechnique;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbParsingException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static pl.poznan.put.pdb.PdbExpdtaLine.parse;

public class TestPdbExpdtaLine {
    private final String validXrayLine =
            "EXPDTA    X-RAY DIFFRACTION                                     "
            + "                ";
    private final String validNmrLine =
            "EXPDTA    SOLUTION NMR                                          "
            + "                ";
    private final String validThreeMethods =
            "EXPDTA    SOLID-STATE NMR; SOLUTION SCATTERING; ELECTRON "
            + "MICROSCOPY             ";
    private final String validWithSpace =
            "EXPDTA    SOLUTION NMR ; THEORETICAL MODEL                      "
            + "                ";
    private final String invalidLine =
            "EXPDTA    SOLUTION XYZ                                          "
            + "                ";

    @Test
    public void testParseXray() throws PdbParsingException {
        PdbExpdtaLine parsed = parse(validXrayLine);
        List<ExperimentalTechnique> experimentalTechniques =
                parsed.getExperimentalTechniques();
        assertEquals(1, experimentalTechniques.size());
        assertEquals(ExperimentalTechnique.X_RAY_DIFFRACTION,
                     experimentalTechniques.get(0));

        String parsedToString = parsed.toString();
        assertEquals(validXrayLine, parsedToString);
    }

    @Test
    public void testParseNmr() throws PdbParsingException {
        PdbExpdtaLine parsed = parse(validNmrLine);
        List<ExperimentalTechnique> experimentalTechniques =
                parsed.getExperimentalTechniques();
        assertEquals(1, experimentalTechniques.size());
        assertEquals(ExperimentalTechnique.SOLUTION_NMR,
                     experimentalTechniques.get(0));

        String parsedToString = parsed.toString();
        assertEquals(validNmrLine, parsedToString);
    }

    @Test
    public void testParseThreeMethods() throws PdbParsingException {
        PdbExpdtaLine parsed = parse(validThreeMethods);
        List<ExperimentalTechnique> experimentalTechniques =
                parsed.getExperimentalTechniques();
        assertEquals(3, experimentalTechniques.size());
        assertEquals(ExperimentalTechnique.SOLID_STATE_NMR,
                     experimentalTechniques.get(0));
        assertEquals(ExperimentalTechnique.SOLUTION_SCATTERING,
                     experimentalTechniques.get(1));
        assertEquals(ExperimentalTechnique.ELECTRON_MICROSCOPY,
                     experimentalTechniques.get(2));

        String parsedToString = parsed.toString();
        assertEquals(validThreeMethods, parsedToString);
    }

    @Test
    public void testParseValidWithSpace() throws PdbParsingException {
        PdbExpdtaLine parsed = parse(validWithSpace);
        List<ExperimentalTechnique> experimentalTechniques =
                parsed.getExperimentalTechniques();
        assertEquals(2, experimentalTechniques.size());
        assertEquals(ExperimentalTechnique.SOLUTION_NMR,
                     experimentalTechniques.get(0));
        assertEquals(ExperimentalTechnique.THEORETICAL_MODEL,
                     experimentalTechniques.get(1));
    }

    @Test(expected = PdbParsingException.class)
    public void testInvalidLine() throws PdbParsingException {
        parse(invalidLine);
    }
}
