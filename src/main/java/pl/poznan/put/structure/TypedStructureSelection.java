package pl.poznan.put.structure;

import java.util.List;

import org.biojava.bio.structure.Group;

import pl.poznan.put.common.MoleculeType;

public class TypedStructureSelection extends StructureSelection {
    private final MoleculeType moleculeType;

    public TypedStructureSelection(String name, List<Group> residues,
            MoleculeType moleculeType) {
        super(name, residues);
        this.moleculeType = moleculeType;
    }

    public MoleculeType getMoleculeType() {
        return moleculeType;
    }
}
