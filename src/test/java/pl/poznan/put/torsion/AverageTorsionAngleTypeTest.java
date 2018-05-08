package pl.poznan.put.torsion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

public class AverageTorsionAngleTypeTest {
  private final AverageTorsionAngleType averageTorsionAngleType =
      RNATorsionAngleType.getAverageOverMainAngles();

  @Test
  public final void getDisplayName() {
    assertEquals("MCQ(α, β, γ, δ, ε, ζ, χ)", averageTorsionAngleType.getLongDisplayName());
    assertEquals("MCQ(α, β, γ, δ, ε, ζ, χ)", averageTorsionAngleType.getShortDisplayName());
  }

  @Test
  public final void getExportName() {
    assertEquals(
        "MCQ_alpha_beta_gamma_delta_epsilon_zeta_chi", averageTorsionAngleType.getExportName());
  }

  @Test
  public final void getConsideredAngles() {
    final List<MasterTorsionAngleType> consideredAngles =
        averageTorsionAngleType.getConsideredAngles();

    assertEquals(7, consideredAngles.size());
    assertTrue(consideredAngles.contains(RNATorsionAngleType.ALPHA));
    assertTrue(consideredAngles.contains(RNATorsionAngleType.BETA));
    assertTrue(consideredAngles.contains(RNATorsionAngleType.GAMMA));
    assertTrue(consideredAngles.contains(RNATorsionAngleType.DELTA));
    assertTrue(consideredAngles.contains(RNATorsionAngleType.EPSILON));
    assertTrue(consideredAngles.contains(RNATorsionAngleType.ZETA));
    assertTrue(consideredAngles.contains(RNATorsionAngleType.CHI));
  }

  @Test
  public final void calculateFromPdb() throws Exception {
    final PdbParser parser = new PdbParser();
    final String content = ResourcesHelper.loadResource("1EHZ.pdb");
    final List<PdbModel> models = parser.parse(content);
    assertEquals(1, models.size());

    final PdbModel model = models.get(0);
    final List<PdbResidue> residues = model.getResidues();
    assertEquals(76, residues.size());

    final TorsionAngleValue alpha = Alpha.getInstance().calculate(residues, 1);
    final TorsionAngleValue beta = Beta.getInstance().calculate(residues, 1);
    final TorsionAngleValue gamma = Gamma.getInstance().calculate(residues, 1);
    final TorsionAngleValue delta = Delta.getInstance().calculate(residues, 1);
    final TorsionAngleValue epsilon = Epsilon.getInstance().calculate(residues, 1);
    final TorsionAngleValue zeta = Zeta.getInstance().calculate(residues, 1);

    assertEquals(new Angle(-67.45, ValueType.DEGREES), alpha.getValue());
    assertEquals(new Angle(-178.39, ValueType.DEGREES), beta.getValue());
    assertEquals(new Angle(53.83, ValueType.DEGREES), gamma.getValue());
    assertEquals(new Angle(83.38, ValueType.DEGREES), delta.getValue());
    assertEquals(new Angle(-145.15, ValueType.DEGREES), epsilon.getValue());
    assertEquals(new Angle(-76.79, ValueType.DEGREES), zeta.getValue());

    final PdbResidue residue = residues.get(1);
    final ResidueInformationProvider provider = residue.getResidueInformationProvider();
    assertEquals(NucleobaseType.CYTOSINE, provider);

    final TorsionAngleValue chi = Chi.getPyrimidineInstance().calculate(residues, 1);
    assertEquals(new Angle(-163.82, ValueType.DEGREES), chi.getValue());

    final TorsionAngleValue result = averageTorsionAngleType.calculate(residues, 1);
    final Angle expected = new Angle(-146.33115, ValueType.DEGREES);
    assertEquals(expected, result.getValue());
  }

  @Test
  public final void calculateFromValues() {
    final List<TorsionAngleValue> values =
        Arrays.asList(
            new TorsionAngleValue(Alpha.getInstance(), new Angle(60, ValueType.DEGREES)),
            new TorsionAngleValue(Alpha.getInstance(), new Angle(25, ValueType.DEGREES)),
            new TorsionAngleValue(Alpha.getInstance(), new Angle(-80, ValueType.DEGREES)),
            new TorsionAngleValue(Alpha.getInstance(), new Angle(-150, ValueType.DEGREES)));
    final TorsionAngleValue result = averageTorsionAngleType.calculate(values);

    final Angle expected = new Angle(-15.363804, ValueType.DEGREES);
    assertEquals(expected, result.getValue());
  }

  @Test
  public final void getAngleTypes() {
    final Collection<? extends TorsionAngleType> angleTypes =
        averageTorsionAngleType.getAngleTypes();
    assertEquals(1, angleTypes.size());
    assertTrue(angleTypes.contains(averageTorsionAngleType));
  }
}
