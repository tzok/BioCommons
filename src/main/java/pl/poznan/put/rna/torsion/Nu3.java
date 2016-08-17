package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Nu3 extends AtomBasedTorsionAngleType {
    private static final Nu3 INSTANCE = new Nu3();

    private Nu3() {
        super(MoleculeType.RNA, Unicode.NU3,
              new Quadruplet<>(AtomName.C2p, AtomName.C3p, AtomName.C4p,
                               AtomName.O4p), new Quadruplet<>(0, 0, 0, 0));
    }

    public static Nu3 getInstance() {
        return Nu3.INSTANCE;
    }
}
