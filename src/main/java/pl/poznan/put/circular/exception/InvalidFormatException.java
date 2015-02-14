package pl.poznan.put.circular.exception;

public class InvalidFormatException extends Exception {
    public InvalidFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFormatException(String message) {
        super(message);
    }
}
