package pl.poznan.put.rna;

public interface Sugar extends NucleicAcidResidueComponent {
  @Override
  default NucleotideComponentType nucleotideComponentType() {
    return NucleotideComponentType.RIBOSE;
  }
}
