package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.Chi3;
import pl.poznan.put.protein.torsion.Chi4;
import pl.poznan.put.protein.torsion.Chi5;
import pl.poznan.put.protein.torsion.ProteinChiType;

public class Arginine extends ProteinSidechain {
    private static final Arginine INSTANCE = new Arginine();

    public static Arginine getInstance() {
        return Arginine.INSTANCE;
    }

    private Arginine() {
        super(Arrays.asList(new AtomName[] { AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.CG, AtomName.HG1, AtomName.HG2, AtomName.CD, AtomName.HD1, AtomName.HD2, AtomName.NE, AtomName.HE, AtomName.CZ, AtomName.NH1, AtomName.HH11, AtomName.HH12, AtomName.NH2, AtomName.HH21, AtomName.HH22 }), "Arginine", 'R', "ARG");
        torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
        torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
        torsionAngleTypes.add(Chi3.getInstance(getChiAtoms(ProteinChiType.CHI3)));
        torsionAngleTypes.add(Chi4.getInstance(getChiAtoms(ProteinChiType.CHI4)));
        torsionAngleTypes.add(Chi5.getInstance(getChiAtoms(ProteinChiType.CHI5)));
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, Chi1.ARGININE_ATOMS);
        chiAtoms.put(ProteinChiType.CHI2, Chi2.ARGININE_ATOMS);
        chiAtoms.put(ProteinChiType.CHI3, Chi3.ARGININE_ATOMS);
        chiAtoms.put(ProteinChiType.CHI4, Chi4.ARGININE_ATOMS);
        chiAtoms.put(ProteinChiType.CHI5, Chi5.ARGININE_ATOMS);
    }
}
