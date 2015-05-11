package pl.poznan.put.protein.torsion;

import java.util.HashMap;
import java.util.Map;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Chi1 extends AtomBasedTorsionAngleType {
    private static final Map<Quadruplet<AtomName>, Chi1> INSTANCE_CACHE = new HashMap<Quadruplet<AtomName>, Chi1>();

    public static Chi1 getInstance(Quadruplet<AtomName> chiAtoms) {
        if (!Chi1.INSTANCE_CACHE.containsKey(chiAtoms)) {
            Chi1.INSTANCE_CACHE.put(chiAtoms, new Chi1(chiAtoms));
        }

        return Chi1.INSTANCE_CACHE.get(chiAtoms);
    }

    private Chi1(Quadruplet<AtomName> atoms) {
        super(MoleculeType.PROTEIN, Unicode.CHI1, atoms, new Quadruplet<Integer>(0, 0, 0, 0));
    }

}
