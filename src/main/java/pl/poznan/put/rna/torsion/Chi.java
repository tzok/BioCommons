package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.ImmutableAtomBasedTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;
import pl.poznan.put.types.Quadruplet;

public final class Chi {
  public static final Quadruplet<AtomName> PURINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.O4p, AtomName.C1p, AtomName.N9, AtomName.C4);
  public static final Quadruplet<AtomName> PYRIMIDINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.O4p, AtomName.C1p, AtomName.N1, AtomName.C2);

  public static final TorsionAngleType PURINE_CHI =
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.CHI,
          "chi",
          Chi.PURINE_ATOMS,
          ImmutableQuadruplet.of(0, 0, 0, 0));
  public static final TorsionAngleType PYRIMIDINE_CHI =
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.CHI,
          "chi",
          Chi.PYRIMIDINE_ATOMS,
          ImmutableQuadruplet.of(0, 0, 0, 0));
}
