package pl.poznan.put.rna;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;

public class Phosphate extends NucleicAcidResidueComponent {
    private static final Phosphate INSTANCE = new Phosphate();

    public static final Phosphate getInstance() {
        return Phosphate.INSTANCE;
    }

    private Phosphate() {
        super(RNAResidueComponentType.PHOSPHATE, Arrays.asList(new AtomName[] { AtomName.P, AtomName.O1P, AtomName.O2P, AtomName.O3p, AtomName.O5p }));
    }
}
