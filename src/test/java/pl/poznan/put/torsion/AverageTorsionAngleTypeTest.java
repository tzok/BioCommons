package pl.poznan.put.torsion;

import org.junit.Test;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.base.NucleobaseType;
import pl.poznan.put.rna.torsion.Alpha;
import pl.poznan.put.rna.torsion.Beta;
import pl.poznan.put.rna.torsion.Chi;
import pl.poznan.put.rna.torsion.Delta;
import pl.poznan.put.rna.torsion.Epsilon;
import pl.poznan.put.rna.torsion.Gamma;
import pl.poznan.put.rna.torsion.RNATorsionAngleType;
import pl.poznan.put.rna.torsion.Zeta;
import pl.poznan.put.utility.ResourcesHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AverageTorsionAngleTypeTest {
  private final AverageTorsionAngleType averageTorsionAngleType =
      RNATorsionAngleType.getAverageOverMainAngles();

  @Test
  public final void getDisplayName() {
    assertThat(averageTorsionAngleType.getLongDisplayName(), is("MCQ(α, β, γ, δ, ε, ζ, χ)"));
    assertThat(averageTorsionAngleType.getShortDisplayName(), is("MCQ(α, β, γ, δ, ε, ζ, χ)"));
  }

  @Test
  public final void getExportName() {
    assertThat(
        averageTorsionAngleType.getExportName(), is("MCQ_alpha_beta_gamma_delta_epsilon_zeta_chi"));
  }

  @Test
  public final void getConsideredAngles() {
    final List<MasterTorsionAngleType> consideredAngles =
        averageTorsionAngleType.getConsideredAngles();

    assertThat(consideredAngles.size(), is(7));
    assertThat(consideredAngles, hasItem(RNATorsionAngleType.ALPHA));
    assertThat(consideredAngles, hasItem(RNATorsionAngleType.BETA));
    assertThat(consideredAngles, hasItem(RNATorsionAngleType.GAMMA));
    assertThat(consideredAngles, hasItem(RNATorsionAngleType.DELTA));
    assertThat(consideredAngles, hasItem(RNATorsionAngleType.EPSILON));
    assertThat(consideredAngles, hasItem(RNATorsionAngleType.ZETA));
    assertThat(consideredAngles, hasItem(RNATorsionAngleType.CHI));
  }

  @Test
  public final void calculateFromPdb() throws Exception {
    final PdbParser parser = new PdbParser();
    final String content = ResourcesHelper.loadResource("1EHZ.pdb");
    final List<PdbModel> models = parser.parse(content);
    assertThat(models.size(), is(1));

    final PdbModel model = models.get(0);
    final List<PdbResidue> residues = model.getResidues();
    assertThat(residues.size(), is(76));

    final TorsionAngleValue alpha = Alpha.getInstance().calculate(residues, 1);
    final TorsionAngleValue beta = Beta.getInstance().calculate(residues, 1);
    final TorsionAngleValue gamma = Gamma.getInstance().calculate(residues, 1);
    final TorsionAngleValue delta = Delta.getInstance().calculate(residues, 1);
    final TorsionAngleValue epsilon = Epsilon.getInstance().calculate(residues, 1);
    final TorsionAngleValue zeta = Zeta.getInstance().calculate(residues, 1);

    assertThat(alpha.getValue(), is(new Angle(-67.45, ValueType.DEGREES)));
    assertThat(beta.getValue(), is(new Angle(-178.39, ValueType.DEGREES)));
    assertThat(gamma.getValue(), is(new Angle(53.83, ValueType.DEGREES)));
    assertThat(delta.getValue(), is(new Angle(83.38, ValueType.DEGREES)));
    assertThat(epsilon.getValue(), is(new Angle(-145.15, ValueType.DEGREES)));
    assertThat(zeta.getValue(), is(new Angle(-76.79, ValueType.DEGREES)));

    final PdbResidue residue = residues.get(1);
    final ResidueInformationProvider provider = residue.getResidueInformationProvider();
    assertThat(provider, is(NucleobaseType.CYTOSINE));

    final TorsionAngleValue chi = Chi.getPyrimidineInstance().calculate(residues, 1);
    assertThat(chi.getValue(), is(new Angle(-163.82, ValueType.DEGREES)));

    final TorsionAngleValue result = averageTorsionAngleType.calculate(residues, 1);
    final Angle expected = new Angle(-146.33115, ValueType.DEGREES);
    assertThat(result.getValue(), is(expected));
  }

  @Test
  public final void calculateFromValues() {
    final List<TorsionAngleValue> values =
        Arrays.asList(
            new TorsionAngleValue(Alpha.getInstance(), new Angle(60.0, ValueType.DEGREES)),
            new TorsionAngleValue(Alpha.getInstance(), new Angle(25.0, ValueType.DEGREES)),
            new TorsionAngleValue(Alpha.getInstance(), new Angle(-80.0, ValueType.DEGREES)),
            new TorsionAngleValue(Alpha.getInstance(), new Angle(-150.0, ValueType.DEGREES)));
    final TorsionAngleValue result = averageTorsionAngleType.calculate(values);

    assertThat(result.getValue(), is(new Angle(-15.363804, ValueType.DEGREES)));
  }

  @Test
  public final void getAngleTypes() {
    final Collection<? extends TorsionAngleType> angleTypes =
        averageTorsionAngleType.getAngleTypes();
    assertThat(angleTypes.size(), is(1));
    assertThat(angleTypes.contains(averageTorsionAngleType), is(true));
  }
}
