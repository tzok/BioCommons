package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinChiType;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.types.Quadruplet;

public class IsoLeucine extends ProteinSidechain {
    private static final IsoLeucine INSTANCE = new IsoLeucine();

    public static IsoLeucine getInstance() {
        return IsoLeucine.INSTANCE;
    }

    private IsoLeucine() {
        super(Arrays.asList(new AtomName[] { AtomName.CB, AtomName.HB, AtomName.CG1, AtomName.HG11, AtomName.HG12, AtomName.CG2, AtomName.HG21, AtomName.HG22, AtomName.HG23, AtomName.CD1, AtomName.HD11, AtomName.HD12, AtomName.HD13 }), "Isoleucine", 'I', "ILE");
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1));
        chiAtoms.put(ProteinChiType.CHI2, new Quadruplet<AtomName>(AtomName.CA, AtomName.CB, AtomName.CG1, AtomName.CD1));
    }
}
