package pl.poznan.put.circular.exception;

public class InvalidVectorFormatException extends RuntimeException {
  private static final long serialVersionUID = -5702461311528191559L;

  public InvalidVectorFormatException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidVectorFormatException(final String message) {
    super(message);
  }
}
