package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinChiType;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.types.Quadruplet;

public class GlutamicAcid extends ProteinSidechain {
    private static final GlutamicAcid INSTANCE = new GlutamicAcid();

    public static GlutamicAcid getInstance() {
        return GlutamicAcid.INSTANCE;
    }

    private GlutamicAcid() {
        super(Arrays.asList(new AtomName[] { AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.CG, AtomName.HG1, AtomName.HG2, AtomName.CD, AtomName.OE1, AtomName.OE2 }), "Glutamic acid", 'E', "GLU");
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG));
        chiAtoms.put(ProteinChiType.CHI2, new Quadruplet<AtomName>(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD));
        chiAtoms.put(ProteinChiType.CHI3, new Quadruplet<AtomName>(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.OE1));
    }
}
