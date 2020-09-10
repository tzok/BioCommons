package pl.poznan.put.pdb;

import java.util.Arrays;

/** An experimental technique used to solve a structure (according to the PDB sources). */
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

  private final String pdbName;

  ExperimentalTechnique(final String pdbName) {
    this.pdbName = pdbName;
  }

  /**
   * Finds a matching instance of this enum in case-insensitive manner.
   *
   * @param pdbName A string representation of an experimental technique.
   * @return An instance of this class that matches {@code fullName} or UNKNOWN if none does.
   */
  public static ExperimentalTechnique fromFullName(final String pdbName) {
    return Arrays.stream(ExperimentalTechnique.values())
        .filter(technique -> technique.pdbName.equalsIgnoreCase(pdbName))
        .findFirst()
        .orElse(ExperimentalTechnique.UNKNOWN);
  }

  /** @return The name as used in PDB files. */
  public String getPdbName() {
    return pdbName;
  }
}
