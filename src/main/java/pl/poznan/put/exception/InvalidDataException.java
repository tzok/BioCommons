package pl.poznan.put.exception;

public class InvalidDataException extends Exception {
    private static final long serialVersionUID = -5350947907099114373L;

    public InvalidDataException() {
        super();
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDataException(String message) {
        super(message);
    }
}
