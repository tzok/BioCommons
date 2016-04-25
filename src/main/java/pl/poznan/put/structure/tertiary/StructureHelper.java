package pl.poznan.put.structure.tertiary;

import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import pl.poznan.put.atom.AtomName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class StructureHelper {
    public static Atom findAtom(Group residue, AtomName atomName) {
        for (Atom atom : residue.getAtoms()) {
            if (atomName.matchesName(atom.getName())) {
                return atom;
            }
        }
        return null;
    }

    public static Atom[] findAtoms(Group residue, AtomName[] atomNames) {
        List<Atom> atoms = new ArrayList<Atom>();
        for (AtomName atomName : atomNames) {
            atoms.add(StructureHelper.findAtom(residue, atomName));
        }
        return atoms.toArray(new Atom[atoms.size()]);
    }

    public static Atom[] findAllAtoms(Chain chain, AtomName atomName) {
        List<Atom> result = new ArrayList<Atom>();

        for (Group group : chain.getAtomGroups()) {
            Atom atom = StructureHelper.findAtom(group, atomName);
            if (atom != null) {
                result.add(atom);
            }
        }

        return result.toArray(new Atom[result.size()]);
    }

    public static Atom[] findAllAtoms(Structure structure, AtomName atomName) {
        List<Atom> result = new ArrayList<Atom>();
        for (Chain chain : structure.getChains()) {
            Atom[] atomsChain = StructureHelper.findAllAtoms(chain, atomName);
            result.addAll(Arrays.asList(atomsChain));
        }
        return result.toArray(new Atom[result.size()]);
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

    public static boolean isModified(Group group, AtomName[] atomNames) {
        for (AtomName atomName : atomNames) {
            if (atomName.getType().isHeavy() && StructureHelper.findAtom(group, atomName) == null) {
                return true;
            }
        }
        return false;
    }

    private StructureHelper() {
    }
}
