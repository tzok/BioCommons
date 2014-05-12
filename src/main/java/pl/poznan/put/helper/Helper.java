package pl.poznan.put.helper;

import java.util.List;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Group;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.nucleic.BackboneAtoms;

public class Helper {
    private static final int MINIMUM_MATCHING_ATOMS_REQUIRED = 3;

    private Helper() {
    }

    public static Atom findAtom(Group residue, AtomName atomType) {
        for (Atom atom : residue.getAtoms()) {
            if (atomType.matchesName(atom.getFullName())) {
                return atom;
            }
        }
        return null;
    }

    public static boolean isNucleic(Group residue) {
        List<AtomName> backboneAtoms = BackboneAtoms.getAtoms();
        int counter = 0;

        for (Atom atom : residue.getAtoms()) {
            String pdbName = atom.getName().trim();

            for (AtomName backboneAtom : backboneAtoms) {
                if (backboneAtom.matchesName(pdbName)) {
                    counter++;
                    break;
                }
            }

            if (counter >= Helper.MINIMUM_MATCHING_ATOMS_REQUIRED) {
                return true;
            }
        }

        return false;
    }
}
