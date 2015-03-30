package pl.poznan.put.rna;

import java.util.List;

import pl.poznan.put.atom.AtomName;

public abstract class Sugar extends NucleicAcidResidueComponent {
    protected Sugar(List<AtomName> atoms) {
        super(RNAResidueComponentType.SUGAR, atoms);
    }
}
