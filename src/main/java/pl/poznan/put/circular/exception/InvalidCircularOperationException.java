package pl.poznan.put.circular.exception;

public class InvalidCircularOperationException extends RuntimeException {
    private static final long serialVersionUID = 4906831752987274440L;

    public InvalidCircularOperationException(
            final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidCircularOperationException(final String message) {
        super(message);
    }
}
