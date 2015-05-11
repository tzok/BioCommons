package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Zeta extends AtomBasedTorsionAngleType {
    private static final Zeta INSTANCE = new Zeta();

    public static Zeta getInstance() {
        return Zeta.INSTANCE;
    }

    private Zeta() {
        super(MoleculeType.RNA, Unicode.ZETA, new Quadruplet<AtomName>(AtomName.C3p, AtomName.O3p, AtomName.P, AtomName.O5p), new Quadruplet<Integer>(0, 0, 1, 1));
    }
}
