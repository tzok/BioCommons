package pl.poznan.put.pdb;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdbModresLine implements ChainNumberICode, Serializable {
  private static final long serialVersionUID = 1679492136825436435L;
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
  private static final String RECORD_NAME = "MODRES";
  private final String idCode;
  private final String residueName;
  private final String chainIdentifier;
  private final int residueNumber;
  private final String insertionCode;
  private final String standardResidueName;
  private final String comment;

  public PdbModresLine(
      final String idCode,
      final String residueName,
      final String chainIdentifier,
      final int residueNumber,
      final String insertionCode,
      final String standardResidueName,
      final String comment) {
    super();
    this.idCode = idCode;
    this.residueName = residueName;
    this.chainIdentifier = chainIdentifier;
    this.residueNumber = residueNumber;
    this.insertionCode = insertionCode;
    this.standardResidueName = standardResidueName;
    this.comment = comment;
  }

  public static PdbModresLine parse(final String line) throws PdbParsingException {
    if (line.length() < 70) {
      throw new PdbParsingException("PDB MODRES line is not at least 70 character long");
    }

    try {
      final String recordName = line.substring(0, 6).trim();

      if (!Objects.equals(PdbModresLine.RECORD_NAME, recordName)) {
        throw new PdbParsingException("PDB line does not start with MODRES");
      }

      final String idCode = line.substring(7, 11).trim();
      final String residueName = line.substring(12, 15).trim();
      final String chainIdentifier = Character.toString(line.charAt(16));
      final int residueNumber = Integer.parseInt(line.substring(18, 22).trim());
      final String insertionCode = Character.toString(line.charAt(22));
      final String standardResidueName = line.substring(24, 27).trim();
      final String comment = line.substring(29, 70);
      return new PdbModresLine(
          idCode,
          residueName,
          chainIdentifier,
          residueNumber,
          insertionCode,
          standardResidueName,
          comment);
    } catch (final NumberFormatException e) {
      throw new PdbParsingException("Failed to parse PDB MODRES line", e);
    }
  }

  public static String getRecordName() {
    return PdbModresLine.RECORD_NAME;
  }

  public final String getIdCode() {
    return idCode;
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

  public final String getStandardResidueName() {
    return standardResidueName;
  }

  public final String getComment() {
    return comment;
  }

  @Override
  public final String toString() {
    if (chainIdentifier.length() != 1) {
      PdbModresLine.LOGGER.error(
          "Field 'chainIdentifier' is longer than 1 char. " + "Only first letter will be taken");
    }
    if (insertionCode.length() != 1) {
      PdbModresLine.LOGGER.error(
          "Field 'insertionCode' is longer than 1 char. Only" + " first letter will be taken");
    }
    return String.format(
        Locale.US,
        PdbModresLine.FORMAT,
        idCode,
        residueName,
        chainIdentifier.charAt(0),
        residueNumber,
        insertionCode.charAt(0),
        standardResidueName,
        comment);
  }
}
