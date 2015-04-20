package pl.poznan.put.torsion.type;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.interfaces.DisplayableExportable;

public abstract class TorsionAngleType implements DisplayableExportable {
    private final MoleculeType moleculeType;

    protected TorsionAngleType(MoleculeType moleculeType) {
        super();
        this.moleculeType = moleculeType;
    }

    public MoleculeType getMoleculeType() {
        return moleculeType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (moleculeType == null ? 0 : moleculeType.hashCode());
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
        TorsionAngleType other = (TorsionAngleType) obj;
        if (moleculeType != other.moleculeType) {
            return false;
        }
        return true;
    }
}