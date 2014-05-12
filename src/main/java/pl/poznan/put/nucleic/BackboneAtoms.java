package pl.poznan.put.nucleic;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.atoms.AtomName;

public class BackboneAtoms {
    private static final List<AtomName> ATOMS = new ArrayList<AtomName>();

    static {
        BackboneAtoms.ATOMS.add(AtomName.P);
        BackboneAtoms.ATOMS.add(AtomName.O1P);
        BackboneAtoms.ATOMS.add(AtomName.O2P);
        BackboneAtoms.ATOMS.add(AtomName.O5p);
        BackboneAtoms.ATOMS.add(AtomName.C5p);
        BackboneAtoms.ATOMS.add(AtomName.H5p);
        BackboneAtoms.ATOMS.add(AtomName.H5pp);
        BackboneAtoms.ATOMS.add(AtomName.C4p);
        BackboneAtoms.ATOMS.add(AtomName.H4p);
        BackboneAtoms.ATOMS.add(AtomName.O4p);
        BackboneAtoms.ATOMS.add(AtomName.C1p);
        BackboneAtoms.ATOMS.add(AtomName.H1p);
        BackboneAtoms.ATOMS.add(AtomName.C2p);
        BackboneAtoms.ATOMS.add(AtomName.H2pp);
        BackboneAtoms.ATOMS.add(AtomName.O2p);
        BackboneAtoms.ATOMS.add(AtomName.H2p);
        BackboneAtoms.ATOMS.add(AtomName.C3p);
        BackboneAtoms.ATOMS.add(AtomName.H3p);
        BackboneAtoms.ATOMS.add(AtomName.O3p);
    }

    public static List<AtomName> getAtoms() {
        return BackboneAtoms.ATOMS;
    }

    private BackboneAtoms() {
    }
}
