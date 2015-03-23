package pl.poznan.put.structure.secondary.formats;

public class InvalidSecondaryStructureException extends Exception {
    public InvalidSecondaryStructureException() {
        super();
    }

    public InvalidSecondaryStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSecondaryStructureException(String message) {
        super(message);
    }

    public InvalidSecondaryStructureException(Throwable cause) {
        super(cause);
    }
}
