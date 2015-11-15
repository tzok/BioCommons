package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.ProteinChiType;

public class Serine extends ProteinSidechain {
    private static final Serine INSTANCE = new Serine();

    public static Serine getInstance() {
        return Serine.INSTANCE;
    }

    private Serine() {
        super(Arrays.asList(AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.OG, AtomName.HG1), "Serine", 'S', "SER");
        torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, Chi1.SERINE_ATOMS);
    }
}
