package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;
import pl.poznan.put.types.Quadruplet;

public final class Nu0 extends AtomBasedTorsionAngleType {
  private static final Nu0 INSTANCE = new Nu0();

  private Nu0() {
    super(
        MoleculeType.RNA,
        Unicode.NU0,
        ImmutableQuadruplet.of(AtomName.C4p, AtomName.O4p, AtomName.C1p, AtomName.C2p),
        ImmutableQuadruplet.of(0, 0, 0, 0));
  }

  public static Nu0 getInstance() {
    return Nu0.INSTANCE;
  }
}
