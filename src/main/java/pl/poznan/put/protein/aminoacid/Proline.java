package pl.poznan.put.protein.aminoacid;

import java.util.Arrays;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.ProteinChiType;

public final class Proline extends ProteinSidechain {
  private static final Proline INSTANCE = new Proline();

  private Proline() {
    super(
        Arrays.asList(
            AtomName.CB,
            AtomName.HB1,
            AtomName.HB2,
            AtomName.CD,
            AtomName.HD1,
            AtomName.HD2,
            AtomName.CG,
            AtomName.HG1,
            AtomName.HG2),
        "Proline",
        'P',
        "PRO");
    torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
    torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
  }

  public static Proline getInstance() {
    return Proline.INSTANCE;
  }

  @Override
  protected void fillChiAtomsMap() {
    chiAtoms.put(ProteinChiType.CHI1, Chi1.PROLINE_ATOMS);
    chiAtoms.put(ProteinChiType.CHI2, Chi2.PROLINE_ATOMS);
  }
}
