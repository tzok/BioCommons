package pl.poznan.put.protein;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.ImmutableAtomBasedTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;
import pl.poznan.put.types.Quadruplet;

import java.util.Collections;
import java.util.List;

final class Chi5 {
  public static final Quadruplet<AtomName> ARGININE_ATOMS =
      ImmutableQuadruplet.of(AtomName.CD, AtomName.NE, AtomName.CZ, AtomName.NH1);

  private static final TorsionAngleType ANGLE_TYPE =
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.PROTEIN,
          Unicode.CHI5,
          "chi5",
          Chi5.ARGININE_ATOMS,
          ImmutableQuadruplet.of(0, 0, 0, 0));

  private Chi5() {
    super();
  }

  public static List<TorsionAngleType> angleTypes() {
    return Collections.singletonList(Chi5.ANGLE_TYPE);
  }

  public static TorsionAngleType getInstance(final Quadruplet<AtomName> unused) {
    return Chi5.ANGLE_TYPE;
  }
}
