package pl.poznan.put.notation;

import java.util.Arrays;

/**
 * Base-phosphate notation. Zirbel, C. L., et al (2009). Classification and energetics of the
 * base-phosphate interactions in RNA. Nucleic Acids Research, 37(15), 4898–4918.
 * http://doi.org/10.1093/nar/gkp468
 */
public enum BPh {
  _0("0BPh", "n0BPh", "0PhB", "n0PhB"),
  _1("1BPh", "n1BPh", "1PhB", "n1PhB"),
  _2("2BPh", "n2BPh", "2PhB", "n2PhB"),
  _3("3BPh", "n3BPh", "3PhB", "n3PhB"),
  _4("4BPh", "n4BPh", "4PhB", "n4PhB"),
  _5("5BPh", "n5BPh", "5PhB", "n5PhB"),
  _6("6BPh", "n6BPh", "6PhB", "n6PhB"),
  _7("7BPh", "n7BPh", "7PhB", "n7PhB"),
  _8("8BPh", "n8BPh", "8PhB", "n8PhB"),
  _9("9BPh", "n9BPh", "9PhB", "n9PhB"),
  UNKNOWN("UNKNOWN");

  private final String[] displayNames;

  BPh(final String... displayNames) {
    this.displayNames = displayNames;
  }

  public static BPh fromString(final String candidate) {
      return Arrays.stream(BPh.values()).filter(bph -> Arrays.asList(bph.displayNames).contains(candidate)).findFirst().orElse(BPh.UNKNOWN);
  }

  public String getDisplayName() {
    return displayNames[0];
  }
}
