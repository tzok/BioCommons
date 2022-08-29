package pl.poznan.put.pdb;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import org.immutables.value.Value;

/** A representation of TITLE line in PDB format. */
@Value.Immutable
public abstract class PdbTitleLine implements Serializable {
  // @formatter:off
  /*
   *  COLUMNS       DATA  TYPE     FIELD             DEFINITION
   *  ------------------------------------------------------------------------------------
   *   1 -  6       Record name    "TITLE "
   *   9 - 10       Continuation   continuation      Allows concatenation of multiple records.
   *  11 - 80       String         title             Title of the experiment.
   */
  // @formatter:on

  private static final String FORMAT = "TITLE   %2s%70s"; // NON-NLS
  private static final String RECORD_NAME = "TITLE"; // NON-NLS

  /**
   * Parses a line of text as TITLE in PDB format.
   *
   * @param line A TITLE line.
   * @return An instance of this class.
   */
  public static PdbTitleLine parse(final String line) {
    final String recordName = line.substring(0, 6).trim();

    if (!Objects.equals(PdbTitleLine.RECORD_NAME, recordName)) {
      throw new PdbParsingException(
          String.format("PDB line does not start with %s", PdbTitleLine.RECORD_NAME));
    }

    final String continuation = line.substring(8, 10).trim();
    final String title = line.substring(10, 80).trim();
    return ImmutablePdbTitleLine.of(continuation, title);
  }

  /**
   * @return The value of the {@code continuation} attribute
   */
  @Value.Parameter(order = 1)
  public abstract String continuation();

  /**
   * @return The value of the {@code title} attribute
   */
  @Value.Parameter(order = 2)
  public abstract String title();

  @Override
  public final String toString() {
    return toPdb();
  }

  /**
   * @return A line in PDB format.
   */
  public final String toPdb() {
    return String.format(Locale.US, PdbTitleLine.FORMAT, continuation(), title());
  }
}
