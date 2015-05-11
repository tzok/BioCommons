package pl.poznan.put.protein.torsion;

import java.util.HashMap;
import java.util.Map;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Chi3 extends AtomBasedTorsionAngleType {
    private static final Map<Quadruplet<AtomName>, Chi3> INSTANCE_CACHE = new HashMap<Quadruplet<AtomName>, Chi3>();

    public static Chi3 getInstance(ProteinSidechain sidechain) {
        Quadruplet<AtomName> chiAtoms = sidechain.getChiAtoms(ProteinChiType.CHI3);

        if (!Chi3.INSTANCE_CACHE.containsKey(chiAtoms)) {
            Chi3.INSTANCE_CACHE.put(chiAtoms, new Chi3(chiAtoms));
        }

        return Chi3.INSTANCE_CACHE.get(chiAtoms);
    }

    private Chi3(Quadruplet<AtomName> atoms) {
        super(MoleculeType.PROTEIN, Unicode.CHI3, atoms, new Quadruplet<Integer>(0, 0, 0, 0));
    }

}
