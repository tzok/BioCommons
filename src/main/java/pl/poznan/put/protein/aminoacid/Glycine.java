package pl.poznan.put.protein.aminoacid;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;

import java.util.Arrays;

public class Glycine extends ProteinSidechain {
    private static final Glycine INSTANCE = new Glycine();

    private Glycine() {
        super(Arrays.asList(AtomName.HA1, AtomName.HA2), "Glycine", 'G', "GLY");
    }

    public static Glycine getInstance() {
        return Glycine.INSTANCE;
    }

    @Override
    protected void fillChiAtomsMap() {
        // empty method, Glycine does not have CHI angles
    }
}
