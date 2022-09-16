package pl.poznan.put.pdb.analysis;

import java.util.Collections;
import java.util.Set;
import pl.poznan.put.atom.AtomName;

/** A fragment of a residue with a set of expected atoms and a set of possible other atoms. */
@FunctionalInterface
public interface ResidueComponent {
  /**
   * @return A set of atoms that are expected to be present in this residue component.
   */
  Set<AtomName> requiredAtoms();

  /**
   * @return A set of additional atoms that might be present in this residue component.
   */
  default Set<AtomName> additionalAtoms() {
    return Collections.emptySet();
  }
}
