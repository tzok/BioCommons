package pl.poznan.put.circular.exception;

public class InvalidCircularValueException extends RuntimeException {
    public InvalidCircularValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCircularValueException(String message) {
        super(message);
    }
}
