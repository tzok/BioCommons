package pl.poznan.put.protein.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;
import pl.poznan.put.types.Quadruplet;

public final class Omega extends AtomBasedTorsionAngleType {
  private static final Omega INSTANCE = new Omega();

  private Omega() {
    super(
        MoleculeType.PROTEIN,
        Unicode.OMEGA,
        ImmutableQuadruplet.of(AtomName.CA, AtomName.C, AtomName.N, AtomName.CA),
        ImmutableQuadruplet.of(0, 0, 1, 1));
  }

  public static Omega getInstance() {
    return Omega.INSTANCE;
  }
}
