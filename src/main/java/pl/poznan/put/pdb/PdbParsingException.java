package pl.poznan.put.pdb;

public class PdbParsingException extends Exception {
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
