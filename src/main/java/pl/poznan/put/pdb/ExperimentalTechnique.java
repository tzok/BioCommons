package pl.poznan.put.pdb;

import java.util.Objects;

/**
 * Lists all experimental techniques a structure can be solved with (according to the PDB sources).
 */
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

  ExperimentalTechnique(final String fullName) {
    this.fullName = fullName;
  }

  public static ExperimentalTechnique fromFullName(final String fullName) {
    for (final ExperimentalTechnique technique : ExperimentalTechnique.values()) {
      if (Objects.equals(technique.fullName, fullName)) {
        return technique;
      }
    }
    return ExperimentalTechnique.UNKNOWN;
  }

  public String getFullName() {
    return fullName;
  }
}