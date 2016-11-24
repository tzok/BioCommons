package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public final class Gamma extends AtomBasedTorsionAngleType {
    private static final Gamma INSTANCE = new Gamma();

    private Gamma() {
        super(MoleculeType.RNA, Unicode.GAMMA,
              new Quadruplet<>(AtomName.O5p, AtomName.C5p, AtomName.C4p,
                               AtomName.C3p), new Quadruplet<>(0, 0, 0, 0));
    }

    public static Gamma getInstance() {
        return Gamma.INSTANCE;
    }
}
