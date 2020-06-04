package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;
import pl.poznan.put.types.Quadruplet;

public final class Chi extends AtomBasedTorsionAngleType {
  public static final Quadruplet<AtomName> PURINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.O4p, AtomName.C1p, AtomName.N9, AtomName.C4);
  public static final Quadruplet<AtomName> PYRIMIDINE_ATOMS =
      ImmutableQuadruplet.of(AtomName.O4p, AtomName.C1p, AtomName.N1, AtomName.C2);

  private static final Chi PURINE_INSTANCE = new Chi(Chi.PURINE_ATOMS);
  private static final Chi PYRIMIDINE_INSTANCE = new Chi(Chi.PYRIMIDINE_ATOMS);

  private Chi(final Quadruplet<AtomName> atoms) {
    super(MoleculeType.RNA, Unicode.CHI, atoms, ImmutableQuadruplet.of(0, 0, 0, 0));
  }

  public static Chi getPurineInstance() {
    return Chi.PURINE_INSTANCE;
  }

  public static Chi getPyrimidineInstance() {
    return Chi.PYRIMIDINE_INSTANCE;
  }
}
