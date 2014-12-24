package pl.poznan.put.nucleic;

import pl.poznan.put.atoms.AtomName;

public enum NucleotideFragmentType {
    BASE(new AtomName[] { AtomName.C2, AtomName.C4, AtomName.C5, AtomName.C5M, AtomName.C6, AtomName.C8, AtomName.N1, AtomName.N2, AtomName.N3, AtomName.N4, AtomName.N6, AtomName.N7, AtomName.N9, AtomName.O2, AtomName.O4, AtomName.O6, }), SUGAR(new AtomName[] { AtomName.C1p, AtomName.C2p, AtomName.C3p, AtomName.C4p, AtomName.O2p, AtomName.O4p }), PHOSPHATE(new AtomName[] { AtomName.P, AtomName.O1P, AtomName.O2P, AtomName.O3p, AtomName.O5p }), UNKNOWN(new AtomName[] {});

    private final AtomName[] atoms;

    private NucleotideFragmentType(AtomName[] atoms) {
        this.atoms = atoms;
    }

    public static NucleotideFragmentType detect(AtomName atomName) {
        for (NucleotideFragmentType type : NucleotideFragmentType.values()) {
            for (AtomName an : type.atoms) {
                if (an == atomName) {
                    return type;
                }
            }
        }

        return NucleotideFragmentType.UNKNOWN;
    }
}
