package pl.poznan.put.protein.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.type.PseudoTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Calpha extends PseudoTorsionAngleType {
    private static final Calpha INSTANCE = new Calpha();

    public static Calpha getInstance() {
        return Calpha.INSTANCE;
    }

    private Calpha() {
        super(MoleculeType.PROTEIN, Unicode.CALPHA, new Quadruplet<AtomName>(AtomName.CA, AtomName.CA, AtomName.CA, AtomName.CA), new Quadruplet<Integer>(0, 1, 2, 3));
    }
}
