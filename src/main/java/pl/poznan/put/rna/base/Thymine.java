package pl.poznan.put.rna.base;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.rna.DeoxyRibose;
import pl.poznan.put.rna.Pyrimidine;
import pl.poznan.put.rna.Sugar;

public class Thymine extends Pyrimidine {
    private static final Thymine INSTANCE = new Thymine();

    public static Thymine getInstance() {
        return Thymine.INSTANCE;
    }

    private Thymine() {
        super(Arrays.asList(AtomName.N1, AtomName.C6, AtomName.H6, AtomName.C2, AtomName.O2, AtomName.N3, AtomName.H3, AtomName.C4, AtomName.O4, AtomName.C5, AtomName.C5M, AtomName.H51, AtomName.H52, AtomName.H53), "Thymine", 'T', "T", "THY", "DT");
    }

    @Override
    public Sugar getDefaultSugarInstance() {
        return DeoxyRibose.getInstance();
    }
}
