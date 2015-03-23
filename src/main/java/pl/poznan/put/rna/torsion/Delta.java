package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Delta extends AtomBasedTorsionAngleType {
    private static final Delta INSTANCE = new Delta();

    public static Delta getInstance() {
        return Delta.INSTANCE;
    }

    private Delta() {
        super(MoleculeType.RNA, Unicode.DELTA, new Quadruplet<AtomName>(AtomName.C5p, AtomName.C4p, AtomName.C3p, AtomName.O3p), new Quadruplet<Integer>(0, 0, 0, 0));
    }
}
