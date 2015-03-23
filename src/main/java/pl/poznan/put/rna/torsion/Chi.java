package pl.poznan.put.rna.torsion;

import java.util.HashMap;
import java.util.Map;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.rna.Base;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Chi extends AtomBasedTorsionAngleType {
    private static final Map<Quadruplet<AtomName>, Chi> INSTANCE_CACHE = new HashMap<Quadruplet<AtomName>, Chi>();

    public static Chi getInstance(Base rnaBase) {
        Quadruplet<AtomName> chiAtoms = rnaBase.getChiAtoms();

        if (!Chi.INSTANCE_CACHE.containsKey(chiAtoms)) {
            Chi.INSTANCE_CACHE.put(chiAtoms, new Chi(chiAtoms));
        }

        return Chi.INSTANCE_CACHE.get(chiAtoms);
    }

    private Chi(Quadruplet<AtomName> atoms) {
        super(MoleculeType.RNA, Unicode.CHI, atoms, new Quadruplet<Integer>(0, 0, 0, 0));
    }
}
