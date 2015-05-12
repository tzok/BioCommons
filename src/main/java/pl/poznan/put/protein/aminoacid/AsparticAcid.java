package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.ProteinChiType;

public class AsparticAcid extends ProteinSidechain {
    private static final AsparticAcid INSTANCE = new AsparticAcid();

    public static AsparticAcid getInstance() {
        return AsparticAcid.INSTANCE;
    }

    private AsparticAcid() {
        super(Arrays.asList(new AtomName[] { AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.CG, AtomName.OD1, AtomName.OD2 }), "Aspartic acid", 'D', "ASP");
        torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
        torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, Chi1.ASPARTIC_ACID_ATOMS);
        chiAtoms.put(ProteinChiType.CHI2, Chi2.ASPARTIC_ACID_ATOMS);
    }
}
