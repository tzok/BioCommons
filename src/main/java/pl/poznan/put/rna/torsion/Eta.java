package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.torsion.type.PseudoTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Eta extends PseudoTorsionAngleType {
    private static final Eta INSTANCE = new Eta();

    public static Eta getInstance() {
        return Eta.INSTANCE;
    }

    private Eta() {
        super(MoleculeType.RNA, Unicode.ETA, new Quadruplet<AtomName>(AtomName.C4p, AtomName.P, AtomName.C4p, AtomName.P), new Quadruplet<Integer>(0, 1, 1, 2));
    }
}
