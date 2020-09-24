package pl.poznan.put.rna;

import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;

/** A nucleobase (adenine, cytosine, guanine, uracil or thymine). */
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
