package pl.poznan.put.pdb;

public class PdbParsingException extends Exception {
    public PdbParsingException() {
        super();
    }

    public PdbParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdbParsingException(String message) {
        super(message);
    }

    public PdbParsingException(Throwable cause) {
        super(cause);
    }
}
