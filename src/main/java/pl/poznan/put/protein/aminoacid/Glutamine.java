package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinChiType;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.types.Quadruplet;

public class Glutamine extends ProteinSidechain {
    private static final Glutamine INSTANCE = new Glutamine();

    public static Glutamine getInstance() {
        return Glutamine.INSTANCE;
    }

    private Glutamine() {
        super(Arrays.asList(new AtomName[] { AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.CG, AtomName.HG1, AtomName.HG2, AtomName.CD, AtomName.OE1, AtomName.NE2, AtomName.HE21, AtomName.HE22 }), "Glutamine", 'Q', "GLN");
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG));
        chiAtoms.put(ProteinChiType.CHI2, new Quadruplet<AtomName>(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD));
        chiAtoms.put(ProteinChiType.CHI3, new Quadruplet<AtomName>(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.OE1));
    }
}
