package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Nu3 extends AtomBasedTorsionAngleType {
    private static final Nu3 INSTANCE = new Nu3();

    public static Nu3 getInstance() {
        return Nu3.INSTANCE;
    }

    private Nu3() {
        super(MoleculeType.RNA, Unicode.NU3, new Quadruplet<AtomName>(AtomName.C2p, AtomName.C3p, AtomName.C4p, AtomName.O4p), new Quadruplet<Integer>(0, 0, 0, 0));
    }
}
