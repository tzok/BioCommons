package pl.poznan.put.rna;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueComponent;

import java.util.List;
import java.util.Locale;

public abstract class NucleicAcidResidueComponent extends ResidueComponent {
  private final RNAResidueComponentType type;

  NucleicAcidResidueComponent(
      final RNAResidueComponentType type,
      final List<AtomName> atoms,
      final List<AtomName> additionalAtoms) {
    super(type.name().toLowerCase(Locale.ENGLISH), MoleculeType.RNA, atoms, additionalAtoms);
    this.type = type;
  }

  NucleicAcidResidueComponent(final RNAResidueComponentType type, final List<AtomName> atoms) {
    super(type.name().toLowerCase(Locale.ENGLISH), MoleculeType.RNA, atoms);
    this.type = type;
  }

  public final RNAResidueComponentType getType() {
    return type;
  }
}
