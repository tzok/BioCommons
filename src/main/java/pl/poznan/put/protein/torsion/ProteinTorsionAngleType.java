package pl.poznan.put.protein.torsion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AverageTorsionAngleType;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.TorsionRange;

public enum ProteinTorsionAngleType implements MasterTorsionAngleType {
  PHI(Phi.getInstance()),
  PSI(Psi.getInstance()),
  OMEGA(Omega.getInstance()),
  CALPHA(Calpha.getInstance()),
  CHI1(Chi1.getInstances()),
  CHI2(Chi2.getInstances()),
  CHI3(Chi3.getInstances()),
  CHI4(Chi4.getInstances()),
  CHI5(Chi5.getInstances());

  private static final MasterTorsionAngleType[] MAIN = {
    ProteinTorsionAngleType.PHI, ProteinTorsionAngleType.PSI, ProteinTorsionAngleType.OMEGA
  };
  private static final AverageTorsionAngleType AVERAGE_TORSION_INSTANCE =
      new AverageTorsionAngleType(MoleculeType.PROTEIN, ProteinTorsionAngleType.MAIN);
  private final List<TorsionAngleType> angleTypes;

  ProteinTorsionAngleType(final TorsionAngleType... angleTypes) {
    this.angleTypes = Arrays.asList(angleTypes);
  }

  public static MasterTorsionAngleType[] mainAngles() {
    return ProteinTorsionAngleType.MAIN;
  }

  public static AverageTorsionAngleType getAverageOverMainAngles() {
    return ProteinTorsionAngleType.AVERAGE_TORSION_INSTANCE;
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
    return TorsionRange.getProvider().fromAngle(angle);
  }
}
