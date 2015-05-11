package pl.poznan.put.rna.torsion;

import java.util.HashMap;
import java.util.Map;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.rna.BaseType;
import pl.poznan.put.rna.Purine;
import pl.poznan.put.rna.Pyrimidine;
import pl.poznan.put.torsion.type.AtomBasedTorsionAngleType;
import pl.poznan.put.types.Quadruplet;

public class Chi extends AtomBasedTorsionAngleType {
    private static final Map<Quadruplet<AtomName>, Chi> INSTANCE_CACHE = new HashMap<Quadruplet<AtomName>, Chi>();

    public static Chi getInstance(BaseType baseType) {
        Quadruplet<AtomName> chiAtoms;

        switch (baseType) {
        case PURINE:
            chiAtoms = Purine.chiAtoms();
            break;
        case PYRIMIDINE:
            chiAtoms = Pyrimidine.chiAtoms();
            break;
        default:
            chiAtoms = new Quadruplet<AtomName>(AtomName.UNKNOWN, AtomName.UNKNOWN, AtomName.UNKNOWN, AtomName.UNKNOWN);
        }

        if (!Chi.INSTANCE_CACHE.containsKey(chiAtoms)) {
            Chi.INSTANCE_CACHE.put(chiAtoms, new Chi(chiAtoms, baseType));
        }

        return Chi.INSTANCE_CACHE.get(chiAtoms);
    }

    private final BaseType baseType;

    private Chi(Quadruplet<AtomName> atoms, BaseType baseType) {
        super(MoleculeType.RNA, Unicode.CHI, atoms, new Quadruplet<Integer>(0, 0, 0, 0));
        this.baseType = baseType;
    }

    public BaseType getBaseType() {
        return baseType;
    }
}
