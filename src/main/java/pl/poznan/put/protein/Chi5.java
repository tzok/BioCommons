package pl.poznan.put.protein;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.ImmutableAtomBasedTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruple;
import pl.poznan.put.types.Quadruple;

import java.util.Collections;
import java.util.List;

final class Chi5 {
  public static final Quadruple<AtomName> ARGININE_ATOMS =
      ImmutableQuadruple.of(AtomName.CD, AtomName.NE, AtomName.CZ, AtomName.NH1);

  private static final TorsionAngleType ANGLE_TYPE =
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.PROTEIN,
          Unicode.CHI5,
          "chi5",
          Chi5.ARGININE_ATOMS,
          ImmutableQuadruple.of(0, 0, 0, 0));

  private Chi5() {
    super();
  }

  public static List<TorsionAngleType> angleTypes() {
    return Collections.singletonList(Chi5.ANGLE_TYPE);
  }

  public static TorsionAngleType getInstance(final Quadruple<AtomName> unused) {
    return Chi5.ANGLE_TYPE;
  }
}
