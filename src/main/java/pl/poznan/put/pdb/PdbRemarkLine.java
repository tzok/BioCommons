package pl.poznan.put.pdb;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class PdbRemarkLine {
    // @formatter:off
    /*
        COLUMNS       DATA TYPE     FIELD         DEFINITION
        --------------------------------------------------------------------------------------
         1 -  6       Record name   "REMARK"
         8 - 10       Integer       remarkNum     Remark  number. It is not an error for
                                                  remark n to exist in an entry when
                                                  remark n-1 does not.
        12 - 79       LString       empty         Left  as white space in first line
                                                  of each  new remark.
     */
    // @formatter:on
    private static final String FORMAT = "REMARK %3d %-68s ";
    private static final String RECORD_NAME = "REMARK";

    public static PdbRemarkLine parse(String line) throws PdbParsingException {
        if (line.length() < 79) {
            throw new PdbParsingException("PDB REMARK line is not at least 79 character long");
        }

        try {
            String recordName = line.substring(0, 6).trim();

            if (!"REMARK".equals(recordName)) {
                throw new PdbParsingException("PDB line does not start with REMARK");
            }

            int remarkNumber = Integer.parseInt(line.substring(7, 10).trim());
            String remarkContent = StringUtils.stripEnd(line.substring(11, 79), null);
            return new PdbRemarkLine(remarkNumber, remarkContent);
        } catch (NumberFormatException e) {
            throw new PdbParsingException("Failed to parse PDB REMARK line", e);
        }
    }

    public static String getRecordName() {
        return PdbRemarkLine.RECORD_NAME;
    }

    private final int remarkNumber;
    private final String remarkContent;

    public PdbRemarkLine(int remarkNumber, String remarkContent) {
        super();
        this.remarkNumber = remarkNumber;
        this.remarkContent = remarkContent;
    }

    public int getRemarkNumber() {
        return remarkNumber;
    }

    public String getRemarkContent() {
        return remarkContent;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, PdbRemarkLine.FORMAT, remarkNumber, remarkContent);
    }
}
