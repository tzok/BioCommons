package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Gamma extends AtomBasedTorsionAngleType {
    private static final Gamma INSTANCE = new Gamma();

    public static Gamma getInstance() {
        return Gamma.INSTANCE;
    }

    private Gamma() {
        super(MoleculeType.RNA, Unicode.GAMMA, new Quadruplet<AtomName>(AtomName.O5p, AtomName.C5p, AtomName.C4p, AtomName.C3p), new Quadruplet<Integer>(0, 0, 0, 0));
    }
}
