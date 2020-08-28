package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;

public final class Epsilon extends AtomBasedTorsionAngleType {
  private static final Epsilon INSTANCE = new Epsilon();

  private Epsilon() {
    super(
        MoleculeType.RNA,
        Unicode.EPSILON,
        ImmutableQuadruplet.of(AtomName.C4p, AtomName.C3p, AtomName.O3p, AtomName.P),
        ImmutableQuadruplet.of(0, 0, 0, 1));
  }

  public static Epsilon getInstance() {
    return Epsilon.INSTANCE;
  }
}
