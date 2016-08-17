package pl.poznan.put.protein.aminoacid;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;

import java.util.Arrays;

public class Alanine extends ProteinSidechain {
    private static final Alanine INSTANCE = new Alanine();

    private Alanine() {
        super(Arrays.asList(AtomName.CB, AtomName.HB1, AtomName.HB2,
                            AtomName.HB3), "Alanine", 'A', "ALA");
    }

    public static Alanine getInstance() {
        return Alanine.INSTANCE;
    }

    @Override
    protected void fillChiAtomsMap() {
        // empty method, Alanine does not have CHI angles
    }
}
