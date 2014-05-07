package pl.poznan.put.nucleotide;

import java.util.ArrayList;
import java.util.List;

public class PhosphateRiboseAtoms {
    private static final List<AtomName> ATOMS = new ArrayList<AtomName>();

    static {
        PhosphateRiboseAtoms.ATOMS.add(AtomName.P);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.O1P);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.O2P);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.O5p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.C5p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.H5p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.H5pp);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.C4p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.H4p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.O4p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.C1p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.H1p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.C2p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.H2pp);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.O2p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.H2p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.C3p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.H3p);
        PhosphateRiboseAtoms.ATOMS.add(AtomName.O3p);
    }

    public static List<AtomName> getAtoms() {
        return PhosphateRiboseAtoms.ATOMS;
    }

    private PhosphateRiboseAtoms() {
    }
}
