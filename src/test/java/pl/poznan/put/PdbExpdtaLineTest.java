package pl.poznan.put;

import org.junit.Assert;
import org.junit.Test;
import pl.poznan.put.pdb.ExperimentalTechnique;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbParsingException;

import java.util.List;

import static org.hamcrest.Matchers.is;
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
  public final void testParseXray() {
    final PdbExpdtaLine parsed = parse(PdbExpdtaLineTest.VALID_XRAY_LINE);
    final List<ExperimentalTechnique> experimentalTechniques = parsed.getExperimentalTechniques();
    Assert.assertEquals(1, experimentalTechniques.size());
    Assert.assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.X_RAY_DIFFRACTION));

    final String parsedToString = parsed.toString();
    Assert.assertThat(parsedToString, is(PdbExpdtaLineTest.VALID_XRAY_LINE));
  }

  @Test
  public final void testParseNmr() {
    final PdbExpdtaLine parsed = parse(PdbExpdtaLineTest.VALID_NMR_LINE);
    final List<ExperimentalTechnique> experimentalTechniques = parsed.getExperimentalTechniques();
    Assert.assertEquals(1, experimentalTechniques.size());
    Assert.assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.SOLUTION_NMR));

    final String parsedToString = parsed.toString();
    Assert.assertThat(parsedToString, is(PdbExpdtaLineTest.VALID_NMR_LINE));
  }

  @Test
  public final void testParseThreeMethods() {
    final PdbExpdtaLine parsed = parse(PdbExpdtaLineTest.VALID_THREE_METHODS);
    final List<ExperimentalTechnique> experimentalTechniques = parsed.getExperimentalTechniques();
    Assert.assertEquals(3, experimentalTechniques.size());
    Assert.assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.SOLID_STATE_NMR));
    Assert.assertThat(experimentalTechniques.get(1), is(ExperimentalTechnique.SOLUTION_SCATTERING));
    Assert.assertThat(experimentalTechniques.get(2), is(ExperimentalTechnique.ELECTRON_MICROSCOPY));

    final String parsedToString = parsed.toString();
    Assert.assertThat(parsedToString, is(PdbExpdtaLineTest.VALID_THREE_METHODS));
  }

  @Test
  public final void testParseValidWithSpace() {
    final PdbExpdtaLine parsed = parse(PdbExpdtaLineTest.VALID_WITH_SPACE);
    final List<ExperimentalTechnique> experimentalTechniques = parsed.getExperimentalTechniques();
    Assert.assertEquals(2, experimentalTechniques.size());
    Assert.assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.SOLUTION_NMR));
    Assert.assertThat(experimentalTechniques.get(1), is(ExperimentalTechnique.THEORETICAL_MODEL));
  }

  @Test(expected = PdbParsingException.class)
  public final void testInvalidLine() {
    parse(PdbExpdtaLineTest.INVALID_LINE);
  }
}
