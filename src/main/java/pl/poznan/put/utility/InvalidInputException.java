package pl.poznan.put.utility;

public class InvalidInputException extends Exception {
    private static final long serialVersionUID = 1873169469355235471L;

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }
}
