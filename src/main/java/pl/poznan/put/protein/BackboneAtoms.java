package pl.poznan.put.protein;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.atoms.AtomName;

public class BackboneAtoms {
    private static final List<AtomName> ATOMS = new ArrayList<AtomName>();

    static {
        BackboneAtoms.ATOMS.add(AtomName.N);
        BackboneAtoms.ATOMS.add(AtomName.HN);
        BackboneAtoms.ATOMS.add(AtomName.CA);
        BackboneAtoms.ATOMS.add(AtomName.HA);
        BackboneAtoms.ATOMS.add(AtomName.C);
        BackboneAtoms.ATOMS.add(AtomName.O);
    }

    public static List<AtomName> getAtoms() {
        return BackboneAtoms.ATOMS;
    }

    private BackboneAtoms() {
    }
}
