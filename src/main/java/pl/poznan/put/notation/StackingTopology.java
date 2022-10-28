package pl.poznan.put.notation;

import java.util.Arrays;

/**
 * Stacking topologies are described in: New Metrics for Comparing and Assessing Discrepancies
 * between RNA 3D Structures and Models. M. Parisien, J.A. Cruz, E. Westhof, F. Major. RNA. 2009.
 * 15(10):1875–1885. doi:10.1261/rna.1700409
 */
public enum StackingTopology {
  UPWARD("upward"),
  DOWNWARD("downward"),
  INWARD("inward"),
  OUTWARD("outward"),
  UNKNOWN("UNKNOWN");

  private final String displayName;

  StackingTopology(final String displayName) {
    this.displayName = displayName;
  }

  /**
   * Finds an enum constant that matches the given one or return a predefined UNKNOWN value.
   *
   * @param candidate A string representing a StackingTopology value.
   * @return An instance of this class that matches given name or UNKNOWN if none does.
   */
  public static StackingTopology fromString(final String candidate) {
    return Arrays.stream(StackingTopology.values())
        .filter(br -> br.displayName.contains(candidate))
        .findFirst()
        .orElse(StackingTopology.UNKNOWN);
  }

  /**
   * @return The default display name.
   */
  public String displayName() {
    return displayName;
  }

  /**
   * @return An instance where up- and down- or in- and out- are inverted.
   */
  public StackingTopology invert() {
    switch (this) {
      case UPWARD:
        return DOWNWARD;
      case DOWNWARD:
        return UPWARD;
      case INWARD:
        return OUTWARD;
      case OUTWARD:
        return INWARD;
      case UNKNOWN:
        return UNKNOWN;
    }
    throw new IllegalArgumentException("Failed to invert stacking topology: " + this);
  }
}
