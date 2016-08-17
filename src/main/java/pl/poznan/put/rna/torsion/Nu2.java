package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Nu2 extends AtomBasedTorsionAngleType {
    private static final Nu2 INSTANCE = new Nu2();

    private Nu2() {
        super(MoleculeType.RNA, Unicode.NU2,
              new Quadruplet<>(AtomName.C1p, AtomName.C2p, AtomName.C3p,
                               AtomName.C4p), new Quadruplet<>(0, 0, 0, 0));
    }

    public static Nu2 getInstance() {
        return Nu2.INSTANCE;
    }
}
