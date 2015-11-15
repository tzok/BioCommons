package pl.poznan.put.pdb;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class PdbRemark465Line extends PdbRemarkLine implements ChainNumberICode {
    // @formatter:off
    /*
        REMARK 465                                                                       
        REMARK 465 MISSING  RESIDUES                                                     
        REMARK 465 THE FOLLOWING  RESIDUES WERE NOT LOCATED IN THE                       
        REMARK 465 EXPERIMENT.  (M=MODEL NUMBER; RES=RESIDUE NAME; C=CHAIN               
        REMARK 465 IDENTIFIER;  SSSEQ=SEQUENCE NUMBER; I=INSERTION CODE.)                
        REMARK 465                                                                      
        REMARK 465   M RES C SSSEQI                                                     
        REMARK 465     ARG A    46                                                      
        REMARK 465     GLY A    47                                                      
        REMARK 465     ALA A    48                                                      
        REMARK 465     ARG A    49                                                      
        REMARK 465     MET A    50
        
        REMARK 465                                                                      
        REMARK 465 MISSING RESIDUES
        REMARK 465 THE FOLLOWING RESIDUES WERE NOT LOCATED IN THE
        REMARK 465 EXPERIMENT. (RES=RESIDUE NAME; C=CHAIN IDENTIFIER;
        REMARK 465 SSSEQ=SEQUENCE NUMBER; I=INSERTION CODE.)
        REMARK 465   MODELS 1-20
        REMARK 465     RES C SSSEQI
        REMARK 465     MET A     1
        REMARK 465     GLY A     2
     */
    
    private static final String[] COMMENT_LINES = new String[] {
        "REMARK 465",
        "REMARK 465 MISSING RESIDUES",
        "REMARK 465 THE FOLLOWING RESIDUES WERE NOT LOCATED IN THE",
        "REMARK 465 EXPERIMENT. (M=MODEL NUMBER; RES=RESIDUE NAME; C=CHAIN",
        "REMARK 465 IDENTIFIER; SSSEQ=SEQUENCE NUMBER; I=INSERTION CODE.)",
        "REMARK 465 M RES C SSSEQI",
        "REMARK 465 EXPERIMENT. (RES=RESIDUE NAME; C=CHAIN IDENTIFIER;",
        "REMARK 465 SSSEQ=SEQUENCE NUMBER; I=INSERTION CODE.)",
        "REMARK 465 RES C SSSEQI"
    };
    // @formatter:on
    private static final String REMARK_FORMAT = "  %1s %3s %c %5d%c                                                     ";
    private static final String FORMAT = "REMARK 465 " + PdbRemark465Line.REMARK_FORMAT;

    public static boolean isCommentLine(String line) {
        String lineTrimmed = StringUtils.normalizeSpace(line);

        for (String comment : PdbRemark465Line.COMMENT_LINES) {
            if (lineTrimmed.equals(StringUtils.normalizeSpace(comment))) {
                return true;
            }
        }

        return lineTrimmed.startsWith("REMARK 465   MODELS");

    }

    public static PdbRemark465Line parse(String line) throws PdbParsingException {
        PdbRemarkLine remarkLine = PdbRemarkLine.parse(line);
        return PdbRemark465Line.parse(remarkLine);
    }

    public static PdbRemark465Line parse(PdbRemarkLine remarkLine) throws PdbParsingException {
        try {
            String remarkContent = remarkLine.getRemarkContent();
            int modelNumber = remarkContent.charAt(2) == ' ' ? 0 : Integer.parseInt(remarkContent.substring(2, 3));
            String residueName = remarkContent.substring(4, 7).trim();
            char chainIdentifier = remarkContent.charAt(8);
            int residueNumber = Integer.parseInt(remarkContent.substring(10, 15).trim());
            char insertionCode = remarkContent.length() == 15 ? ' ' : remarkContent.charAt(15);
            return new PdbRemark465Line(modelNumber, residueName, chainIdentifier, residueNumber, insertionCode);
        } catch (NumberFormatException e) {
            throw new PdbParsingException("Failed to parse PDB REMARK 465 line", e);
        }
    }

    private final int modelNumber;
    private final String residueName;
    private final char chainIdentifier;
    private final int residueNumber;
    private final char insertionCode;

    public PdbRemark465Line(int modelNumber, String residueName,
            char chainIdentifier, int residueNumber, char insertionCode) {
        super(465, String.format(Locale.US, PdbRemark465Line.FORMAT, modelNumber == 0 ? " " : String.valueOf(modelNumber), residueName, chainIdentifier, residueNumber, insertionCode));
        this.modelNumber = modelNumber;
        this.residueName = residueName;
        this.chainIdentifier = chainIdentifier;
        this.residueNumber = residueNumber;
        this.insertionCode = insertionCode;
    }

    public int getModelNumber() {
        return modelNumber;
    }

    public String getResidueName() {
        return residueName;
    }

    @Override
    public char getChainIdentifier() {
        return chainIdentifier;
    }

    @Override
    public int getResidueNumber() {
        return residueNumber;
    }

    @Override
    public char getInsertionCode() {
        return insertionCode;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, PdbRemark465Line.FORMAT, modelNumber == 0 ? " " : String.valueOf(modelNumber), residueName, chainIdentifier, residueNumber, insertionCode);
    }

    @Override
    public PdbResidueIdentifier getResidueIdentifier() {
        return new PdbResidueIdentifier(chainIdentifier, residueNumber, insertionCode);
    }
}
