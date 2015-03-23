package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.torsion.type.PseudoTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class ThetaPrim extends PseudoTorsionAngleType {
    private static final ThetaPrim INSTANCE = new ThetaPrim();

    public static ThetaPrim getInstance() {
        return ThetaPrim.INSTANCE;
    }

    private ThetaPrim() {
        super(MoleculeType.RNA, Unicode.THETA_PRIM, new Quadruplet<AtomName>(AtomName.P, AtomName.C1p, AtomName.P, AtomName.C1p), new Quadruplet<Integer>(0, 0, 1, 1));
    }
}
