package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Nu0 extends AtomBasedTorsionAngleType {
    private static final Nu0 INSTANCE = new Nu0();

    public static Nu0 getInstance() {
        return Nu0.INSTANCE;
    }

    private Nu0() {
        super(MoleculeType.RNA, Unicode.NU0, new Quadruplet<AtomName>(AtomName.C4p, AtomName.O4p, AtomName.C1p, AtomName.C2p), new Quadruplet<Integer>(0, 0, 0, 0));
    }
}
