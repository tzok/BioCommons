package pl.poznan.put.structure.secondary.formats;

public class InvalidStructureException extends Exception {
    public InvalidStructureException() {
        super();
    }

    public InvalidStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStructureException(String message) {
        super(message);
    }

    public InvalidStructureException(Throwable cause) {
        super(cause);
    }
}
