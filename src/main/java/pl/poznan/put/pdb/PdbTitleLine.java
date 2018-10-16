package pl.poznan.put.pdb;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import lombok.Getter;

public final class PdbTitleLine implements Serializable {
  private static final long serialVersionUID = 6843804077423547537L;
  // @formatter:off
  /*
   *  COLUMNS       DATA  TYPE     FIELD             DEFINITION
   *  ------------------------------------------------------------------------------------
   *   1 -  6       Record name    "TITLE "
   *   9 - 10       Continuation   continuation      Allows concatenation of multiple records.
   *  11 - 80       String         title             Title of the  experiment.
   */
  // @formatter:on
  private static final String FORMAT = "TITLE   %2s%70s"; // NON-NLS
  private static final String RECORD_NAME = "TITLE"; // NON-NLS
  private static final PdbTitleLine EMPTY_INSTANCE = new PdbTitleLine("", "");
  @Getter private final String continuation;
  @Getter private final String title;

  private PdbTitleLine(final String continuation, final String title) {
    super();
    this.continuation = continuation;
    this.title = title;
  }

  public static PdbTitleLine parse(final String line) throws PdbParsingException {
    if (line.length() < 80) {
      throw new PdbParsingException(
          String.format(
              "PDB %s line is not shorter than %d characters", PdbTitleLine.RECORD_NAME, 80));
    }

    final String recordName = line.substring(0, 6).trim();

    if (!Objects.equals(PdbTitleLine.RECORD_NAME, recordName)) {
      throw new PdbParsingException(
          String.format("PDB line does not start with %s", PdbTitleLine.RECORD_NAME));
    }

    final String continuation = line.substring(8, 10).trim();
    final String title = line.substring(10, 80).trim();
    return new PdbTitleLine(continuation, title);
  }

  public static PdbTitleLine emptyInstance() {
    return PdbTitleLine.EMPTY_INSTANCE;
  }

  public static String getRecordName() {
    return PdbTitleLine.RECORD_NAME;
  }

  @Override
  public String toString() {
    return String.format(Locale.US, PdbTitleLine.FORMAT, continuation, title);
  }
}
