package pl.poznan.put.protein.aminoacid;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.ProteinChiType;

import java.util.Arrays;

public final class Tyrosine extends ProteinSidechain {
  private static final Tyrosine INSTANCE = new Tyrosine();

  private Tyrosine() {
    super(
        Arrays.asList(
            AtomName.CB,
            AtomName.HB1,
            AtomName.HB2,
            AtomName.CG,
            AtomName.CD1,
            AtomName.HD1,
            AtomName.CE1,
            AtomName.HE1,
            AtomName.CZ,
            AtomName.OH,
            AtomName.HH,
            AtomName.CD2,
            AtomName.HD2,
            AtomName.CE2,
            AtomName.HE2),
        "Tyrosine",
        'Y',
        "TYR");
    chiAtoms.put(ProteinChiType.CHI1, Chi1.TYROSINE_ATOMS);
    chiAtoms.put(ProteinChiType.CHI2, Chi2.TYROSINE_ATOMS);
    torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
    torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
  }

  public static Tyrosine getInstance() {
    return Tyrosine.INSTANCE;
  }
}
