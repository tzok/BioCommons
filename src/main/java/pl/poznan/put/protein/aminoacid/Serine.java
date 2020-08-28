package pl.poznan.put.protein.aminoacid;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.ProteinChiType;

import java.util.Arrays;

public final class Serine extends ProteinSidechain {
  private static final Serine INSTANCE = new Serine();

  private Serine() {
    super(
        Arrays.asList(AtomName.CB, AtomName.HB1, AtomName.HB2, AtomName.OG, AtomName.HG1),
        "Serine",
        'S',
        "SER");
    chiAtoms.put(ProteinChiType.CHI1, Chi1.SERINE_ATOMS);
    torsionAngleTypes.add(Chi1.getInstance(getChiAtoms(ProteinChiType.CHI1)));
  }

  public static Serine getInstance() {
    return Serine.INSTANCE;
  }
}
