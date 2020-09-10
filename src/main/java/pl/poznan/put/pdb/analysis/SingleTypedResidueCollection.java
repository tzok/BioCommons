package pl.poznan.put.pdb.analysis;

/** A collection of residues with a common molecule type (RNA or protein). */
@FunctionalInterface
public interface SingleTypedResidueCollection extends ResidueCollection {
  /** @return A detected type of the chain (RNA or protein). */
  default MoleculeType moleculeType() {
    int rnaCounter = 0;
    int proteinCounter = 0;

    for (final PdbResidue residue : residues()) {
      switch (residue.moleculeType()) {
        case PROTEIN:
          proteinCounter += 1;
          break;
        case RNA:
          rnaCounter += 1;
          break;
        case UNKNOWN:
          break;
      }
    }
    return (rnaCounter > proteinCounter) ? MoleculeType.RNA : MoleculeType.PROTEIN;
  }
}
