package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.PseudoTorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;
import pl.poznan.put.types.Quadruplet;

public final class ThetaPrim extends PseudoTorsionAngleType {
  private static final ThetaPrim INSTANCE = new ThetaPrim();

  private ThetaPrim() {
    super(
        MoleculeType.RNA,
        Unicode.THETA_PRIM,
        ImmutableQuadruplet.of(AtomName.P, AtomName.C1p, AtomName.P, AtomName.C1p),
        ImmutableQuadruplet.of(0, 0, 1, 1));
  }

  public static ThetaPrim getInstance() {
    return ThetaPrim.INSTANCE;
  }
}
