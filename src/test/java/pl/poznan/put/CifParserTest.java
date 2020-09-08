package pl.poznan.put;

import org.junit.Test;
import pl.poznan.put.pdb.ExperimentalTechnique;
import pl.poznan.put.pdb.analysis.CifModel;
import pl.poznan.put.pdb.analysis.CifParser;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.StructureModel;
import pl.poznan.put.utility.ResourcesHelper;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CifParserTest {
  @Test
  public final void test100D() throws Exception {
    final String cif100D = ResourcesHelper.loadResource("100D.cif");
    final CifParser parser = new CifParser();
    final List<CifModel> models = parser.parse(cif100D);
    assertThat(models.size(), is(1));

    final StructureModel model = models.get(0);
    final List<PdbChain> chains = model.chains();
    assertThat(chains.size(), is(2));

    final List<ExperimentalTechnique> experimentalTechniques =
        model.experimentalData().experimentalTechniques();
    assertThat(experimentalTechniques.size(), is(1));
    assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.X_RAY_DIFFRACTION));
    assertThat(model.resolution().resolution(), is(1.9));
  }

  @Test
  public final void test148L() throws Exception {
    final String cif148L = ResourcesHelper.loadResource("148L.cif");
    final CifParser parser = new CifParser();
    final List<CifModel> models = parser.parse(cif148L);
    assertThat(models.size(), is(1));
    final StructureModel model = models.get(0);

    final PdbResidue residueE164 = model.findResidue("E", 164, " ");
    assertThat(residueE164.isMissing(), is(true));

    final PdbResidue residueS169 = model.findResidue("S", 169, " ");
    assertThat(residueS169.residueName(), is("API"));
    assertThat(residueS169.modifiedResidueName(), is("LYS"));

    final List<ExperimentalTechnique> experimentalTechniques =
        model.experimentalData().experimentalTechniques();
    assertThat(experimentalTechniques.size(), is(1));
    assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.X_RAY_DIFFRACTION));
    assertThat(model.resolution().resolution(), is(1.9));
  }

  @Test
  public final void test5A93() throws Exception {
    final String cif5A93 = ResourcesHelper.loadResource("5A93.cif");
    final CifParser parser = new CifParser();
    final List<CifModel> models = parser.parse(cif5A93);
    assertThat(models.size(), is(1));

    final StructureModel model = models.get(0);
    final List<PdbChain> chains = model.chains();
    assertThat(chains.size(), is(1));

    final List<ExperimentalTechnique> experimentalTechniques =
        model.experimentalData().experimentalTechniques();
    assertThat(experimentalTechniques.size(), is(2));
    assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.X_RAY_DIFFRACTION));
    assertThat(experimentalTechniques.get(1), is(ExperimentalTechnique.NEUTRON_DIFFRACTION));
    assertThat(model.resolution().resolution(), is(2.2));
  }
}
