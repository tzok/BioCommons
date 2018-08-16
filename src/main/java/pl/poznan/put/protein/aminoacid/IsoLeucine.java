package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.ProteinChiType;

public final class IsoLeucine extends ProteinSidechain {
  private static final IsoLeucine INSTANCE = new IsoLeucine();

  private IsoLeucine() {
    super(
        Arrays.asList(
            AtomName.CB,
            AtomName.HB,
            AtomName.CG1,
            AtomName.HG11,
            AtomName.HG12,
            AtomName.CG2,
            AtomName.HG21,
            AtomName.HG22,
            AtomName.HG23,
            AtomName.CD1,
            AtomName.HD11,
            AtomName.HD12,
            AtomName.HD13),
        "Isoleucine",
        'I',
        "ILE");
    torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
    torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
  }

  public static IsoLeucine getInstance() {
    return IsoLeucine.INSTANCE;
  }

  @Override
  protected void fillChiAtomsMap() {
    chiAtoms.put(ProteinChiType.CHI1, Chi1.ISOLEUCINE_ATOMS);
    chiAtoms.put(ProteinChiType.CHI2, Chi2.ISOLEUCINE_ATOMS);
  }
}
