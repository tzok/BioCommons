package pl.poznan.put.protein;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.ImmutableAtomBasedTorsionAngleType;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.TorsionRange;
import pl.poznan.put.types.ImmutableQuadruple;

/** A torsion angle defined for proteins. */
public enum AminoAcidTorsionAngle implements MasterTorsionAngleType {
  PHI(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.PROTEIN,
          Unicode.PHI,
          "phi",
          ImmutableQuadruple.of(AtomName.C, AtomName.N, AtomName.CA, AtomName.C),
          ImmutableQuadruple.of(-1, 0, 0, 0))),
  PSI(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.PROTEIN,
          Unicode.PSI,
          "psi",
          ImmutableQuadruple.of(AtomName.N, AtomName.CA, AtomName.C, AtomName.N),
          ImmutableQuadruple.of(0, 0, 0, 1))),
  OMEGA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.PROTEIN,
          Unicode.OMEGA,
          "omega",
          ImmutableQuadruple.of(AtomName.CA, AtomName.C, AtomName.N, AtomName.CA),
          ImmutableQuadruple.of(0, 0, 1, 1))),
  CALPHA(
      ImmutableAtomBasedTorsionAngleType.builder()
          .moleculeType(MoleculeType.PROTEIN)
          .shortDisplayName(Unicode.CALPHA)
          .exportName("c-alpha")
          .atoms(ImmutableQuadruple.of(AtomName.CA, AtomName.CA, AtomName.CA, AtomName.CA))
          .residueRule(ImmutableQuadruple.of(0, 1, 2, 3))
          .isPseudoTorsion(true)
          .build()),
  CHI1(Chi1.angleTypes()),
  CHI2(Chi2.angleTypes()),
  CHI3(Chi3.angleTypes()),
  CHI4(Chi4.angleTypes()),
  CHI5(Chi5.angleTypes());

  private final List<TorsionAngleType> angleTypes;

  AminoAcidTorsionAngle(final TorsionAngleType... angleTypes) {
    this.angleTypes = Arrays.asList(angleTypes);
  }

  AminoAcidTorsionAngle(final Collection<TorsionAngleType> angleTypes) {
    this.angleTypes = new ArrayList<>(angleTypes);
  }

  @Override
  public List<TorsionAngleType> angleTypes() {
    return Collections.unmodifiableList(angleTypes);
  }

  @Override
  public Range range(final Angle angle) {
    return TorsionRange.rangeProvider().fromAngle(angle);
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
