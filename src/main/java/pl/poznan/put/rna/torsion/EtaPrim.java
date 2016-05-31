package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.PseudoTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class EtaPrim extends PseudoTorsionAngleType {
    private static final EtaPrim INSTANCE = new EtaPrim();

    public static EtaPrim getInstance() {
        return EtaPrim.INSTANCE;
    }

    private EtaPrim() {
        super(MoleculeType.RNA, Unicode.ETA_PRIM, new Quadruplet<>(AtomName.C1p, AtomName.P, AtomName.C1p, AtomName.P), new Quadruplet<>(-1, 0, 0, 1));
    }
}
