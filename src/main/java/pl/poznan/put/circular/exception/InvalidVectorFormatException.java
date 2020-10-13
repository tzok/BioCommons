package pl.poznan.put.circular.exception;

public class InvalidVectorFormatException extends RuntimeException {
  public InvalidVectorFormatException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidVectorFormatException(final String message) {
    super(message);
  }
}
