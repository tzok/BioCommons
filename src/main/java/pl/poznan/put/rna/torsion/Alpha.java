package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;

public final class Alpha extends AtomBasedTorsionAngleType {
  private static final Alpha INSTANCE = new Alpha();

  private Alpha() {
    super(
        MoleculeType.RNA,
        Unicode.ALPHA,
        ImmutableQuadruplet.of(AtomName.O3p, AtomName.P, AtomName.O5p, AtomName.C5p),
        ImmutableQuadruplet.of(-1, 0, 0, 0));
  }

  public static Alpha getInstance() {
    return Alpha.INSTANCE;
  }
}
