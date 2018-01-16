package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public final class Nu4 extends AtomBasedTorsionAngleType {
  private static final Nu4 INSTANCE = new Nu4();

  private Nu4() {
    super(
        MoleculeType.RNA,
        Unicode.NU4,
        new Quadruplet<>(AtomName.C3p, AtomName.C4p, AtomName.O4p, AtomName.C1p),
        new Quadruplet<>(0, 0, 0, 0));
  }

  public static Nu4 getInstance() {
    return Nu4.INSTANCE;
  }
}
