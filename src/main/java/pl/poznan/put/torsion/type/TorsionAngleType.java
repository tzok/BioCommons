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
}