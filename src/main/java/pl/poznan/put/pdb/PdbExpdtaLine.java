package pl.poznan.put.pdb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A representation of EXPDTA line in PDB files. */
@Value.Immutable
@JsonSerialize(as = ImmutablePdbExpdtaLine.class)
@JsonDeserialize(as = ImmutablePdbExpdtaLine.class)
public abstract class PdbExpdtaLine implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(PdbExpdtaLine.class);

  // @formatter:off
  //  COLUMNS       DATA TYPE      FIELD         DEFINITION
  //  ------------------------------------------------------------------------------------
  //  1 -  6       Record name    "EXPDTA"
  //  9 - 10       Continuation   continuation  Allows concatenation of multiple records.
  // 11 - 79       SList          technique     The experimental technique(s) with
  //                                            optional comment describing the
  //                                            sample or experiment.
  // @formatter:on
  private static final String FORMAT = "EXPDTA    %-70s"; // NON-NLS
  private static final String RECORD_NAME = "EXPDTA";

  /**
   * Parses text as an EXPDTA line in PDB format.
   *
   * @param line EXPDTA line in PDB format.
   * @return An instance of this class.
   */
  public static PdbExpdtaLine parse(final String line) {
    final String recordName = StringUtils.trimToEmpty(StringUtils.substring(line, 0, 6));
    if (!Objects.equals(PdbExpdtaLine.RECORD_NAME, recordName)) {
      throw new PdbParsingException("PDB line does not start with EXPDTA");
    }

    final List<ExperimentalTechnique> experimentalTechniques = new ArrayList<>();
    for (final String techniqueFullName :
        StringUtils.trimToEmpty(StringUtils.substring(line, 10)).split(";")) {
      final ExperimentalTechnique technique =
          ExperimentalTechnique.fromFullName(StringUtils.trimToEmpty(techniqueFullName));
      if (technique == ExperimentalTechnique.UNKNOWN) {
        PdbExpdtaLine.LOGGER.warn("Unknown experimental technique: {}", techniqueFullName);
        continue;
      }
      experimentalTechniques.add(technique);
    }
    return ImmutablePdbExpdtaLine.of(experimentalTechniques);
  }

  /**
   * @return The value of the {@code experimentalTechniques} attribute
   */
  @Value.Parameter(order = 1)
  public abstract List<ExperimentalTechnique> experimentalTechniques();

  @Override
  public final String toString() {
    return toPdb();
  }

  /**
   * @return A line in PDB format.
   */
  public final String toPdb() {
    final StringBuilder builder = new StringBuilder();

    if (experimentalTechniques().isEmpty()) {
      builder.append(ExperimentalTechnique.UNKNOWN);
    } else {
      builder.append(experimentalTechniques().get(0).getPdbName());
      for (int i = 1; i < experimentalTechniques().size(); i++) {
        builder.append("; ");
        builder.append(experimentalTechniques().get(i).getPdbName());
      }
    }
    return String.format(Locale.US, PdbExpdtaLine.FORMAT, builder);
  }
}
