package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.ProteinChiType;
import pl.poznan.put.types.Quadruplet;

public class Cysteine extends ProteinSidechain {
    private static final Cysteine INSTANCE = new Cysteine();

    public static Cysteine getInstance() {
        return Cysteine.INSTANCE;
    }

    private Cysteine() {
        super(Arrays.asList(new AtomName[] { AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.SG, AtomName.HG1 }), "Cysteine", 'C', "CYS");
        torsionAngleTypes.add(Chi1.getInstance(this));
    }

    @Override
    protected void fillChiAtomsMap() {
        chiAtoms.put(ProteinChiType.CHI1, new Quadruplet<AtomName>(AtomName.N, AtomName.CA, AtomName.CB, AtomName.SG));
    }
}
