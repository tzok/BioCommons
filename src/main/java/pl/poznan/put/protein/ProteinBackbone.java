package pl.poznan.put.protein;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueComponent;

import java.util.Arrays;

public final class ProteinBackbone extends ResidueComponent {
  private static final ProteinBackbone INSTANCE = new ProteinBackbone();

  private ProteinBackbone() {
    super(
        "backbone",
        MoleculeType.PROTEIN,
        Arrays.asList(AtomName.N, AtomName.HN, AtomName.CA, AtomName.HA, AtomName.C, AtomName.O));
  }

  public static ProteinBackbone getInstance() {
    return ProteinBackbone.INSTANCE;
  }
}
