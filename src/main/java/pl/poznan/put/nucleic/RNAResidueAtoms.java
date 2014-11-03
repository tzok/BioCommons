package pl.poznan.put.nucleic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.ResidueType;

public class RNAResidueAtoms {
    private static final Map<ResidueType, List<AtomName>> MAP = new HashMap<ResidueType, List<AtomName>>();

    static {
        List<AtomName> atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.N9);
        atoms.add(AtomName.C4);
        atoms.add(AtomName.N2);
        atoms.add(AtomName.H21);
        atoms.add(AtomName.H22);
        atoms.add(AtomName.N3);
        atoms.add(AtomName.C2);
        atoms.add(AtomName.N1);
        atoms.add(AtomName.H1);
        atoms.add(AtomName.C6);
        atoms.add(AtomName.O6);
        atoms.add(AtomName.C5);
        atoms.add(AtomName.N7);
        atoms.add(AtomName.C8);
        atoms.add(AtomName.H8);
        RNAResidueAtoms.MAP.put(ResidueType.GUANINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.N9);
        atoms.add(AtomName.C5);
        atoms.add(AtomName.N7);
        atoms.add(AtomName.C8);
        atoms.add(AtomName.H8);
        atoms.add(AtomName.N1);
        atoms.add(AtomName.C2);
        atoms.add(AtomName.H2);
        atoms.add(AtomName.N3);
        atoms.add(AtomName.C4);
        atoms.add(AtomName.C6);
        atoms.add(AtomName.N6);
        atoms.add(AtomName.H61);
        atoms.add(AtomName.H62);
        RNAResidueAtoms.MAP.put(ResidueType.ADENINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.N1);
        atoms.add(AtomName.C6);
        atoms.add(AtomName.H6);
        atoms.add(AtomName.C5);
        atoms.add(AtomName.H5);
        atoms.add(AtomName.C2);
        atoms.add(AtomName.O2);
        atoms.add(AtomName.N3);
        atoms.add(AtomName.C4);
        atoms.add(AtomName.N4);
        atoms.add(AtomName.H41);
        atoms.add(AtomName.H42);
        RNAResidueAtoms.MAP.put(ResidueType.CYTOSINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.N1);
        atoms.add(AtomName.C6);
        atoms.add(AtomName.H6);
        atoms.add(AtomName.C2);
        atoms.add(AtomName.O2);
        atoms.add(AtomName.N3);
        atoms.add(AtomName.H3);
        atoms.add(AtomName.C4);
        atoms.add(AtomName.O4);
        atoms.add(AtomName.C5);
        atoms.add(AtomName.C5M);
        atoms.add(AtomName.H51);
        atoms.add(AtomName.H52);
        atoms.add(AtomName.H53);
        RNAResidueAtoms.MAP.put(ResidueType.THYMINE, atoms);

        atoms = new ArrayList<AtomName>();
        atoms.add(AtomName.N1);
        atoms.add(AtomName.C6);
        atoms.add(AtomName.H6);
        atoms.add(AtomName.C2);
        atoms.add(AtomName.O2);
        atoms.add(AtomName.N3);
        atoms.add(AtomName.H3);
        atoms.add(AtomName.C4);
        atoms.add(AtomName.O4);
        atoms.add(AtomName.C5);
        atoms.add(AtomName.H5);
        RNAResidueAtoms.MAP.put(ResidueType.URACIL, atoms);
    }

    public static AtomName[] getAtoms(ResidueType residueType) {
        if (residueType == null
                || !RNAResidueAtoms.MAP.containsKey(residueType)) {
            return new AtomName[0];
        }

        List<AtomName> list = RNAResidueAtoms.MAP.get(residueType);
        return list.toArray(new AtomName[list.size()]);
    }

    private RNAResidueAtoms() {
    }
}
