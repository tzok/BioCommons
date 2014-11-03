package pl.poznan.put.nucleic;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.atoms.AtomName;

public class RNABackboneAtoms {
    private static final List<AtomName> ATOMS = new ArrayList<AtomName>();

    static {
        RNABackboneAtoms.ATOMS.add(AtomName.P);
        RNABackboneAtoms.ATOMS.add(AtomName.O1P);
        RNABackboneAtoms.ATOMS.add(AtomName.O2P);
        RNABackboneAtoms.ATOMS.add(AtomName.O5p);
        RNABackboneAtoms.ATOMS.add(AtomName.C5p);
        RNABackboneAtoms.ATOMS.add(AtomName.H5p);
        RNABackboneAtoms.ATOMS.add(AtomName.H5pp);
        RNABackboneAtoms.ATOMS.add(AtomName.C4p);
        RNABackboneAtoms.ATOMS.add(AtomName.H4p);
        RNABackboneAtoms.ATOMS.add(AtomName.O4p);
        RNABackboneAtoms.ATOMS.add(AtomName.C1p);
        RNABackboneAtoms.ATOMS.add(AtomName.H1p);
        RNABackboneAtoms.ATOMS.add(AtomName.C2p);
        RNABackboneAtoms.ATOMS.add(AtomName.H2pp);
        RNABackboneAtoms.ATOMS.add(AtomName.O2p);
        RNABackboneAtoms.ATOMS.add(AtomName.H2p);
        RNABackboneAtoms.ATOMS.add(AtomName.C3p);
        RNABackboneAtoms.ATOMS.add(AtomName.H3p);
        RNABackboneAtoms.ATOMS.add(AtomName.O3p);
    }

    public static AtomName[] getAtoms() {
        return RNABackboneAtoms.ATOMS.toArray(new AtomName[RNABackboneAtoms.ATOMS.size()]);
    }

    private RNABackboneAtoms() {
    }
}
