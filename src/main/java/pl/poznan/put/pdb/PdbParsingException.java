package pl.poznan.put.pdb;

/** When data in PDB files is inconsistent with the documented format. */
public class PdbParsingException extends RuntimeException {
  public PdbParsingException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public PdbParsingException(final String message) {
    super(message);
  }
}
