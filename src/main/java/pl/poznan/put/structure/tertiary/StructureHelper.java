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
import java.util.Objects;

final class StructureHelper {
  private StructureHelper() {
    super();
  }

  public static Atom[] findAtoms(final Group residue, final AtomName... atomNames) {
    return Arrays.stream(atomNames)
        .map(atomName -> StructureHelper.findAtom(residue, atomName))
        .toArray(Atom[]::new);
  }

  private static Atom findAtom(final Group residue, final AtomName atomName) {
    return residue.getAtoms().stream()
        .filter(atom -> atomName.matchesName(atom.getName()))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format("Failed to find %s in residue %s", atomName, residue)));
  }

  public static Atom[] findAllAtoms(final Structure structure, final AtomName atomName) {
    final List<Atom> result = new ArrayList<>();
    structure.getChains().stream()
        .map(chain -> StructureHelper.findAllAtoms(chain, atomName))
        .map(Arrays::asList)
        .forEach(result::addAll);
    return result.toArray(new Atom[0]);
  }

  private static Atom[] findAllAtoms(final Chain chain, final AtomName atomName) {
    return chain.getAtomGroups().stream()
        .map(group -> StructureHelper.findAtom(group, atomName))
        .filter(Objects::nonNull)
        .toArray(Atom[]::new);
  }

  public static void mergeAltLocs(final Group group) {
    final LinkedHashSet<Atom> atoms = new LinkedHashSet<>(group.getAtoms());

    for (final Group altloc : group.getAltLocs()) {
      atoms.addAll(altloc.getAtoms());
    }

    group.setAtoms(new ArrayList<>(atoms));
  }

  public static boolean isModified(final Group group, final AtomName... atomNames) {
    return Arrays.stream(atomNames)
        .anyMatch(
            atomName ->
                atomName.getType().isHeavy()
                    && (StructureHelper.findAtom(group, atomName) == null));
  }
}
