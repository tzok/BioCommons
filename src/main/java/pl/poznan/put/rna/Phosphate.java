package pl.poznan.put.rna;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;

public class Phosphate extends NucleicAcidResidueComponent {
    private static final Phosphate INSTANCE = new Phosphate();

    public static Phosphate getInstance() {
        return Phosphate.INSTANCE;
    }

    private Phosphate() {
        super(RNAResidueComponentType.PHOSPHATE, Arrays.asList(AtomName.P, AtomName.O1P, AtomName.O2P, AtomName.O3p, AtomName.O5p), Arrays.asList(AtomName.O3P, AtomName.PA, AtomName.O1A, AtomName.O2A, AtomName.O3A, AtomName.PB, AtomName.O1B, AtomName.O2B, AtomName.O3B, AtomName.PC, AtomName.O1C, AtomName.O2C, AtomName.O3C, AtomName.PG, AtomName.O1G, AtomName.O2G, AtomName.O3G));
    }
}
