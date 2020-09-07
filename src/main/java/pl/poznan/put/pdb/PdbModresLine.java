package pl.poznan.put.pdb;

import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/** Representation of MODRES line in PDB format. */
@Value.Immutable
public abstract class PdbModresLine implements ChainNumberICode, Serializable {
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

  /**
   * Parse text with MODRES line in PDB format.
   *
   * @param line A text with MODRES line in PDB format.
   * @return An instance of this class with fields set to parsed values.
   */
  public static PdbModresLine parse(final String line) {
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
      return ImmutablePdbModresLine.of(
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

  @Override
  public final String toString() {
    if (chainIdentifier().length() != 1) {
      PdbModresLine.LOGGER.error(
          "Field 'chainIdentifier' is longer than 1 char. Only first letter will be taken");
    }
    if (insertionCode().length() != 1) {
      PdbModresLine.LOGGER.error(
          "Field 'insertionCode' is longer than 1 char. Only first letter will be taken");
    }
    return String.format(
        Locale.US,
        PdbModresLine.FORMAT,
        idCode(),
        residueName(),
        chainIdentifier().charAt(0),
        residueNumber(),
        insertionCode().charAt(0),
        standardResidueName(),
        comment());
  }

  /** @return The value of the {@code idCode} attribute */
  @Value.Parameter(order = 1)
  public abstract String idCode();

  /** @return The value of the {@code residueName} attribute */
  @Value.Parameter(order = 2)
  public abstract String residueName();

  /** @return The value of the {@code chainIdentifier} attribute */
  @Override
  @Value.Parameter(order = 3)
  public abstract String chainIdentifier();

  /** @return The value of the {@code residueNumber} attribute */
  @Override
  @Value.Parameter(order = 4)
  public abstract int residueNumber();

  /** @return The value of the {@code insertionCode} attribute */
  @Override
  @Value.Parameter(order = 5)
  public abstract String insertionCode();

  /** @return The value of the {@code standardResidueName} attribute */
  @Value.Parameter(order = 6)
  public abstract String standardResidueName();

  /** @return The value of the {@code comment} attribute */
  @Value.Parameter(order = 7)
  public abstract String comment();
}
