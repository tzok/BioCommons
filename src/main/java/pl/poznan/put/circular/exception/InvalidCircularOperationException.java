package pl.poznan.put.circular.exception;

public class InvalidCircularOperationException extends Exception {
    public InvalidCircularOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCircularOperationException(String message) {
        super(message);
    }
}
