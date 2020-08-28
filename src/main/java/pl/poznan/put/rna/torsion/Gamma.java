package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;

public final class Gamma extends AtomBasedTorsionAngleType {
  private static final Gamma INSTANCE = new Gamma();

  private Gamma() {
    super(
        MoleculeType.RNA,
        Unicode.GAMMA,
        ImmutableQuadruplet.of(AtomName.O5p, AtomName.C5p, AtomName.C4p, AtomName.C3p),
        ImmutableQuadruplet.of(0, 0, 0, 0));
  }

  public static Gamma getInstance() {
    return Gamma.INSTANCE;
  }
}
