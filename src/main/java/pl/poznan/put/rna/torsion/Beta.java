package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Beta extends AtomBasedTorsionAngleType {
    private static final Beta INSTANCE = new Beta();

    public static Beta getInstance() {
        return Beta.INSTANCE;
    }

    private Beta() {
        super(MoleculeType.RNA, Unicode.BETA, new Quadruplet<AtomName>(AtomName.P, AtomName.O5p, AtomName.C5p, AtomName.C4p), new Quadruplet<Integer>(0, 0, 0, 0));
    }
}
