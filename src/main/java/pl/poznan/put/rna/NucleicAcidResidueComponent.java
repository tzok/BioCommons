package pl.poznan.put.rna;

import pl.poznan.put.pdb.analysis.ResidueComponent;

public interface NucleicAcidResidueComponent extends ResidueComponent {
  NucleotideComponentType nucleotideComponentType();
}
