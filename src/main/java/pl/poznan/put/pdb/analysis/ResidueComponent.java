package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.CollectionUtils;
import pl.poznan.put.atom.AtomName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
             Collections.<AtomName>emptyList());
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

    public final List<AtomName> getAdditionalAtoms() {
        return Collections.unmodifiableList(additionalAtoms);
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
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        ResidueComponent other = (ResidueComponent) obj;
        return residueComponentName.equals(other.residueComponentName) && (
                moleculeType == other.moleculeType) && CollectionUtils
                       .isEqualCollection(atoms, other.atoms) && CollectionUtils
                       .isEqualCollection(additionalAtoms,
                                          other.additionalAtoms);
    }
}
