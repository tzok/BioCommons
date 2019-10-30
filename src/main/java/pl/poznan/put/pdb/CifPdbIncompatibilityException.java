package pl.poznan.put.pdb;

class CifPdbIncompatibilityException extends RuntimeException {
  CifPdbIncompatibilityException(final String message) {
    super(message);
  }
}
