package pl.poznan.put.protein.aminoacid;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.Chi3;
import pl.poznan.put.protein.torsion.ProteinChiType;

import java.util.Arrays;

public final class GlutamicAcid extends ProteinSidechain {
  private static final GlutamicAcid INSTANCE = new GlutamicAcid();

  private GlutamicAcid() {
    super(
        Arrays.asList(
            AtomName.CB,
            AtomName.HB1,
            AtomName.HB2,
            AtomName.CG,
            AtomName.HG1,
            AtomName.HG2,
            AtomName.CD,
            AtomName.OE1,
            AtomName.OE2),
        "Glutamic acid",
        'E',
        "GLU");
    torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
    torsionAngleTypes.add(Chi2.getInstance(getChiAtoms(ProteinChiType.CHI2)));
    torsionAngleTypes.add(Chi3.getInstance(getChiAtoms(ProteinChiType.CHI3)));
  }

  public static GlutamicAcid getInstance() {
    return GlutamicAcid.INSTANCE;
  }

  @Override
  protected void fillChiAtomsMap() {
    chiAtoms.put(ProteinChiType.CHI1, Chi1.GLUTAMIC_ACID_ATOMS);
    chiAtoms.put(ProteinChiType.CHI2, Chi2.GLUTAMIC_ACID_ATOMS);
    chiAtoms.put(ProteinChiType.CHI3, Chi3.GLUTAMIC_ACID_ATOMS);
  }
}
