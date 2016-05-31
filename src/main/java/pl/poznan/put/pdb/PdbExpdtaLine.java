package pl.poznan.put.pdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PdbExpdtaLine {
    public enum ExperimentalTechnique {
        ELECTRON_CRYSTALLOGRAPHY("ELECTRON CRYSTALLOGRAPHY"),
        ELECTRON_MICROSCOPY("ELECTRON MICROSCOPY"),
        EPR("EPR"),
        FIBER_DIFFRACTION("FIBER DIFFRACTION"),
        FLUORESCENCE_TRANSFER("FLUORESCENCE TRANSFER"),
        INFRARED_SPECTROSCOPY("INFRARED SPECTROSCOPY"),
        NEUTRON_DIFFRACTION("NEUTRON DIFFRACTION"),
        POWDER_DIFFRACTION("POWDER DIFFRACTION"),
        SOLID_STATE_NMR("SOLID-STATE NMR"),
        SOLUTION_NMR("SOLUTION NMR"),
        SOLUTION_SCATTERING("SOLUTION SCATTERING"),
        THEORETICAL_MODEL("THEORETICAL MODEL"),
        X_RAY_DIFFRACTION("X-RAY DIFFRACTION"),
        UNKNOWN("UNKNOWN");


        private final String fullName;

        ExperimentalTechnique(String fullName) {
            this.fullName = fullName;
        }

        public String getFullName() {
            return fullName;
        }

        public static ExperimentalTechnique fromFullName(String fullName) {
            for (ExperimentalTechnique technique : ExperimentalTechnique.values()) {
                if (technique.fullName.equals(fullName)) {
                    return technique;
                }
            }
            return ExperimentalTechnique.UNKNOWN;
        }
    }

    // @formatter:off
    //  COLUMNS       DATA TYPE      FIELD         DEFINITION
    //  ------------------------------------------------------------------------------------
    //  1 -  6       Record name    "EXPDTA"
    //  9 - 10       Continuation   continuation  Allows concatenation of multiple records.
    // 11 - 79       SList          technique     The experimental technique(s) with
    //                                            optional comment describing the
    //                                            sample or experiment.
    // @formatter:on
    private static final String FORMAT = "EXPDTA    %-70s";
    private static final PdbExpdtaLine EMPTY_INSTANCE = new PdbExpdtaLine(Collections.singletonList(ExperimentalTechnique.UNKNOWN));

    public static PdbExpdtaLine emptyInstance() {
        return PdbExpdtaLine.EMPTY_INSTANCE;
    }

    public static PdbExpdtaLine parse(String line) throws PdbParsingException {
        String recordName = line.substring(0, 6).trim();

        if (!"EXPDTA".equals(recordName)) {
            throw new PdbParsingException("PDB line does not start with EXPDTA");
        }

        List<ExperimentalTechnique> experimentalTechniques = new ArrayList<>();
        for (String techniqueFullName : line.substring(10).trim().split(";")) {
            ExperimentalTechnique technique = ExperimentalTechnique.fromFullName(techniqueFullName.trim());
            if (technique == ExperimentalTechnique.UNKNOWN) {
                throw new PdbParsingException("Failed to parse line: " + line);
            }
            experimentalTechniques.add(technique);
        }
        return new PdbExpdtaLine(experimentalTechniques);
    }

    private final List<ExperimentalTechnique> experimentalTechniques;

    public PdbExpdtaLine(List<ExperimentalTechnique> experimentalTechniques) {
        this.experimentalTechniques = experimentalTechniques;
    }

    public List<ExperimentalTechnique> getExperimentalTechniques() {
        return experimentalTechniques;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (experimentalTechniques.isEmpty()) {
            builder.append(ExperimentalTechnique.UNKNOWN);
        } else {
            builder.append(experimentalTechniques.get(0).fullName);
            for (int i = 1; i < experimentalTechniques.size(); i++) {
                builder.append("; ");
                builder.append(experimentalTechniques.get(i).fullName);
            }
        }
        return String.format(Locale.US, PdbExpdtaLine.FORMAT, builder.toString());
    }
}
