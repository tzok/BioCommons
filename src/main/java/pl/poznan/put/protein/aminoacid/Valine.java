package pl.poznan.put.protein.aminoacid;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.ProteinChiType;

import java.util.Arrays;

public final class Valine extends ProteinSidechain {
  private static final Valine INSTANCE = new Valine();

  private Valine() {
    super(
        Arrays.asList(
            AtomName.CB,
            AtomName.HB,
            AtomName.CG1,
            AtomName.HG11,
            AtomName.HG12,
            AtomName.HG13,
            AtomName.CG2,
            AtomName.HG21,
            AtomName.HG22,
            AtomName.HG23),
        "Valine",
        'V',
        "VAL");
    torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
  }

  public static Valine getInstance() {
    return Valine.INSTANCE;
  }

  @Override
  protected void fillChiAtomsMap() {
    chiAtoms.put(ProteinChiType.CHI1, Chi1.VALINE_ATOMS);
  }
}
