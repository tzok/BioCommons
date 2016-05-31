package pl.poznan.put.pdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class PdbModresLine implements ChainNumberICode {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdbModresLine.class);
    // @formatter:off
    /*
       COLUMNS        DATA TYPE     FIELD       DEFINITION
       --------------------------------------------------------------------------------
        1 -  6        Record name   "MODRES"
        8 - 11        IDcode        idCode      ID code of this entry.
       13 - 15        Residue name  resName     Residue name used in this entry.
       17             Character     chainID     Chain identifier.
       19 - 22        Integer       seqNum      Sequence number.
       23             AChar         iCode       Insertion code.
       25 - 27        Residue name  stdRes      Standard residue name.
       30 - 70        String        comment     Description of the residue modification.
    */
    // @formatter:on
    private static final String FORMAT = "MODRES %4s %3s %c %4d%c %3s  %41s          ";
    private final static String RECORD_NAME = "MODRES";

    public static PdbModresLine parse(String line) throws PdbParsingException {
        if (line.length() < 70) {
            throw new PdbParsingException("PDB MODRES line is not at least 70 character long");
        }

        try {
            String recordName = line.substring(0, 6).trim();

            if (!"MODRES".equals(recordName)) {
                throw new PdbParsingException("PDB line does not start with MODRES");
            }

            String idCode = line.substring(7, 11).trim();
            String residueName = line.substring(12, 15).trim();
            String chainIdentifier = Character.toString(line.charAt(16));
            int residueNumber = Integer.parseInt(line.substring(18, 22).trim());
            String insertionCode = Character.toString(line.charAt(22));
            String standardResidueName = line.substring(24, 27).trim();
            String comment = line.substring(29, 70);
            return new PdbModresLine(idCode, residueName, chainIdentifier, residueNumber, insertionCode, standardResidueName, comment);
        } catch (NumberFormatException e) {
            throw new PdbParsingException("Failed to parse PDB MODRES line", e);
        }
    }

    public static String getRecordName() {
        return PdbModresLine.RECORD_NAME;
    }

    private final String idCode;
    private final String residueName;
    private final String chainIdentifier;
    private final int residueNumber;
    private final String insertionCode;
    private final String standardResidueName;
    private final String comment;

    public PdbModresLine(String idCode, String residueName,
                         String chainIdentifier, int residueNumber, String insertionCode,
                         String standardResidueName, String comment) {
        super();
        this.idCode = idCode;
        this.residueName = residueName;
        this.chainIdentifier = chainIdentifier;
        this.residueNumber = residueNumber;
        this.insertionCode = insertionCode;
        this.standardResidueName = standardResidueName;
        this.comment = comment;
    }

    public String getIdCode() {
        return idCode;
    }

    public String getResidueName() {
        return residueName;
    }

    @Override
    public String getChainIdentifier() {
        return chainIdentifier;
    }

    @Override
    public int getResidueNumber() {
        return residueNumber;
    }

    @Override
    public String getInsertionCode() {
        return insertionCode;
    }

    public String getStandardResidueName() {
        return standardResidueName;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        if (chainIdentifier.length() != 1) {
            PdbModresLine.LOGGER.error("Field 'chainIdentifier' is longer than 1 char. Only first letter will be taken");
        }
        if (insertionCode.length() != 1) {
            PdbModresLine.LOGGER.error("Field 'insertionCode' is longer than 1 char. Only first letter will be taken");
        }
        return String.format(Locale.US, PdbModresLine.FORMAT, idCode, residueName, chainIdentifier.charAt(0), residueNumber, insertionCode.charAt(0), standardResidueName, comment);
    }

    @Override
    public PdbResidueIdentifier getResidueIdentifier() {
        return new PdbResidueIdentifier(chainIdentifier, residueNumber, insertionCode);
    }
}
