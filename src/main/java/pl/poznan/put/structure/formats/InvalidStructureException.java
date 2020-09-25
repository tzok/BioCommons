package pl.poznan.put.structure.formats;

public class InvalidStructureException extends RuntimeException {
  public InvalidStructureException() {
    super();
  }

  public InvalidStructureException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidStructureException(final String message) {
    super(message);
  }

  public InvalidStructureException(final Throwable cause) {
    super(cause);
  }
}
