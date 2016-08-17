package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.PseudoTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Theta extends PseudoTorsionAngleType {
    private static final Theta INSTANCE = new Theta();

    private Theta() {
        super(MoleculeType.RNA, Unicode.THETA,
              new Quadruplet<>(AtomName.P, AtomName.C4p, AtomName.P,
                               AtomName.C4p), new Quadruplet<>(0, 0, 1, 1));
    }

    public static Theta getInstance() {
        return Theta.INSTANCE;
    }
}
