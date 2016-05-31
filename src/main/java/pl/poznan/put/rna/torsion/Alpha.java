package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Alpha extends AtomBasedTorsionAngleType {
    private static final Alpha INSTANCE = new Alpha();

    public static Alpha getInstance() {
        return Alpha.INSTANCE;
    }

    private Alpha() {
        super(MoleculeType.RNA, Unicode.ALPHA, new Quadruplet<>(AtomName.O3p, AtomName.P, AtomName.O5p, AtomName.C5p), new Quadruplet<>(-1, 0, 0, 0));
    }
}
