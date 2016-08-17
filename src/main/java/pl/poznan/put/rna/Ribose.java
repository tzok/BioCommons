package pl.poznan.put.rna;

import pl.poznan.put.atom.AtomName;

import java.util.Arrays;

public class Ribose extends Sugar {
    private static final Ribose INSTANCE = new Ribose();

    private Ribose() {
        super(Arrays.asList(AtomName.C5p, AtomName.H5p, AtomName.H5pp,
                            AtomName.C4p, AtomName.H4p, AtomName.O4p,
                            AtomName.C3p, AtomName.H3p, AtomName.C2p,
                            AtomName.O2p, AtomName.H2p, AtomName.H2pp,
                            AtomName.C1p, AtomName.H1p));
    }

    public static Ribose getInstance() {
        return Ribose.INSTANCE;
    }
}
