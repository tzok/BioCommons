package pl.poznan.put.rna;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.ImmutableAtomBasedTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;

/**
 * A glycosidic bond torsion angle (chi), which is defined differently for purines and pyrimidines.
 */
public enum Chi {
  PURINE(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.CHI,
          "chi",
          ImmutableQuadruplet.of(AtomName.O4p, AtomName.C1p, AtomName.N9, AtomName.C4),
          ImmutableQuadruplet.of(0, 0, 0, 0))),
  PYRIMIDINE(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.CHI,
          "chi",
          ImmutableQuadruplet.of(AtomName.O4p, AtomName.C1p, AtomName.N1, AtomName.C2),
          ImmutableQuadruplet.of(0, 0, 0, 0)));

  private final TorsionAngleType angleType;

  Chi(final TorsionAngleType angleType) {
    this.angleType = angleType;
  }

  /** @return The torsion angle type for this instance of chi. */
  public TorsionAngleType angleType() {
    return angleType;
  }
}
