package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;
import pl.poznan.put.types.Quadruplet;

public final class Zeta extends AtomBasedTorsionAngleType {
  private static final Zeta INSTANCE = new Zeta();

  private Zeta() {
    super(
        MoleculeType.RNA,
        Unicode.ZETA,
        ImmutableQuadruplet.of(AtomName.C3p, AtomName.O3p, AtomName.P, AtomName.O5p),
        ImmutableQuadruplet.of(0, 0, 1, 1));
  }

  public static Zeta getInstance() {
    return Zeta.INSTANCE;
  }
}
