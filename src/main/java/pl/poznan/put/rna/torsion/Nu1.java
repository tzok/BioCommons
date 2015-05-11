package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Nu1 extends AtomBasedTorsionAngleType {
    private static final Nu1 INSTANCE = new Nu1();

    public static Nu1 getInstance() {
        return Nu1.INSTANCE;
    }

    private Nu1() {
        super(MoleculeType.RNA, Unicode.NU1, new Quadruplet<AtomName>(AtomName.O4p, AtomName.C1p, AtomName.C2p, AtomName.C3p), new Quadruplet<Integer>(0, 0, 0, 0));
    }
}
