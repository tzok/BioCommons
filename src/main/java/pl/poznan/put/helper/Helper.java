package pl.poznan.put.helper;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Group;

import pl.poznan.put.nucleotide.AtomName;

public class Helper {
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
}
