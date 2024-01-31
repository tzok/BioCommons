package pl.poznan.put.pdb.analysis;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import pl.poznan.put.pdb.ExperimentalTechnique;
import pl.poznan.put.pdb.ImmutablePdbResidueIdentifier;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.utility.ResourcesHelper;

public class CifParserTest {
  @Test
  public final void test100D() throws Exception {
    final String cif100D = ResourcesHelper.loadResource("100D.cif");
    final List<CifModel> models = new CifParser().parse(cif100D);
    assertThat(models.size(), is(1));

    final PdbModel model = models.get(0);
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
    final List<CifModel> models = new CifParser().parse(cif148L);
    assertThat(models.size(), is(1));
    final PdbModel model = models.get(0);

    final PdbResidue residueE164 =
        model.findResidue(ImmutablePdbResidueIdentifier.of("E", 164, Optional.empty()));
    assertThat(residueE164.isMissing(), is(true));

    final PdbResidue residueS169 =
        model.findResidue(ImmutablePdbResidueIdentifier.of("S", 169, Optional.empty()));
    assertThat(residueS169.standardResidueName(), is("API"));
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
    final List<CifModel> models = new CifParser().parse(cif5A93);
    assertThat(models.size(), is(1));

    final PdbModel model = models.get(0);
    final List<PdbChain> chains = model.chains();
    assertThat(chains.size(), is(1));

    final List<ExperimentalTechnique> experimentalTechniques =
        model.experimentalData().experimentalTechniques();
    assertThat(experimentalTechniques.size(), is(2));
    assertThat(experimentalTechniques.get(0), is(ExperimentalTechnique.X_RAY_DIFFRACTION));
    assertThat(experimentalTechniques.get(1), is(ExperimentalTechnique.NEUTRON_DIFFRACTION));
    assertThat(model.resolution().resolution(), is(1.598));
  }

  @Test
  public final void test1AM0() throws Exception {
    final String cif1AM0 = ResourcesHelper.loadResource("1am0-assembly-1.cif");
    final List<CifModel> models = new CifParser().parse(cif1AM0);
    assertThat(models.size(), is(8));

    final PdbModel model = models.get(0);
    assertThat(model.missingResidues().size(), is(24));
    assertThat(model.residues().size(), is(41));
  }

  @Test
  public final void test1K9W() throws Exception {
    final String cif1K9W = ResourcesHelper.loadResource("1k9w-assembly-2.cif");
    final List<CifModel> models = new CifParser().parse(cif1K9W);
    assertThat(models.size(), is(1));

    final CifModel model = models.get(0);
    final Set<PdbResidueIdentifier> unique =
        model.residues().stream().map(PdbResidueIdentifier::from).collect(Collectors.toSet());
    assertThat(model.residues().size(), is(unique.size()));
  }

  @Test
  public final void test430D() throws IOException {
    final String cif430D = ResourcesHelper.loadResource("430D.cif");
    final List<CifModel> models = new CifParser().parse(cif430D);
    assertThat(models.size(), is(1));

    final CifModel model = models.get(0);
    final PdbResidue residue =
        model.findResidue(ImmutablePdbResidueIdentifier.of("A", 27, Optional.empty()));
    assertThat(residue.isModified(), is(true));

    final List<CifModel> reparsed = new CifParser().parse(model.toCif());
    assertThat(reparsed.size(), is(1));

    final CifModel reparsedModel = reparsed.get(0);
    final PdbResidue reparsedResidue =
        reparsedModel.findResidue(ImmutablePdbResidueIdentifier.of("A", 27, Optional.empty()));
    assertThat(reparsedResidue.isModified(), is(true));
  }
}
