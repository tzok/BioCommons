package pl.poznan.put.circular.exception;

public class InvalidCircularOperationException extends RuntimeException {
  public InvalidCircularOperationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidCircularOperationException(final String message) {
    super(message);
  }
}
