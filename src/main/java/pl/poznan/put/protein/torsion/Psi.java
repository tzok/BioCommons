package pl.poznan.put.protein.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Psi extends AtomBasedTorsionAngleType {
    private static final Psi INSTANCE = new Psi();

    private Psi() {
        super(MoleculeType.PROTEIN, Unicode.PSI,
              new Quadruplet<>(AtomName.N, AtomName.CA, AtomName.C, AtomName.N),
              new Quadruplet<>(0, 0, 0, 1));
    }

    public static Psi getInstance() {
        return Psi.INSTANCE;
    }
}
