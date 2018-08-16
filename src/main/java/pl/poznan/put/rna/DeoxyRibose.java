package pl.poznan.put.rna;

import java.util.Arrays;
import pl.poznan.put.atom.AtomName;

public final class DeoxyRibose extends Sugar {
  private static final DeoxyRibose INSTANCE = new DeoxyRibose();

  private DeoxyRibose() {
    super(
        Arrays.asList(
            AtomName.C5p,
            AtomName.H5p,
            AtomName.H5pp,
            AtomName.C4p,
            AtomName.H4p,
            AtomName.O4p,
            AtomName.C3p,
            AtomName.H3p,
            AtomName.C2p,
            AtomName.H2p,
            AtomName.H2pp,
            AtomName.C1p,
            AtomName.H1p));
  }

  public static DeoxyRibose getInstance() {
    return DeoxyRibose.INSTANCE;
  }
}
