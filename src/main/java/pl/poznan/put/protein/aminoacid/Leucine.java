package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.ProteinChiType;

public class Leucine extends ProteinSidechain {
    private static final Leucine INSTANCE = new Leucine();

    public static Leucine getInstance() {
        return Leucine.INSTANCE;
    }

    private Leucine() {
        super(Arrays.asList(AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.CG, AtomName.HG, AtomName.CD1, AtomName.HD11, AtomName.HD12, AtomName.HD13, AtomName.CD2, AtomName.HD21, AtomName.HD22, AtomName.HD23), "Leucine", 'L', "LEU");
        torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
        torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, Chi1.LEUCINE_ATOMS);
        chiAtoms.put(ProteinChiType.CHI2, Chi2.LEUCINE_ATOMS);
    }
}
