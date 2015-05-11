package pl.poznan.put.protein.torsion;

import java.util.HashMap;
import java.util.Map;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Chi4 extends AtomBasedTorsionAngleType {
    private static final Map<Quadruplet<AtomName>, Chi4> INSTANCE_CACHE = new HashMap<Quadruplet<AtomName>, Chi4>();

    public static Chi4 getInstance(ProteinSidechain sidechain) {
        Quadruplet<AtomName> chiAtoms = sidechain.getChiAtoms(ProteinChiType.CHI4);

        if (!Chi4.INSTANCE_CACHE.containsKey(chiAtoms)) {
            Chi4.INSTANCE_CACHE.put(chiAtoms, new Chi4(chiAtoms));
        }

        return Chi4.INSTANCE_CACHE.get(chiAtoms);
    }

    private Chi4(Quadruplet<AtomName> atoms) {
        super(MoleculeType.PROTEIN, Unicode.CHI4, atoms, new Quadruplet<Integer>(0, 0, 0, 0));
    }

}
