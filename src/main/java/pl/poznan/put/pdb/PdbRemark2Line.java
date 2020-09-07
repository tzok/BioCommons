package pl.poznan.put.pdb;

import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/** Representation of REMARK 2 line which describes experimental resolution. */
@Value.Immutable
public abstract class PdbRemark2Line implements Serializable {
  public static final String PROLOGUE =
      "REMARK   2                                                      " + "                ";
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
  private static final String FORMAT =
      "REMARK   2 RESOLUTION. %7.2f ANGSTROMS.                         " + "              ";

  // @formatter:off
  // COLUMNS        DATA TYPE     FIELD          DEFINITION
  // --------------------------------------------------------------------------------
  //  1 -  6        Record name   "REMARK"
  // 10             LString(1)    "2"
  // 12 - 38        LString(28)   "RESOLUTION.  NOT APPLICABLE."
  // @formatter:on
  private static final String NOT_APPLICABLE =
      "REMARK   2 RESOLUTION. NOT APPLICABLE.                          " + "                ";

  /**
   * Parse the text in REMARK 2 of PDB format.
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

  @Override
  public final String toString() {
    if (Double.isNaN(resolution())) {
      return PdbRemark2Line.NOT_APPLICABLE;
    }

    return String.format(Locale.US, PdbRemark2Line.FORMAT, resolution());
  }

  /** @return The value of the {@code resolution} attribute */
  @Value.Parameter(order = 1)
  public abstract double resolution();
}
