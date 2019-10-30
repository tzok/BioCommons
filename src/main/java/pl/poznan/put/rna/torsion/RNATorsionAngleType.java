package pl.poznan.put.rna.torsion;

import pl.poznan.put.circular.Angle;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.rna.torsion.range.ChiRange;
import pl.poznan.put.rna.torsion.range.Pseudorotation;
import pl.poznan.put.torsion.AverageTorsionAngleType;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.RangeProvider;
import pl.poznan.put.torsion.range.TorsionRange;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum RNATorsionAngleType implements MasterTorsionAngleType {
  ALPHA(Alpha.getInstance()),
  BETA(Beta.getInstance()),
  GAMMA(Gamma.getInstance()),
  DELTA(Delta.getInstance()),
  EPSILON(Epsilon.getInstance()),
  ZETA(Zeta.getInstance()),
  NU0(Nu0.getInstance()),
  NU1(Nu1.getInstance()),
  NU2(Nu2.getInstance()),
  NU3(Nu3.getInstance()),
  NU4(Nu4.getInstance()),
  ETA(Eta.getInstance()),
  THETA(Theta.getInstance()),
  ETA_PRIM(EtaPrim.getInstance()),
  THETA_PRIM(ThetaPrim.getInstance()),
  CHI(ChiRange.getProvider(), Chi.getPurineInstance(), Chi.getPyrimidineInstance()),
  PSEUDOPHASE_PUCKER(Pseudorotation.getProvider(), PseudophasePuckerType.getInstance());

  private static final MasterTorsionAngleType[] MAIN = {
    RNATorsionAngleType.ALPHA, RNATorsionAngleType.BETA,
    RNATorsionAngleType.GAMMA, RNATorsionAngleType.DELTA,
    RNATorsionAngleType.EPSILON, RNATorsionAngleType.ZETA,
    RNATorsionAngleType.CHI
  };
  private static final AverageTorsionAngleType AVERAGE_TORSION_INSTANCE =
      new AverageTorsionAngleType(MoleculeType.RNA, RNATorsionAngleType.MAIN);
  private final RangeProvider rangeProvider;
  private final List<TorsionAngleType> angleTypes;

  RNATorsionAngleType(final RangeProvider rangeProvider, final TorsionAngleType... angleTypes) {
    this.rangeProvider = rangeProvider;
    this.angleTypes = Arrays.asList(angleTypes);
  }

  RNATorsionAngleType(final TorsionAngleType... angleTypes) {
    this(TorsionRange.getProvider(), angleTypes);
  }

  public static MasterTorsionAngleType[] mainAngles() {
    return RNATorsionAngleType.MAIN.clone();
  }

  public static AverageTorsionAngleType getAverageOverMainAngles() {
    return RNATorsionAngleType.AVERAGE_TORSION_INSTANCE;
  }

  @Override
  public List<TorsionAngleType> getAngleTypes() {
    return Collections.unmodifiableList(angleTypes);
  }

  @Override
  public String getLongDisplayName() {
    assert !angleTypes.isEmpty();
    return angleTypes.get(0).getLongDisplayName();
  }

  @Override
  public String getShortDisplayName() {
    assert !angleTypes.isEmpty();
    return angleTypes.get(0).getShortDisplayName();
  }

  @Override
  public String getExportName() {
    assert !angleTypes.isEmpty();
    return angleTypes.get(0).getExportName();
  }

  @Override
  public Range getRange(final Angle angle) {
    return rangeProvider.fromAngle(angle);
  }
}
