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

public final class StructureHelper {
    private StructureHelper() {
        super();
    }

    public static Atom[] findAtoms(
            final Group residue, final AtomName... atomNames) {
        final List<Atom> atoms = new ArrayList<>();
        for (final AtomName atomName : atomNames) {
            atoms.add(StructureHelper.findAtom(residue, atomName));
        }
        return atoms.toArray(new Atom[atoms.size()]);
    }

    public static Atom findAtom(final Group residue, final AtomName atomName) {
        for (final Atom atom : residue.getAtoms()) {
            if (atomName.matchesName(atom.getName())) {
                return atom;
            }
        }
        return null;
    }

    public static Atom[] findAllAtoms(
            final Structure structure, final AtomName atomName) {
        final List<Atom> result = new ArrayList<>();
        for (final Chain chain : structure.getChains()) {
            final Atom[] atomsChain = StructureHelper.findAllAtoms(chain, atomName);
            result.addAll(Arrays.asList(atomsChain));
        }
        return result.toArray(new Atom[result.size()]);
    }

    public static Atom[] findAllAtoms(
            final Chain chain, final AtomName atomName) {
        final List<Atom> result = new ArrayList<>();

        for (final Group group : chain.getAtomGroups()) {
            final Atom atom = StructureHelper.findAtom(group, atomName);
            if (atom != null) {
                result.add(atom);
            }
        }

        return result.toArray(new Atom[result.size()]);
    }

    public static void mergeAltLocs(final Group group) {
        final LinkedHashSet<Atom> atoms = new LinkedHashSet<>(group.getAtoms());

        for (final Group altloc : group.getAltLocs()) {
            for (final Atom atom : altloc.getAtoms()) {
                if (!atoms.contains(atom)) {
                    atoms.add(atom);
                }
            }
        }

        group.setAtoms(new ArrayList<>(atoms));
    }

    public static boolean isModified(
            final Group group, final AtomName... atomNames) {
        for (final AtomName atomName : atomNames) {
            if (atomName.getType().isHeavy() && (
                    StructureHelper.findAtom(group, atomName) == null)) {
                return true;
            }
        }
        return false;
    }
}
