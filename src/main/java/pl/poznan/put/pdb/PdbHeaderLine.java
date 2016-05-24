package pl.poznan.put.pdb;

import java.util.Locale;

public class PdbHeaderLine {
    // @formatter:off
    /*
     *  COLUMNS       DATA  TYPE     FIELD             DEFINITION
     *  ------------------------------------------------------------------------------------
     *   1 -  6       Record name    "HEADER"
     *  11 - 50       String(40)     classification    Classifies the molecule(s).
     *  51 - 59       Date           depDate           Deposition date. This is the date the
     *                                                 coordinates  were received at the PDB.
     *  63 - 66       IDcode         idCode            This identifier is unique within the PDB.
     */
    // @formatter:on
    private static final String FORMAT = "HEADER    %-40s%9s   %4s              ";
    private final static String RECORD_NAME = "HEADER";
    private final static PdbHeaderLine EMPTY_INSTANCE = new PdbHeaderLine("", "", "");

    public static PdbHeaderLine parse(String line) throws PdbParsingException {
        if (line.length() < 66) {
            throw new PdbParsingException("PDB HEADER line is not at least 66 characters long");
        }

        String recordName = line.substring(0, 6).trim();

        if (!"HEADER".equals(recordName)) {
            throw new PdbParsingException("PDB line does not start with HEADER");
        }

        String classification = line.substring(10, 50).trim();
        String depositionDate = line.substring(50, 59).trim();
        String idCode = line.substring(62, 66).trim();
        return new PdbHeaderLine(classification, depositionDate, idCode);
    }

    public static PdbHeaderLine emptyInstance() {
        return PdbHeaderLine.EMPTY_INSTANCE;
    }

    public static String getRecordName() {
        return PdbHeaderLine.RECORD_NAME;
    }

    private final String classification;
    private final String depositionDate;
    private final String idCode;

    public PdbHeaderLine(String classification, String depositionDate, String idCode) {
        super();
        this.classification = classification;
        this.depositionDate = depositionDate;
        this.idCode = idCode;
    }

    public String getClassification() {
        return classification;
    }

    public String getDepositionDate() {
        return depositionDate;
    }

    public String getIdCode() {
        return idCode;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, PdbHeaderLine.FORMAT, classification, depositionDate, idCode);
    }
}
