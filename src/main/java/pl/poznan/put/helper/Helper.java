package pl.poznan.put.helper;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.ChainType;
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
        ChainType chainType = ChainType.detect(chain);

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
}
