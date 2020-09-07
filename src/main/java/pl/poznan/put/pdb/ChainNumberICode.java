package pl.poznan.put.pdb;

/** Methods that allow to address a residue by its chain name, residue number and insertion code. */
public interface ChainNumberICode {
  /**
   * Create an instance of {@link PdbResidueIdentifier} that holds the info gathered by methods of
   * this interface.
   *
   * @return An object that can be used to address specific residue.
   */
  default PdbResidueIdentifier toResidueIdentifer() {
    return ImmutablePdbResidueIdentifier.of(chainIdentifier(), residueNumber(), insertionCode());
  }

  /** @return The identifier of the chain a residue belongs to. */
  String chainIdentifier();

  /** @return The number of a residue in the chain. */
  int residueNumber();

  /**
   * @return Optional insertion code, used in some PDB and mmCIF files to represent "inserted"
   *     residues while maintaining the original numbering.
   */
  String insertionCode();
}
