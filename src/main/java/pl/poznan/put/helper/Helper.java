package pl.poznan.put.helper;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.common.ResidueType;

public class Helper {
    private Helper() {
    }

    public static Atom findAtom(Group residue, AtomName atomName) {
        for (Atom atom : residue.getAtoms()) {
            if (atomName.matchesName(atom.getFullName())) {
                return atom;
            }
        }
        return null;
    }

    public static String getSequence(Chain chain) {
        StringBuilder builder = new StringBuilder();
        MoleculeType chainType = MoleculeType.detect(chain);

        for (Group residue : chain.getAtomGroups()) {
            ResidueType type = ResidueType.fromString(chainType,
                    residue.getPDBName());

            if (type == ResidueType.UNKNOWN) {
                type = ResidueType.detect(residue);
            }

            builder.append(type.getOneLetter());
        }

        return builder.toString();
    }

    public static void mergeAltLocs(Group group) {
        LinkedHashSet<Atom> atoms = new LinkedHashSet<Atom>();
        atoms.addAll(group.getAtoms());

        for (Group altloc : group.getAltLocs()) {
            for (Atom atom : altloc.getAtoms()) {
                if (!atoms.contains(atom)) {
                    atoms.add(atom);
                }
            }
        }

        group.setAtoms(new ArrayList<Atom>(atoms));
    }
}
