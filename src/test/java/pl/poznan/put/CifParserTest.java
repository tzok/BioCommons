package pl.poznan.put;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import pl.poznan.put.pdb.ExperimentalTechnique;
import pl.poznan.put.pdb.analysis.CifModel;
import pl.poznan.put.pdb.analysis.CifParser;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.utility.ResourcesHelper;

public class CifParserTest {
  @Test
  public final void test100D() throws Exception {
    final String cif100D = ResourcesHelper.loadResource("100D.cif");
    final CifParser parser = new CifParser();
    final List<CifModel> models = parser.parse(cif100D);
    Assert.assertEquals(1, models.size());

    final CifModel model = models.get(0);
    final List<PdbChain> chains = model.getChains();
    Assert.assertEquals(2, chains.size());

    final List<ExperimentalTechnique> experimentalTechniques =
        model.getExperimentalDataLine().getExperimentalTechniques();
    Assert.assertEquals(1, experimentalTechniques.size());
    Assert.assertEquals(ExperimentalTechnique.X_RAY_DIFFRACTION, experimentalTechniques.get(0));
    Assert.assertEquals(1.9, model.getResolutionLine().getResolution(), 0.001);
  }

  @Test
  public final void test148L() throws Exception {
    final String cif148L = ResourcesHelper.loadResource("148L.cif");
    final CifParser parser = new CifParser();
    final List<CifModel> models = parser.parse(cif148L);
    Assert.assertEquals(1, models.size());
    final CifModel model = models.get(0);

    PdbResidue residue = model.findResidue("E", 164, " ");
    Assert.assertTrue(residue.isMissing());

    residue = model.findResidue("S", 169, " ");
    Assert.assertEquals("API", residue.getOriginalResidueName());
    Assert.assertEquals("LYS", residue.getModifiedResidueName());

    final List<ExperimentalTechnique> experimentalTechniques =
        model.getExperimentalDataLine().getExperimentalTechniques();
    Assert.assertEquals(1, experimentalTechniques.size());
    Assert.assertEquals(ExperimentalTechnique.X_RAY_DIFFRACTION, experimentalTechniques.get(0));
    Assert.assertEquals(1.9, model.getResolutionLine().getResolution(), 0.001);
  }

  @Test
  public final void test5A93() throws Exception {
    final String cif5A93 = ResourcesHelper.loadResource("5A93.cif");
    final CifParser parser = new CifParser();
    final List<CifModel> models = parser.parse(cif5A93);
    Assert.assertEquals(1, models.size());

    final CifModel model = models.get(0);
    final List<PdbChain> chains = model.getChains();
    Assert.assertEquals(1, chains.size());

    final List<ExperimentalTechnique> experimentalTechniques =
        model.getExperimentalDataLine().getExperimentalTechniques();
    Assert.assertEquals(2, experimentalTechniques.size());
    Assert.assertEquals(ExperimentalTechnique.X_RAY_DIFFRACTION, experimentalTechniques.get(0));
    Assert.assertEquals(ExperimentalTechnique.NEUTRON_DIFFRACTION, experimentalTechniques.get(1));
    Assert.assertEquals(2.2, model.getResolutionLine().getResolution(), 0.001);
  }
}
