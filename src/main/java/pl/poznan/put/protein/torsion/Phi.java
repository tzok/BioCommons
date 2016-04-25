package pl.poznan.put.protein.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Phi extends AtomBasedTorsionAngleType {
    private static final Phi INSTANCE = new Phi();

    public static Phi getInstance() {
        return Phi.INSTANCE;
    }

    private Phi() {
        super(MoleculeType.PROTEIN, Unicode.PHI, new Quadruplet<AtomName>(AtomName.C, AtomName.N, AtomName.CA, AtomName.C), new Quadruplet<Integer>(-1, 0, 0, 0));
    }
}
