package pl.poznan.put.circular.exception;

public class InvalidCircularValueException extends RuntimeException {
  private static final long serialVersionUID = 4884607145051888298L;

  public InvalidCircularValueException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidCircularValueException(final String message) {
    super(message);
  }
}
