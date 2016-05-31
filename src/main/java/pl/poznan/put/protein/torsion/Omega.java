package pl.poznan.put.protein.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Omega extends AtomBasedTorsionAngleType {
    private static final Omega INSTANCE = new Omega();

    public static Omega getInstance() {
        return Omega.INSTANCE;
    }

    private Omega() {
        super(MoleculeType.PROTEIN, Unicode.OMEGA, new Quadruplet<>(AtomName.CA, AtomName.C, AtomName.N, AtomName.CA), new Quadruplet<>(0, 0, 1, 1));
    }
}
