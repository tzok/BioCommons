package pl.poznan.put;

import org.junit.Test;
import pl.poznan.put.pdb.ExperimentalTechnique;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbParsingException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static pl.poznan.put.pdb.PdbExpdtaLine.parse;

public class PdbExpdtaLineTest {
  // @formatter:off
  private static final String VALID_XRAY_LINE =
      "EXPDTA    X-RAY DIFFRACTION                                                     ";
  private static final String VALID_NMR_LINE =
      "EXPDTA    SOLUTION NMR                                                          ";
  private static final String VALID_THREE_METHODS =
      "EXPDTA    SOLID-STATE NMR; SOLUTION SCATTERING; ELECTRON MICROSCOPY             ";
  private static final String VALID_WITH_SPACE =
      "EXPDTA    SOLUTION NMR ; THEORETICAL MODEL                                      ";
  private static final String INVALID_LINE =
      "EXPDTA    SOLUTION XYZ                                                          ";
  // @formatter:on

  @Test
  public final void testParseXray() throws PdbParsingException {
    final PdbExpdtaLine parsed = parse(PdbExpdtaLineTest.VALID_XRAY_LINE);
    final List<ExperimentalTechnique> experimentalTechniques = parsed.getExperimentalTechniques();
    assertEquals(1, experimentalTechniques.size());
    assertEquals(ExperimentalTechnique.X_RAY_DIFFRACTION, experimentalTechniques.get(0));

    final String parsedToString = parsed.toString();
    assertEquals(PdbExpdtaLineTest.VALID_XRAY_LINE, parsedToString);
  }

  @Test
  public final void testParseNmr() throws PdbParsingException {
    final PdbExpdtaLine parsed = parse(PdbExpdtaLineTest.VALID_NMR_LINE);
    final List<ExperimentalTechnique> experimentalTechniques = parsed.getExperimentalTechniques();
    assertEquals(1, experimentalTechniques.size());
    assertEquals(ExperimentalTechnique.SOLUTION_NMR, experimentalTechniques.get(0));

    final String parsedToString = parsed.toString();
    assertEquals(PdbExpdtaLineTest.VALID_NMR_LINE, parsedToString);
  }

  @Test
  public final void testParseThreeMethods() throws PdbParsingException {
    final PdbExpdtaLine parsed = parse(PdbExpdtaLineTest.VALID_THREE_METHODS);
    final List<ExperimentalTechnique> experimentalTechniques = parsed.getExperimentalTechniques();
    assertEquals(3, experimentalTechniques.size());
    assertEquals(ExperimentalTechnique.SOLID_STATE_NMR, experimentalTechniques.get(0));
    assertEquals(ExperimentalTechnique.SOLUTION_SCATTERING, experimentalTechniques.get(1));
    assertEquals(ExperimentalTechnique.ELECTRON_MICROSCOPY, experimentalTechniques.get(2));

    final String parsedToString = parsed.toString();
    assertEquals(PdbExpdtaLineTest.VALID_THREE_METHODS, parsedToString);
  }

  @Test
  public final void testParseValidWithSpace() throws PdbParsingException {
    final PdbExpdtaLine parsed = parse(PdbExpdtaLineTest.VALID_WITH_SPACE);
    final List<ExperimentalTechnique> experimentalTechniques = parsed.getExperimentalTechniques();
    assertEquals(2, experimentalTechniques.size());
    assertEquals(ExperimentalTechnique.SOLUTION_NMR, experimentalTechniques.get(0));
    assertEquals(ExperimentalTechnique.THEORETICAL_MODEL, experimentalTechniques.get(1));
  }

  @Test(expected = PdbParsingException.class)
  public final void testInvalidLine() throws PdbParsingException {
    parse(PdbExpdtaLineTest.INVALID_LINE);
  }
}
