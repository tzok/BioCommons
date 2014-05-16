package pl.poznan.put.helper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Structure;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.common.ResidueType;

public class StructureHelper {
    public static Atom findAtom(Group residue, AtomName atomName) {
        for (Atom atom : residue.getAtoms()) {
            if (atomName.matchesName(atom.getFullName())) {
                return atom;
            }
        }
        return null;
    }

    public static List<Atom> findAllAtoms(Chain chain, AtomName atomName) {
        List<Atom> result = new ArrayList<Atom>();

        for (Group group : chain.getAtomGroups()) {
            Atom atom = StructureHelper.findAtom(group, atomName);
            if (atom != null) {
                result.add(atom);
            }
        }

        return result;
    }

    public static List<Atom> findAllAtoms(Structure structure, AtomName atomName) {
        List<Atom> result = new ArrayList<Atom>();

        for (Chain chain : structure.getChains()) {
            result.addAll(StructureHelper.findAllAtoms(chain, atomName));
        }

        return result;
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

    private StructureHelper() {
    }
}
