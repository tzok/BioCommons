package pl.poznan.put.rna;

import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;

public interface Nucleobase extends NucleicAcidResidueComponent, ResidueInformationProvider {
  @Override
  default MoleculeType moleculeType() {
    return MoleculeType.RNA;
  }

  @Override
  default NucleotideComponentType nucleotideComponentType() {
    return NucleotideComponentType.BASE;
  }
}
