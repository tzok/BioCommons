package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinChiType;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.types.Quadruplet;

public class Methionine extends ProteinSidechain {
    private static final Methionine INSTANCE = new Methionine();

    public static Methionine getInstance() {
        return Methionine.INSTANCE;
    }

    private Methionine() {
        super(Arrays.asList(new AtomName[] { AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.CG, AtomName.HG1, AtomName.HG2, AtomName.SD, AtomName.CE, AtomName.HE1, AtomName.HE2, AtomName.HE3 }), "Methionine", 'M', "MET");
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG));
        chiAtoms.put(ProteinChiType.CHI2, new Quadruplet<AtomName>(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.SD));
        chiAtoms.put(ProteinChiType.CHI3, new Quadruplet<AtomName>(AtomName.CB, AtomName.CG, AtomName.SD, AtomName.CE));
    }
}
