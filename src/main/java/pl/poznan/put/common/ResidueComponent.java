package pl.poznan.put.common;

import java.util.List;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.MoleculeType;

public abstract class ResidueComponent implements AtomContainer {
    private final String residueComponentName;
    private final MoleculeType moleculeType;
    private final List<AtomName> atoms;

    protected ResidueComponent(String residueComponentName, MoleculeType moleculeType, List<AtomName> atoms) {
        super();
        this.residueComponentName = residueComponentName;
        this.moleculeType = moleculeType;
        this.atoms = atoms;
    }

    public String getResidueComponentName() {
        return residueComponentName;
    }

    public MoleculeType getMoleculeType() {
        return moleculeType;
    }

    @Override
    public List<AtomName> getAtoms() {
        return atoms;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (atoms == null ? 0 : atoms.hashCode());
        result = prime * result + (moleculeType == null ? 0 : moleculeType.hashCode());
        result = prime * result + (residueComponentName == null ? 0 : residueComponentName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ResidueComponent other = (ResidueComponent) obj;
        if (atoms == null) {
            if (other.atoms != null) {
                return false;
            }
        } else if (!atoms.equals(other.atoms)) {
            return false;
        }
        if (moleculeType != other.moleculeType) {
            return false;
        }
        if (residueComponentName == null) {
            if (other.residueComponentName != null) {
                return false;
            }
        } else if (!residueComponentName.equals(other.residueComponentName)) {
            return false;
        }
        return true;
    }
}
