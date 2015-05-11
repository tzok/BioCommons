package pl.poznan.put.protein.torsion;

import java.util.HashMap;
import java.util.Map;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Chi5 extends AtomBasedTorsionAngleType {
    private static final Map<Quadruplet<AtomName>, Chi5> INSTANCE_CACHE = new HashMap<Quadruplet<AtomName>, Chi5>();

    public static Chi5 getInstance(Quadruplet<AtomName> chiAtoms) {
        if (!Chi5.INSTANCE_CACHE.containsKey(chiAtoms)) {
            Chi5.INSTANCE_CACHE.put(chiAtoms, new Chi5(chiAtoms));
        }

        return Chi5.INSTANCE_CACHE.get(chiAtoms);
    }

    private Chi5(Quadruplet<AtomName> atoms) {
        super(MoleculeType.PROTEIN, Unicode.CHI5, atoms, new Quadruplet<Integer>(0, 0, 0, 0));
    }

}
