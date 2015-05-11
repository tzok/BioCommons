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
import pl.poznan.put.types.Quadruplet;

public class Arginine extends ProteinSidechain {
    private static final Arginine INSTANCE = new Arginine();

    public static Arginine getInstance() {
        return Arginine.INSTANCE;
    }

    private Arginine() {
        super(Arrays.asList(new AtomName[] { AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.CG, AtomName.HG1, AtomName.HG2, AtomName.CD, AtomName.HD1, AtomName.HD2, AtomName.NE, AtomName.HE, AtomName.CZ, AtomName.NH1, AtomName.HH11, AtomName.HH12, AtomName.NH2, AtomName.HH21, AtomName.HH22 }), "Arginine", 'R', "ARG");
        torsionAngleTypes.add(Chi1.getInstance(this));
        torsionAngleTypes.add(Chi2.getInstance(this));
        torsionAngleTypes.add(Chi3.getInstance(this));
        torsionAngleTypes.add(Chi4.getInstance(this));
        torsionAngleTypes.add(Chi5.getInstance(this));
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG));
        chiAtoms.put(ProteinChiType.CHI2, new Quadruplet<AtomName>(AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD));
        chiAtoms.put(ProteinChiType.CHI3, new Quadruplet<AtomName>(AtomName.CB, AtomName.CG, AtomName.CD, AtomName.NE));
        chiAtoms.put(ProteinChiType.CHI4, new Quadruplet<AtomName>(AtomName.CG, AtomName.CD, AtomName.NE, AtomName.CZ));
        chiAtoms.put(ProteinChiType.CHI5, new Quadruplet<AtomName>(AtomName.CD, AtomName.NE, AtomName.CZ, AtomName.NH1));
    }
}
