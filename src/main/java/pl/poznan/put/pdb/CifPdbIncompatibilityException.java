package pl.poznan.put.pdb;

/** Exception thrown when it is impossible to convert from mmCIF to PDB. */
public class CifPdbIncompatibilityException extends RuntimeException {
  CifPdbIncompatibilityException(final String message) {
    super(message);
  }
}
