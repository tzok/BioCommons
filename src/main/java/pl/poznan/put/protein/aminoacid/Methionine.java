package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.Chi3;
import pl.poznan.put.protein.torsion.ProteinChiType;

public final class Methionine extends ProteinSidechain {
  private static final Methionine INSTANCE = new Methionine();

  private Methionine() {
    super(
        Arrays.asList(
            AtomName.CB,
            AtomName.HB1,
            AtomName.HB2,
            AtomName.CG,
            AtomName.HG1,
            AtomName.HG2,
            AtomName.SD,
            AtomName.CE,
            AtomName.HE1,
            AtomName.HE2,
            AtomName.HE3),
        "Methionine",
        'M',
        "MET");
    torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
    torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
    torsionAngleTypes.add(Chi3.getInstance(getChiAtoms(ProteinChiType.CHI3)));
  }

  public static Methionine getInstance() {
    return Methionine.INSTANCE;
  }

  @Override
  protected void fillChiAtomsMap() {
    chiAtoms.put(ProteinChiType.CHI1, Chi1.METHIONINE_ATOMS);
    chiAtoms.put(ProteinChiType.CHI2, Chi2.METHIONINE_ATOMS);
    chiAtoms.put(ProteinChiType.CHI3, Chi3.METHIONINE_ATOMS);
  }
}
