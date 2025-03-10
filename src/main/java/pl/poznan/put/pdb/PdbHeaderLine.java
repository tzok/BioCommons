package pl.poznan.put.pdb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

/** A representation of HEADER file in PDB format. */
@Value.Immutable
@JsonSerialize(as = ImmutablePdbHeaderLine.class)
@JsonDeserialize(as = ImmutablePdbHeaderLine.class)
public abstract class PdbHeaderLine implements Serializable {
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
  private static final String FORMAT = "HEADER    %-40s%9s   %4s              "; // NON-NLS
  private static final String RECORD_NAME = "HEADER"; // NON-NLS
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yy", Locale.US);

  /**
   * Parses text with HEADER line in PDB format.
   *
   * @param line Text with HEADER line in PDB format.
   * @return An instance of this class with fields set to values parsed from the {@code line}.
   */
  public static PdbHeaderLine parse(final String line) {
    final String recordName = StringUtils.trimToEmpty(StringUtils.substring(line, 0, 6));
    if (!Objects.equals(PdbHeaderLine.RECORD_NAME, recordName)) {
      throw new PdbParsingException("PDB line does not start with HEADER");
    }

    try {
      final String classification = StringUtils.trimToEmpty(StringUtils.substring(line, 10, 50));
      final Date depositionDate =
          PdbHeaderLine.DATE_FORMAT.parse(
              StringUtils.trimToEmpty(StringUtils.substring(line, 50, 59)));
      final String idCode = StringUtils.trimToEmpty(StringUtils.substring(line, 62, 66));
      return ImmutablePdbHeaderLine.of(classification, depositionDate, idCode);
    } catch (final ParseException e) {
      throw new PdbParsingException("Failed to parse date in line: " + line, e);
    }
  }

  /**
   * @return The value of the {@code classification} attribute
   */
  @Value.Parameter(order = 1)
  public abstract String classification();

  /**
   * @return The value of the {@code depositionDate} attribute
   */
  @Value.Parameter(order = 2)
  public abstract Date depositionDate();

  /**
   * @return The value of the {@code idCode} attribute
   */
  @Value.Parameter(order = 3)
  public abstract String idCode();

  @Override
  public final String toString() {
    return toPdb();
  }

  /**
   * @return A line in PDB format.
   */
  public final String toPdb() {
    return String.format(
        Locale.US,
        PdbHeaderLine.FORMAT,
        classification(),
        PdbHeaderLine.DATE_FORMAT.format(depositionDate()).toUpperCase(Locale.US),
        idCode());
  }
}
