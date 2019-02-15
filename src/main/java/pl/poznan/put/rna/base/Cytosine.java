package pl.poznan.put.rna.base;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.rna.Pyrimidine;
import pl.poznan.put.rna.Ribose;
import pl.poznan.put.rna.Sugar;

import java.util.Arrays;

public final class Cytosine extends Pyrimidine {
  private static final Cytosine INSTANCE = new Cytosine();

  private Cytosine() {
    super(
        Arrays.asList(
            AtomName.N1,
            AtomName.C6,
            AtomName.H6,
            AtomName.C5,
            AtomName.H5,
            AtomName.C2,
            AtomName.O2,
            AtomName.N3,
            AtomName.C4,
            AtomName.N4,
            AtomName.H41,
            AtomName.H42),
        "Cytosine",
        'C',
        "C",
        "CYT",
        "DC");
  }

  public static Cytosine getInstance() {
    return Cytosine.INSTANCE;
  }

  @Override
  public Sugar getDefaultSugarInstance() {
    return Ribose.getInstance();
  }
}
