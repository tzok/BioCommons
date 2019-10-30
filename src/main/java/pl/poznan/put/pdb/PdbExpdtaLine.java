package pl.poznan.put.pdb;

import java.io.Serializable;
import java.util.*;

public class PdbExpdtaLine implements Serializable {
  private static final long serialVersionUID = 6276886553884351623L;

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
  private static final PdbExpdtaLine EMPTY_INSTANCE =
      new PdbExpdtaLine(Collections.singletonList(ExperimentalTechnique.UNKNOWN));
  private final List<ExperimentalTechnique> experimentalTechniques;

  public PdbExpdtaLine(final List<ExperimentalTechnique> experimentalTechniques) {
    super();
    this.experimentalTechniques = new ArrayList<>(experimentalTechniques);
  }

  public static PdbExpdtaLine emptyInstance() {
    return PdbExpdtaLine.EMPTY_INSTANCE;
  }

  public static PdbExpdtaLine parse(final String line) {
    final String recordName = line.substring(0, 6).trim();

    if (!Objects.equals(PdbExpdtaLine.RECORD_NAME, recordName)) {
      throw new PdbParsingException("PDB line does not start with EXPDTA");
    }

    final List<ExperimentalTechnique> experimentalTechniques = new ArrayList<>();
    for (final String techniqueFullName : line.substring(10).trim().split(";")) {
      final ExperimentalTechnique technique =
          ExperimentalTechnique.fromFullName(techniqueFullName.trim());
      if (technique == ExperimentalTechnique.UNKNOWN) {
        throw new PdbParsingException("Failed to parse line: " + line);
      }
      experimentalTechniques.add(technique);
    }
    return new PdbExpdtaLine(experimentalTechniques);
  }

  public final List<ExperimentalTechnique> getExperimentalTechniques() {
    return Collections.unmodifiableList(experimentalTechniques);
  }

  public final boolean isValid() {
    return !experimentalTechniques.isEmpty()
        && (experimentalTechniques.get(0) != ExperimentalTechnique.UNKNOWN);
  }

  @Override
  public final String toString() {
    final StringBuilder builder = new StringBuilder();

    if (experimentalTechniques.isEmpty()) {
      builder.append(ExperimentalTechnique.UNKNOWN);
    } else {
      builder.append(experimentalTechniques.get(0).getPdbName());
      for (int i = 1; i < experimentalTechniques.size(); i++) {
        builder.append("; ");
        builder.append(experimentalTechniques.get(i).getPdbName());
      }
    }
    return String.format(Locale.US, PdbExpdtaLine.FORMAT, builder.toString());
  }
}
