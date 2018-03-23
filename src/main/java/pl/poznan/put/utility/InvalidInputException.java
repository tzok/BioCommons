package pl.poznan.put.utility;

public class InvalidInputException extends Exception {
  private static final long serialVersionUID = 1873169469355235471L;

  public InvalidInputException(final String message) {
    super(message);
  }

  public InvalidInputException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidInputException(final Throwable cause) {
    super(cause);
  }
}
