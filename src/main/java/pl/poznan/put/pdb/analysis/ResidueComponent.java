package pl.poznan.put.pdb.analysis;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import pl.poznan.put.atom.AtomName;

public abstract class ResidueComponent {
    private final String residueComponentName;
    private final MoleculeType moleculeType;
    private final List<AtomName> atoms;

    /**
     * These atoms may or may not be present. Their lack is OK, but if they are present it is good to know.
     */
    private final List<AtomName> additionalAtoms;

    protected ResidueComponent(String residueComponentName,
                               MoleculeType moleculeType, List<AtomName> atoms, List<AtomName> additionalAtoms) {
        super();
        this.residueComponentName = residueComponentName;
        this.moleculeType = moleculeType;
        this.atoms = atoms;
        this.additionalAtoms = additionalAtoms;
    }

    protected ResidueComponent(String residueComponentName,
                               MoleculeType moleculeType, List<AtomName> atoms) {
        this(residueComponentName, moleculeType, atoms, Collections.<AtomName>emptyList());
    }

    public String getResidueComponentName() {
        return residueComponentName;
    }

    public MoleculeType getMoleculeType() {
        return moleculeType;
    }

    public List<AtomName> getAtoms() {
        return atoms;
    }

    public List<AtomName> getAdditionalAtoms() {
        return additionalAtoms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResidueComponent that = (ResidueComponent) o;

        if (!residueComponentName.equals(that.residueComponentName)) return false;
        if (moleculeType != that.moleculeType) return false;
        if (!CollectionUtils.isEqualCollection(atoms, that.atoms)) return false;
        return CollectionUtils.isEqualCollection(atoms, that.atoms);

    }

    @Override
    public int hashCode() {
        int result = residueComponentName.hashCode();
        result = 31 * result + moleculeType.hashCode();
        result = 31 * result + atoms.hashCode();
        result = 31 * result + additionalAtoms.hashCode();
        return result;
    }
}
