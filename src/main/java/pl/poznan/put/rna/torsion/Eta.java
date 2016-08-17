package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.PseudoTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Eta extends PseudoTorsionAngleType {
    private static final Eta INSTANCE = new Eta();

    private Eta() {
        super(MoleculeType.RNA, Unicode.ETA,
              new Quadruplet<>(AtomName.C4p, AtomName.P, AtomName.C4p,
                               AtomName.P), new Quadruplet<>(-1, 0, 0, 1));
    }

    public static Eta getInstance() {
        return Eta.INSTANCE;
    }
}
