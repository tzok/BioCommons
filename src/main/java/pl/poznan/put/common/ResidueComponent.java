package pl.poznan.put.common;

import java.util.List;

import pl.poznan.put.atom.AtomName;

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
}
