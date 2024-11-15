package pl.poznan.put.pdb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import org.immutables.value.Value;

/** A representation of REMARK 2 line which describes experimental resolution. */
@Value.Immutable
@JsonSerialize(as = ImmutablePdbRemark2Line.class)
@JsonDeserialize(as = ImmutablePdbRemark2Line.class)
public abstract class PdbRemark2Line implements Serializable {
  // @formatter:off
  // COLUMNS        DATA TYPE     FIELD          DEFINITION
  // --------------------------------------------------------------------------------
  //  1 -  6        Record name   "REMARK"
  // 10             LString(1)    "2"
  // 12 - 22        LString(11)   "RESOLUTION."
  // 24 - 30        Real(7.2)     resolution     Resolution.
  // 32 - 41        LString(10)   "ANGSTROMS."
  //
  // @formatter:on

  // @formatter:off
  // COLUMNS        DATA TYPE     FIELD          DEFINITION
  // --------------------------------------------------------------------------------
  //  1 -  6        Record name   "REMARK"
  // 10             LString(1)    "2"
  // 12 - 38        LString(28)   "RESOLUTION.  NOT APPLICABLE."
  // @formatter:on

  public static final String PROLOGUE =
      "REMARK   2                                                                      ";
  private static final String FORMAT =
      "REMARK   2 RESOLUTION. %7.2f ANGSTROMS.                                       ";
  private static final String NOT_APPLICABLE =
      "REMARK   2 RESOLUTION. NOT APPLICABLE.                                          ";

  /**
   * Parses the text in REMARK 2 of PDB format.
   *
   * @param line A REMARK 2 line.
   * @return An instance of this class with experimental resolution parsed from {@code line}.
   */
  public static PdbRemark2Line parse(final String line) {
    if (!line.startsWith("REMARK   2 RESOLUTION.")) {
      throw new PdbParsingException("Failed to parse REMARK   2 RESOLUTION. line: " + line);
    }

    try {
      final String resolutionString = line.substring(23).trim();
      double resolution = Double.NaN;

      if (!Objects.equals("NOT APPLICABLE.", resolutionString)) {
        resolution = Double.parseDouble(line.substring(23, 30).trim());
      }

      return ImmutablePdbRemark2Line.of(resolution);
    } catch (final NumberFormatException e) {
      throw new PdbParsingException("Failed to parse REMARK   2 RESOLUTION. line", e);
    }
  }

  /**
   * @return The value of the {@code resolution} attribute
   */
  @Value.Parameter(order = 1)
  public abstract double resolution();

  @Override
  public final String toString() {
    return toPdb();
  }

  /**
   * @return A line in PDB format.
   */
  public final String toPdb() {
    return Double.isNaN(resolution())
        ? PdbRemark2Line.NOT_APPLICABLE
        : String.format(Locale.US, PdbRemark2Line.FORMAT, resolution());
  }
}
