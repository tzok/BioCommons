package pl.poznan.put.protein.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AverageTorsionAngleType;
import pl.poznan.put.torsion.ImmutableAtomBasedTorsionAngleType;
import pl.poznan.put.torsion.ImmutableAverageTorsionAngleType;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.TorsionRange;
import pl.poznan.put.types.ImmutableQuadruplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum ProteinTorsionAngleType implements MasterTorsionAngleType {
  PHI(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.PROTEIN,
          Unicode.PHI,
          "phi",
          ImmutableQuadruplet.of(AtomName.C, AtomName.N, AtomName.CA, AtomName.C),
          ImmutableQuadruplet.of(-1, 0, 0, 0))),
  PSI(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.PROTEIN,
          Unicode.PSI,
          "psi",
          ImmutableQuadruplet.of(AtomName.N, AtomName.CA, AtomName.C, AtomName.N),
          ImmutableQuadruplet.of(0, 0, 0, 1))),
  OMEGA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.PROTEIN,
          Unicode.OMEGA,
          "omega",
          ImmutableQuadruplet.of(AtomName.CA, AtomName.C, AtomName.N, AtomName.CA),
          ImmutableQuadruplet.of(0, 0, 1, 1))),
  CALPHA(
      ImmutableAtomBasedTorsionAngleType.builder()
          .moleculeType(MoleculeType.PROTEIN)
          .shortDisplayName(Unicode.CALPHA)
          .exportName("c-alpha")
          .atoms(ImmutableQuadruplet.of(AtomName.CA, AtomName.CA, AtomName.CA, AtomName.CA))
          .residueRule(ImmutableQuadruplet.of(0, 1, 2, 3))
          .isPseudoTorsion(true)
          .build()),
  CHI1(Chi1.angleTypes()),
  CHI2(Chi2.angleTypes()),
  CHI3(Chi3.angleTypes()),
  CHI4(Chi4.angleTypes()),
  CHI5(Chi5.angleTypes());

  private static final List<ProteinTorsionAngleType> MAIN =
      Arrays.asList(
          ProteinTorsionAngleType.PHI, ProteinTorsionAngleType.PSI, ProteinTorsionAngleType.OMEGA);
  private static final AverageTorsionAngleType AVERAGE_TORSION_INSTANCE =
      ImmutableAverageTorsionAngleType.of(MoleculeType.PROTEIN, ProteinTorsionAngleType.MAIN);
  private final List<TorsionAngleType> angleTypes;

  ProteinTorsionAngleType(final TorsionAngleType... angleTypes) {
    this.angleTypes = Arrays.asList(angleTypes);
  }

  ProteinTorsionAngleType(final Collection<TorsionAngleType> angleTypes) {
    this.angleTypes = new ArrayList<>(angleTypes);
  }

  public static AverageTorsionAngleType getAverageOverMainAngles() {
    return ProteinTorsionAngleType.AVERAGE_TORSION_INSTANCE;
  }

  @Override
  public List<TorsionAngleType> angleTypes() {
    return Collections.unmodifiableList(angleTypes);
  }

  @Override
  public Range range(final Angle angle) {
    return TorsionRange.getProvider().fromAngle(angle);
  }

  @Override
  public String shortDisplayName() {
    assert !angleTypes.isEmpty();
    return angleTypes.get(0).shortDisplayName();
  }

  @Override
  public String longDisplayName() {
    assert !angleTypes.isEmpty();
    return angleTypes.get(0).longDisplayName();
  }

  @Override
  public String exportName() {
    assert !angleTypes.isEmpty();
    return angleTypes.get(0).exportName();
  }
}
