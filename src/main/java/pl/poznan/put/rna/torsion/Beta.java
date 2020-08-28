package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;

public final class Beta extends AtomBasedTorsionAngleType {
  private static final Beta INSTANCE = new Beta();

  private Beta() {
    super(
        MoleculeType.RNA,
        Unicode.BETA,
        ImmutableQuadruplet.of(AtomName.P, AtomName.O5p, AtomName.C5p, AtomName.C4p),
        ImmutableQuadruplet.of(0, 0, 0, 0));
  }

  public static Beta getInstance() {
    return Beta.INSTANCE;
  }
}
