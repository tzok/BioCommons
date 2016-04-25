package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.ProteinChiType;

public class Cysteine extends ProteinSidechain {
    private static final Cysteine INSTANCE = new Cysteine();

    public static Cysteine getInstance() {
        return Cysteine.INSTANCE;
    }

    private Cysteine() {
        super(Arrays.asList(AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.SG, AtomName.HG1), "Cysteine", 'C', "CYS");
        torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, Chi1.CYSTEINE_ATOMS);
    }
}
