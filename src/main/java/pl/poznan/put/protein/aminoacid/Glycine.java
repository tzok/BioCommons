package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;

public class Glycine extends ProteinSidechain {
    private static final Glycine INSTANCE = new Glycine();

    public static Glycine getInstance() {
        return Glycine.INSTANCE;
    }

    private Glycine() {
        super(Arrays.asList(AtomName.HA1, AtomName.HA2), "Glycine", 'G', "GLY");
    }

    @Override
    protected void fillChiAtomsMap() {
        // empty method, Glycine does not have CHI angles
    }
}
