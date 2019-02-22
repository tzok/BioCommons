package pl.poznan.put.rna;

import pl.poznan.put.atom.AtomName;

import java.util.Collections;
import java.util.List;

public abstract class Sugar extends NucleicAcidResidueComponent {
  private static final Sugar INVALID = new Sugar(Collections.emptyList()) {
        // empty block
      };

  protected Sugar(final List<AtomName> atoms) {
    super(RNAResidueComponentType.RIBOSE, atoms);
  }

  public static Sugar invalidInstance() {
    return Sugar.INVALID;
  }
}
