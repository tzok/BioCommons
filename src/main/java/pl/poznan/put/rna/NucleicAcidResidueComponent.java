package pl.poznan.put.rna;

import pl.poznan.put.pdb.analysis.ResidueComponent;

/** A component of a nucleotide. */
public interface NucleicAcidResidueComponent extends ResidueComponent {
  /**
   * @return The type of this nucleotide component.
   */
  NucleotideComponentType nucleotideComponentType();
}
