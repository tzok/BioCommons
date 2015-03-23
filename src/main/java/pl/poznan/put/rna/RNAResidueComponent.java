package pl.poznan.put.rna;

import java.util.List;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.common.ResidueComponent;

public abstract class RNAResidueComponent extends ResidueComponent {
    private final RNAResidueComponentType type;

    protected RNAResidueComponent(RNAResidueComponentType type, List<AtomName> atoms) {
        super(type.name().toLowerCase(), MoleculeType.RNA, atoms);
        this.type = type;
    }

    public RNAResidueComponentType getType() {
        return type;
    }
}
