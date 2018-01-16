package pl.poznan.put.protein.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public final class Phi extends AtomBasedTorsionAngleType {
  private static final Phi INSTANCE = new Phi();

  private Phi() {
    super(
        MoleculeType.PROTEIN,
        Unicode.PHI,
        new Quadruplet<>(AtomName.C, AtomName.N, AtomName.CA, AtomName.C),
        new Quadruplet<>(-1, 0, 0, 0));
  }

  public static Phi getInstance() {
    return Phi.INSTANCE;
  }
}
