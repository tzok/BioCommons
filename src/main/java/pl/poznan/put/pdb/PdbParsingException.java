package pl.poznan.put.pdb;

/** Exception thrown when data in PDB files is inconsistent with the documented format. */
public class PdbParsingException extends RuntimeException {
  public PdbParsingException() {
    super();
  }

  public PdbParsingException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public PdbParsingException(final String message) {
    super(message);
  }

  public PdbParsingException(final Throwable cause) {
    super(cause);
  }
}
