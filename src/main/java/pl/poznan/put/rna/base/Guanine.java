package pl.poznan.put.rna.base;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.rna.Purine;
import pl.poznan.put.rna.Ribose;
import pl.poznan.put.rna.Sugar;

public class Guanine extends Purine {
    private static final Guanine INSTANCE = new Guanine();

    public static Guanine getInstance() {
        return Guanine.INSTANCE;
    }

    private Guanine() {
        super(Arrays.asList(new AtomName[] { AtomName.N9, AtomName.C4, AtomName.N2, AtomName.H21, AtomName.H22, AtomName.N3, AtomName.C2, AtomName.N1, AtomName.H1, AtomName.C6, AtomName.O6, AtomName.C5, AtomName.N7, AtomName.C8, AtomName.H8 }), "Guanine", 'G', "G", "GUA");
    }

    @Override
    public Sugar getDefaultSugarInstance() {
        return Ribose.getInstance();
    }
}
