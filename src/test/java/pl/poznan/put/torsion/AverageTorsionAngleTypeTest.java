package pl.poznan.put.torsion;

import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.pdb.analysis.PdbParser;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.rna.base.NucleobaseType;
import pl.poznan.put.rna.torsion.Chi;
import pl.poznan.put.rna.torsion.RNATorsionAngleType;
import pl.poznan.put.utility.ResourcesHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AverageTorsionAngleTypeTest {
  private final AverageTorsionAngleType averageTorsionAngleType =
      RNATorsionAngleType.getAverageOverMainAngles();

  @Test
  public final void getDisplayName() {
    assertThat(averageTorsionAngleType.longDisplayName(), is("MCQ(α, β, γ, δ, ε, ζ, χ)"));
    assertThat(averageTorsionAngleType.shortDisplayName(), is("MCQ(α, β, γ, δ, ε, ζ, χ)"));
  }

  @Test
  public final void getExportName() {
    assertThat(
        averageTorsionAngleType.exportName(), is("MCQ_alpha_beta_gamma_delta_epsilon_zeta_chi"));
  }

  @Test
  public final void getConsideredAngles() {
    final List<MasterTorsionAngleType> consideredAngles =
        averageTorsionAngleType.consideredAngles();

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
    final List<PdbResidue> residues = model.residues();
    assertThat(residues.size(), is(76));

    final TorsionAngleValue alpha =
        RNATorsionAngleType.ALPHA.angleTypes().get(0).calculate(residues, 1);
    final TorsionAngleValue beta =
        RNATorsionAngleType.BETA.angleTypes().get(0).calculate(residues, 1);
    final TorsionAngleValue gamma =
        RNATorsionAngleType.GAMMA.angleTypes().get(0).calculate(residues, 1);
    final TorsionAngleValue delta =
        RNATorsionAngleType.DELTA.angleTypes().get(0).calculate(residues, 1);
    final TorsionAngleValue epsilon =
        RNATorsionAngleType.EPSILON.angleTypes().get(0).calculate(residues, 1);
    final TorsionAngleValue zeta =
        RNATorsionAngleType.ZETA.angleTypes().get(0).calculate(residues, 1);

    assertThat(alpha.value(), is(ImmutableAngle.of(FastMath.toRadians(-67.45))));
    assertThat(beta.value(), is(ImmutableAngle.of(FastMath.toRadians(-178.39))));
    assertThat(gamma.value(), is(ImmutableAngle.of(FastMath.toRadians(53.83))));
    assertThat(delta.value(), is(ImmutableAngle.of(FastMath.toRadians(83.38))));
    assertThat(epsilon.value(), is(ImmutableAngle.of(FastMath.toRadians(-145.15))));
    assertThat(zeta.value(), is(ImmutableAngle.of(FastMath.toRadians(-76.79))));

    final PdbResidue residue = residues.get(1);
    final ResidueInformationProvider provider = residue.residueInformationProvider();
    assertThat(provider, is(NucleobaseType.CYTOSINE));

    final TorsionAngleType chiType =
        provider.torsionAngleTypes().stream()
            .filter(
                angleType ->
                    Objects.equals(angleType, Chi.PURINE_CHI)
                        || Objects.equals(angleType, Chi.PYRIMIDINE_CHI))
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalArgumentException("Failed to find chi angle type for: " + residue));

    assertThat(chiType, is(Chi.PYRIMIDINE_CHI));
    final TorsionAngleValue chi = chiType.calculate(residues, 1);
    assertThat(chi.value(), is(ImmutableAngle.of(FastMath.toRadians(-163.82))));

    final TorsionAngleValue result = averageTorsionAngleType.calculate(residues, 1);
    final Angle expected = ImmutableAngle.of(FastMath.toRadians(-146.33115));
    assertThat(result.value(), is(expected));
  }

  @Test
  public final void calculateFromValues() {
    final List<TorsionAngleValue> values =
        Arrays.asList(
            ImmutableTorsionAngleValue.of(
                RNATorsionAngleType.ALPHA.angleTypes().get(0),
                ImmutableAngle.of(FastMath.toRadians(60.0))),
            ImmutableTorsionAngleValue.of(
                RNATorsionAngleType.ALPHA.angleTypes().get(0),
                ImmutableAngle.of(FastMath.toRadians(25.0))),
            ImmutableTorsionAngleValue.of(
                RNATorsionAngleType.ALPHA.angleTypes().get(0),
                ImmutableAngle.of(FastMath.toRadians(-80.0))),
            ImmutableTorsionAngleValue.of(
                RNATorsionAngleType.ALPHA.angleTypes().get(0),
                ImmutableAngle.of(FastMath.toRadians(-150.0))));
    final TorsionAngleValue result = averageTorsionAngleType.calculate(values);

    assertThat(result.value(), is(ImmutableAngle.of(FastMath.toRadians(-15.363804))));
  }

  @Test
  public final void getAngleTypes() {
    final Collection<? extends TorsionAngleType> angleTypes = averageTorsionAngleType.angleTypes();
    assertThat(angleTypes.size(), is(1));
    assertThat(angleTypes.contains(averageTorsionAngleType), is(true));
  }
}
