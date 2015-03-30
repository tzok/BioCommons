package pl.poznan.put.rna.base;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.rna.Purine;
import pl.poznan.put.rna.Ribose;
import pl.poznan.put.rna.Sugar;

public class Adenine extends Purine {
    private static final Adenine INSTANCE = new Adenine();

    public static Adenine getInstance() {
        return Adenine.INSTANCE;
    }

    private Adenine() {
        super(Arrays.asList(new AtomName[] { AtomName.N9, AtomName.C5, AtomName.N7, AtomName.C8, AtomName.H8, AtomName.N1, AtomName.C2, AtomName.H2, AtomName.N3, AtomName.C4, AtomName.C6, AtomName.N6, AtomName.H61, AtomName.H62 }), "Adenine", 'A', "A", "ADE");
    }

    @Override
    public Sugar getDefaultSugarInstance() {
        return Ribose.getInstance();
    }
}
