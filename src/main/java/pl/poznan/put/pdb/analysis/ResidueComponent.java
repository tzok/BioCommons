package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.CollectionUtils;
import pl.poznan.put.atom.AtomName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class ResidueComponent {
    private final String residueComponentName;
    private final MoleculeType moleculeType;
    private final List<AtomName> atoms;

    /**
     * These atoms may or may not be present. Their lack is OK, but if they are
     * present it is good to know.
     */
    private final List<AtomName> additionalAtoms;

    protected ResidueComponent(
            final String residueComponentName, final MoleculeType moleculeType,
            final List<AtomName> atoms) {
        this(residueComponentName, moleculeType, atoms,
             Collections.emptyList());
    }

    protected ResidueComponent(
            final String residueComponentName, final MoleculeType moleculeType,
            final List<AtomName> atoms, final List<AtomName> additionalAtoms) {
        super();
        this.residueComponentName = residueComponentName;
        this.moleculeType = moleculeType;
        this.atoms = new ArrayList<>(atoms);
        this.additionalAtoms = new ArrayList<>(additionalAtoms);
    }

    public final String getResidueComponentName() {
        return residueComponentName;
    }

    public final MoleculeType getMoleculeType() {
        return moleculeType;
    }

    public final List<AtomName> getAtoms() {
        return Collections.unmodifiableList(atoms);
    }

    public final Collection<AtomName> getAdditionalAtoms() {
        return Collections.unmodifiableList(additionalAtoms);
    }

    public final boolean hasAtom(final AtomName atomName) {
        return atoms.contains(atomName) || additionalAtoms.contains(atomName);
    }

    @Override
    public final int hashCode() {
        int result = residueComponentName.hashCode();
        result = (31 * result) + moleculeType.hashCode();
        result = (31 * result) + atoms.hashCode();
        result = (31 * result) + additionalAtoms.hashCode();
        return result;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        final ResidueComponent other = (ResidueComponent) o;
        return Objects.equals(residueComponentName, other.residueComponentName)
               && (moleculeType == other.moleculeType) && CollectionUtils
                       .isEqualCollection(atoms, other.atoms) && CollectionUtils
                       .isEqualCollection(additionalAtoms,
                                          other.additionalAtoms);
    }
}
