package pl.poznan.put.protein.torsion;

import java.util.HashMap;
import java.util.Map;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Chi2 extends AtomBasedTorsionAngleType {
    private static final Map<Quadruplet<AtomName>, Chi2> INSTANCE_CACHE = new HashMap<Quadruplet<AtomName>, Chi2>();

    public static Chi2 getInstance(Quadruplet<AtomName> chiAtoms) {
        if (!Chi2.INSTANCE_CACHE.containsKey(chiAtoms)) {
            Chi2.INSTANCE_CACHE.put(chiAtoms, new Chi2(chiAtoms));
        }

        return Chi2.INSTANCE_CACHE.get(chiAtoms);
    }

    private Chi2(Quadruplet<AtomName> atoms) {
        super(MoleculeType.PROTEIN, Unicode.CHI2, atoms, new Quadruplet<Integer>(0, 0, 0, 0));
    }

}
