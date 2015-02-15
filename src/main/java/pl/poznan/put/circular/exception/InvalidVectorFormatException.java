package pl.poznan.put.circular.exception;

public class InvalidVectorFormatException extends Exception {
    public InvalidVectorFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVectorFormatException(String message) {
        super(message);
    }
}
