package pl.poznan.put.protein.aminoacid;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.ProteinChiType;

import java.util.Arrays;

public final class Threonine extends ProteinSidechain {
  private static final Threonine INSTANCE = new Threonine();

  private Threonine() {
    super(
        Arrays.asList(
            AtomName.CB,
            AtomName.HB,
            AtomName.OG1,
            AtomName.HG1,
            AtomName.CG2,
            AtomName.HG21,
            AtomName.HG22,
            AtomName.HG23),
        "Threonine",
        'T',
        "THR");
    torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
  }

  public static Threonine getInstance() {
    return Threonine.INSTANCE;
  }

  @Override
  protected void fillChiAtomsMap() {
    chiAtoms.put(ProteinChiType.CHI1, Chi1.THREONINE_ATOMS);
  }
}
