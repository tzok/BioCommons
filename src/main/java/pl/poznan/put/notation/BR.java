package pl.poznan.put.notation;

import java.util.Arrays;

/**
 * Base-ribose notation described in: Classification and Energetics of the Base-Phosphate
 * Interactions in RNA. C.L. Zirbel, J.E. Šponer, J. Šponer, J. Stombaugh, N.B. Leontis. Nucleic
 * Acids Research. 2009. 37(15):4898–4918. doi:10.1093/nar/gkp468
 */
public enum BR {
  _0("0BR", "n0BR", "0RB", "n0RB"),
  _1("1BR", "n1BR", "1RB", "n1RB"),
  _2("2BR", "n2BR", "2RB", "n2RB"),
  _3("3BR", "n3BR", "3RB", "n3RB"),
  _4("4BR", "n4BR", "4RB", "n4RB"),
  _5("5BR", "n5BR", "5RB", "n5RB"),
  _6("6BR", "n6BR", "6RB", "n6RB"),
  _7("7BR", "n7BR", "7RB", "n7RB"),
  _8("8BR", "n8BR", "8RB", "n8RB"),
  _9("9BR", "n9BR", "9RB", "n9RB"),
  UNKNOWN("UNKNOWN");

  private final String[] displayNames;

  BR(final String... displayNames) {
    this.displayNames = displayNames;
  }

  /**
   * Find an enum constant that matches the given one or return a predefined UNKNOWN value.
   *
   * @param candidate A string representing a BR value.
   * @return An instance of this class that matches given name or UNKNOWN if none does.
   */
  public static BR fromString(final String candidate) {
    return Arrays.stream(BR.values())
        .filter(br -> Arrays.asList(br.displayNames).contains(candidate))
        .findFirst()
        .orElse(BR.UNKNOWN);
  }

  /** @return The default display name. */
  public String getDisplayName() {
    return displayNames[0];
  }
}
