package pl.poznan.put.protein.aminoacid;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.ProteinChiType;

import java.util.Arrays;

public class Cysteine extends ProteinSidechain {
    private static final Cysteine INSTANCE = new Cysteine();

    private Cysteine() {
        super(Arrays.asList(AtomName.CB, AtomName.HB1, AtomName.HB2,
                            AtomName.SG, AtomName.HG1), "Cysteine", 'C', "CYS");
        torsionAngleTypes
                .add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
    }

    public static Cysteine getInstance() {
        return Cysteine.INSTANCE;
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, Chi1.CYSTEINE_ATOMS);
    }
}
