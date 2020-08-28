package pl.poznan.put.pdb;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class PdbRemark465Line implements ChainNumberICode, Serializable {
  // @formatter:off
  public static final String PROLOGUE =
      "REMARK 465                                                                      \n"
          + "REMARK 465 MISSING RESIDUES                                                     \n"
          + "REMARK 465 THE FOLLOWING RESIDUES WERE NOT LOCATED IN THE                       \n"
          + "REMARK 465 EXPERIMENT. (M=MODEL NUMBER; RES=RESIDUE NAME; C=CHAIN               \n"
          + "REMARK 465 IDENTIFIER; SSSEQ=SEQUENCE NUMBER; I=INSERTION CODE.)                \n"
          + "REMARK 465                                                                      \n"
          + "REMARK 465   M RES C SSSEQI                                                     ";
  private static final long serialVersionUID = -6551046160002695423L;
  private static final Logger LOGGER = LoggerFactory.getLogger(PdbRemark465Line.class);
  // @formatter:on
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
  private static final String[] COMMENT_LINES = {
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
  private static final String REMARK_FORMAT =
      "  %1s %3s %c %5d%c                                              " + "       ";
  private static final String FORMAT = "REMARK 465 " + PdbRemark465Line.REMARK_FORMAT;
  private final int modelNumber;
  private final String residueName;
  private final String chainIdentifier;
  private final int residueNumber;
  private final String insertionCode;

  public PdbRemark465Line(
      final int modelNumber,
      final String residueName,
      final String chainIdentifier,
      final int residueNumber,
      final String insertionCode) {
    super();
    this.modelNumber = modelNumber;
    this.residueName = residueName;
    this.chainIdentifier = chainIdentifier;
    this.residueNumber = residueNumber;
    this.insertionCode = insertionCode;
  }

  public static boolean isCommentLine(final String line) {
    final String lineTrimmed = StringUtils.normalizeSpace(line);

    return Arrays.stream(PdbRemark465Line.COMMENT_LINES)
            .anyMatch(comment -> Objects.equals(lineTrimmed, StringUtils.normalizeSpace(comment)))
        || lineTrimmed.startsWith("REMARK 465   MODELS");
  }

  public static PdbRemark465Line parse(final String line) {
    if (line.length() < 79) {
      throw new PdbParsingException("PDB REMARK line is not at least 79 character long");
    }

    try {
      final String recordName = line.substring(0, 6).trim();

      if (!Objects.equals("REMARK", recordName)) {
        throw new PdbParsingException("PDB line does not start with REMARK");
      }
      final int remarkNumber = Integer.parseInt(line.substring(7, 10).trim());
      if (remarkNumber != 465) {
        throw new PdbParsingException("Unsupported REMARK line occurred");
      }

      final String remarkContent = StringUtils.stripEnd(line.substring(11, 79), null);
      final int modelNumber =
          (remarkContent.charAt(2) == ' ') ? 0 : Integer.parseInt(remarkContent.substring(2, 3));
      final String residueName = remarkContent.substring(4, 7).trim();
      final String chainIdentifier = Character.toString(remarkContent.charAt(8));
      final int residueNumber = Integer.parseInt(remarkContent.substring(10, 15).trim());
      final String insertionCode =
          (remarkContent.length() == 15) ? " " : Character.toString(remarkContent.charAt(15));
      return new PdbRemark465Line(
          modelNumber, residueName, chainIdentifier, residueNumber, insertionCode);
    } catch (final NumberFormatException e) {
      throw new PdbParsingException("Failed to parse PDB REMARK 465 line", e);
    }
  }

  public final int getModelNumber() {
    return modelNumber;
  }

  public final String getResidueName() {
    return residueName;
  }

  @Override
  public final String getChainIdentifier() {
    return chainIdentifier;
  }

  @Override
  public final int getResidueNumber() {
    return residueNumber;
  }

  @Override
  public final String getInsertionCode() {
    return insertionCode;
  }

  @Override
  public final PdbResidueIdentifier getResidueIdentifier() {
    return new PdbResidueIdentifier(chainIdentifier, residueNumber, insertionCode);
  }

  @Override
  public final String toString() {
    if (chainIdentifier.length() != 1) {
      PdbRemark465Line.LOGGER.error(
          "Field 'chainIdentifier' is longer than 1 char. " + "Only first letter will be taken");
    }
    if (insertionCode.length() != 1) {
      PdbRemark465Line.LOGGER.error(
          "Field 'insertionCode' is longer than 1 char. Only" + " first letter will be taken");
    }

    final String modelNumberString = (modelNumber == 0) ? " " : String.valueOf(modelNumber);
    final char chain = chainIdentifier.charAt(0);
    final char icode = insertionCode.charAt(0);

    return String.format(
        Locale.US,
        PdbRemark465Line.FORMAT,
        modelNumberString,
        residueName,
        chain,
        residueNumber,
        icode);
  }

  public final PdbRemark465Line replaceChainIdentifier(final String chainIdentifierNew) {
    return new PdbRemark465Line(
        modelNumber, residueName, chainIdentifierNew, residueNumber, insertionCode);
  }
}
