package pl.poznan.put.circular.exception;

public class InvalidCircularValueException extends RuntimeException {
  public InvalidCircularValueException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidCircularValueException(final String message) {
    super(message);
  }
}
