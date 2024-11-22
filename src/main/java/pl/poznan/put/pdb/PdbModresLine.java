package pl.poznan.put.pdb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A representation of MODRES line in PDB format. */
@Value.Immutable
@JsonSerialize(as = ImmutablePdbModresLine.class)
@JsonDeserialize(as = ImmutablePdbModresLine.class)
public abstract class PdbModresLine implements ChainNumberICode, Serializable {
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
  private static final String FORMAT = "MODRES %4s %3s %c %4d%c %3s  %-41s          ";
  private static final String RECORD_NAME = "MODRES";
  private static final Logger LOGGER = LoggerFactory.getLogger(PdbModresLine.class);

  /**
   * Parses text with MODRES line in PDB format.
   *
   * @param line A text with MODRES line in PDB format.
   * @return An instance of this class with fields set to parsed values.
   */
  public static PdbModresLine parse(final String line) {
    try {
      final String recordName = StringUtils.trimToEmpty(StringUtils.substring(line, 0, 6));
      if (!Objects.equals(PdbModresLine.RECORD_NAME, recordName)) {
        throw new PdbParsingException("PDB line does not start with MODRES");
      }

      final String idCode = StringUtils.trimToEmpty(StringUtils.substring(line, 7, 11));
      final String residueName = StringUtils.trimToEmpty(StringUtils.substring(line, 12, 15));
      final String chainIdentifier = StringUtils.trimToEmpty(StringUtils.substring(line, 16, 17));
      final int residueNumber =
          Integer.parseInt(StringUtils.trimToEmpty(StringUtils.substring(line, 18, 22)));
      final String insertionCode = StringUtils.trimToEmpty(StringUtils.substring(line, 22, 23));
      final String standardResidueName =
          StringUtils.trimToEmpty(StringUtils.substring(line, 24, 27));
      final String comment = StringUtils.trimToEmpty(StringUtils.substring(line, 29, 70));
      return ImmutablePdbModresLine.of(
          idCode,
          residueName,
          chainIdentifier,
          residueNumber,
          "".equals(insertionCode) ? Optional.empty() : Optional.of(insertionCode),
          standardResidueName,
          comment);
    } catch (final NumberFormatException e) {
      throw new PdbParsingException("Failed to parse PDB MODRES line", e);
    }
  }

  /**
   * @return The value of the {@code idCode} attribute
   */
  @Value.Parameter(order = 1)
  public abstract String idCode();

  /**
   * @return The value of the {@code residueName} attribute
   */
  @Value.Parameter(order = 2)
  public abstract String residueName();

  /**
   * @return The value of the {@code chainIdentifier} attribute
   */
  @Override
  @Value.Parameter(order = 3)
  public abstract String chainIdentifier();

  /**
   * @return The value of the {@code residueNumber} attribute
   */
  @Override
  @Value.Parameter(order = 4)
  public abstract int residueNumber();

  /**
   * @return The value of the {@code insertionCode} attribute
   */
  @Override
  @Value.Parameter(order = 5)
  public abstract Optional<String> insertionCode();

  /**
   * @return The value of the {@code standardResidueName} attribute
   */
  @Value.Parameter(order = 6)
  public abstract String standardResidueName();

  /**
   * @return The value of the {@code comment} attribute
   */
  @Value.Parameter(order = 7)
  public abstract String comment();

  @Override
  public final String toString() {
    return toPdb();
  }

  /**
   * @return A line in PDB format.
   */
  public final String toPdb() {
    if (chainIdentifier().length() != 1) {
      PdbModresLine.LOGGER.error(
          "Field 'chainIdentifier' is longer than 1 char. Only first letter will be taken");
    }
    if (insertionCode().orElse(" ").length() != 1) {
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
        insertionCode().orElse(" ").charAt(0),
        standardResidueName(),
        comment());
  }
}
