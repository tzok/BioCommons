package pl.poznan.put;

import org.junit.Assert;
import org.junit.Test;
import pl.poznan.put.pdb.ExperimentalTechnique;
import pl.poznan.put.pdb.analysis.CifParser;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.StructureParser;
import pl.poznan.put.utility.ResourcesHelper;

import java.util.List;

import static org.hamcrest.Matchers.is;

public class CifParserTest {
  @Test
  public final void test100D() throws Exception {
    final String cif100D = ResourcesHelper.loadResource("100D.cif");
    final StructureParser parser = new CifParser();
    final List<PdbModel> models = parser.parse(cif100D);
    Assert.assertEquals(1, models.size());

    final PdbModel model = models.get(0);
    final List<PdbChain> chains = model.getChains();
    Assert.assertEquals(2, chains.size());

    final List<ExperimentalTechnique> experimentalTechniques =
        model.getExperimentalDataLine().getExperimentalTechniques();
    Assert.assertEquals(1, experimentalTechniques.size());
    Assert.assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.X_RAY_DIFFRACTION));
    Assert.assertEquals(1.9, model.getResolutionLine().getResolution(), 0.001);
  }

  @Test
  public final void test148L() throws Exception {
    final String cif148L = ResourcesHelper.loadResource("148L.cif");
    final StructureParser parser = new CifParser();
    final List<PdbModel> models = parser.parse(cif148L);
    Assert.assertEquals(1, models.size());
    final PdbModel model = models.get(0);

    PdbResidue residue = model.findResidue("E", 164, " ");
    Assert.assertThat(residue.isMissing(), is(true));

    residue = model.findResidue("S", 169, " ");
    Assert.assertThat(residue.getOriginalResidueName(), is("API"));
    Assert.assertThat(residue.getModifiedResidueName(), is("LYS"));

    final List<ExperimentalTechnique> experimentalTechniques =
        model.getExperimentalDataLine().getExperimentalTechniques();
    Assert.assertEquals(1, experimentalTechniques.size());
    Assert.assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.X_RAY_DIFFRACTION));
    Assert.assertEquals(1.9, model.getResolutionLine().getResolution(), 0.001);
  }

  @Test
  public final void test5A93() throws Exception {
    final String cif5A93 = ResourcesHelper.loadResource("5A93.cif");
    final StructureParser parser = new CifParser();
    final List<PdbModel> models = parser.parse(cif5A93);
    Assert.assertEquals(1, models.size());

    final PdbModel model = models.get(0);
    final List<PdbChain> chains = model.getChains();
    Assert.assertEquals(1, chains.size());

    final List<ExperimentalTechnique> experimentalTechniques =
        model.getExperimentalDataLine().getExperimentalTechniques();
    Assert.assertEquals(2, experimentalTechniques.size());
    Assert.assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.X_RAY_DIFFRACTION));
    Assert.assertThat(experimentalTechniques.get(1), is(ExperimentalTechnique.NEUTRON_DIFFRACTION));
    Assert.assertEquals(2.2, model.getResolutionLine().getResolution(), 0.001);
  }
}
