package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.ProteinChiType;

public class Asparagine extends ProteinSidechain {
    private static final Asparagine INSTANCE = new Asparagine();

    public static Asparagine getInstance() {
        return Asparagine.INSTANCE;
    }

    private Asparagine() {
        super(Arrays.asList(new AtomName[] { AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.CG, AtomName.OD1, AtomName.ND2, AtomName.HD21, AtomName.HD22 }), "Asparagine", 'N', "ASN");
        torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
        torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, Chi1.ASPARAGINE_ATOMS);
        chiAtoms.put(ProteinChiType.CHI2, Chi2.ASPARAGINE_ATOMS);
    }
}
