package pl.poznan.put.rna;

interface Sugar extends NucleicAcidResidueComponent {
  @Override
  default NucleotideComponentType nucleotideComponentType() {
    return NucleotideComponentType.RIBOSE;
  }
}
