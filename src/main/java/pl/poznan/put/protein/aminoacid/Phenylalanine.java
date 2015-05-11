package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.ProteinChiType;

public class Phenylalanine extends ProteinSidechain {
    private static final Phenylalanine INSTANCE = new Phenylalanine();

    public static Phenylalanine getInstance() {
        return Phenylalanine.INSTANCE;
    }

    private Phenylalanine() {
        super(Arrays.asList(new AtomName[] { AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.CG, AtomName.CD1, AtomName.HD1, AtomName.CE1, AtomName.HE1, AtomName.CZ, AtomName.HZ, AtomName.CD2, AtomName.HD2, AtomName.CE2, AtomName.HE2 }), "Phenylalanine", 'F', "PHE");
        torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
        torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, Chi1.PHENYLALANINE_ATOMS);
        chiAtoms.put(ProteinChiType.CHI2, Chi2.PHENYLALANINE_ATOMS);
    }
}
