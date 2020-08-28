package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;

public final class Delta extends AtomBasedTorsionAngleType {
  private static final Delta INSTANCE = new Delta();

  private Delta() {
    super(
        MoleculeType.RNA,
        Unicode.DELTA,
        ImmutableQuadruplet.of(AtomName.C5p, AtomName.C4p, AtomName.C3p, AtomName.O3p),
        ImmutableQuadruplet.of(0, 0, 0, 0));
  }

  public static Delta getInstance() {
    return Delta.INSTANCE;
  }
}
