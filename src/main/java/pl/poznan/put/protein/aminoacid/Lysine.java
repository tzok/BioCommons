package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.Chi3;
import pl.poznan.put.protein.torsion.Chi4;
import pl.poznan.put.protein.torsion.ProteinChiType;

public class Lysine extends ProteinSidechain {
    private static final Lysine INSTANCE = new Lysine();

    public static Lysine getInstance() {
        return Lysine.INSTANCE;
    }

    private Lysine() {
        super(Arrays.asList(AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.CG, AtomName.HG1, AtomName.HG2, AtomName.CD, AtomName.HD1, AtomName.HD2, AtomName.CE, AtomName.HE1, AtomName.HE2, AtomName.NZ, AtomName.HZ1, AtomName.HZ2, AtomName.HZ3), "Lysine", 'K', "LYS");
        torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
        torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
        torsionAngleTypes.add(Chi3.getInstance(getChiAtoms(ProteinChiType.CHI3)));
        torsionAngleTypes.add(Chi4.getInstance(getChiAtoms(ProteinChiType.CHI4)));
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, Chi1.LYSINE_ATOMS);
        chiAtoms.put(ProteinChiType.CHI2, Chi2.LYSINE_ATOMS);
        chiAtoms.put(ProteinChiType.CHI3, Chi3.LYSINE_ATOMS);
        chiAtoms.put(ProteinChiType.CHI4, Chi4.LYSINE_ATOMS);
    }
}
