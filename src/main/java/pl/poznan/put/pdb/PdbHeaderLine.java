package pl.poznan.put.pdb;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PdbHeaderLine implements Serializable {
  private static final long serialVersionUID = -2116748731208564528L;

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
  private static final PdbHeaderLine EMPTY_INSTANCE = new PdbHeaderLine("", new Date(0L), "");
  private final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.US);
  private final String classification;
  private final Date depositionDate;
  private final String idCode;

  public PdbHeaderLine(
      final String classification, final Date depositionDate, final String idCode) {
    super();
    this.classification = classification;
    this.depositionDate = new Date(depositionDate.getTime());
    this.idCode = idCode;
  }

  public static PdbHeaderLine parse(final String line) {
    if (line.length() < 66) {
      throw new PdbParsingException("PDB HEADER line is not at least 66 characters long");
    }

    final String recordName = line.substring(0, 6).trim();

    if (!Objects.equals(PdbHeaderLine.RECORD_NAME, recordName)) {
      throw new PdbParsingException("PDB line does not start with HEADER");
    }

    final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.US);

    try {
      final String classification = line.substring(10, 50).trim();
      final Date depositionDate = dateFormat.parse(line.substring(50, 59).trim());
      final String idCode = line.substring(62, 66).trim();
      return new PdbHeaderLine(classification, depositionDate, idCode);
    } catch (final ParseException e) {
      throw new PdbParsingException("Failed to parse date in line: " + line, e);
    }
  }

  public static PdbHeaderLine emptyInstance() {
    return PdbHeaderLine.EMPTY_INSTANCE;
  }

  public static String getRecordName() {
    return PdbHeaderLine.RECORD_NAME;
  }

  public final String getClassification() {
    return classification;
  }

  public final Date getDepositionDate() {
    return (Date) depositionDate.clone();
  }

  public final String getIdCode() {
    return idCode;
  }

  @Override
  public final String toString() {
    return String.format(
        Locale.US,
        PdbHeaderLine.FORMAT,
        classification,
        dateFormat.format(depositionDate).toUpperCase(Locale.US),
        idCode);
  }
}
