package pl.poznan.put.notation;

import java.util.Arrays;
import java.util.Objects;
import pl.poznan.put.structure.BasePair;

/**
 * A classification of RNA base pairs described in Principles of Nucleic Acid Structure. W. Saenger.
 */
public enum Saenger {
  I,
  II,
  III,
  IV,
  V,
  VI,
  VII,
  VIII,
  IX,
  X,
  XI,
  XII,
  XIII,
  XIV,
  XV,
  XVI,
  XVII,
  XVIII,
  XIX,
  XX,
  XXI,
  XXII,
  XXIII,
  XXIV,
  XXV,
  XXVI,
  XXVII,
  XXVIII,
  UNKNOWN;

  /**
   * Finds an enum that matches the given name or return UNKNOWN otherwise.
   *
   * @param str A string representing Saenger notation.
   * @return An instance of this enum that matches the given name or UNKNOWN if none does.
   */
  public static Saenger fromString(final String str) {
    return Arrays.stream(Saenger.values())
        .filter(saenger -> Objects.equals(saenger.toString(), str))
        .findFirst()
        .orElse(Saenger.UNKNOWN);
  }

  /**
   * Matches a value in range 1-28 to an instance of this class with the same value written as a
   * Roman numeral. The number is used in mmCIF files in {@code hbond_28} field.
   *
   * @param number An integer in range [1; 28].
   * @return An instance of this class that matches {@code ordinal} or UNKNOWN if none does.
   */
  public static Saenger fromNumber(final int number) {
    if ((number >= 1) && (number <= 28)) {
      return Saenger.values()[number - 1];
    }
    return Saenger.UNKNOWN;
  }

  /**
   * Checks which one of {@link Saenger} instances matches a canonical base pair of provided
   * sequence. C-G canonical base pairs are XIX, A-U are XX and G-U are XXVIII. If base pair
   * sequence is other than C-G, A-U or G-U, then this method returns UNKNOWN.
   *
   * @param basePair A base pair to be checked.
   * @return XIX for C-G base pair, XX for A-U base pair, XXVIII for G-U base pair and UNKNOWN for
   *     all others.
   */
  public static Saenger assumeCanonical(final BasePair basePair) {
    final char l = Character.toUpperCase(basePair.left().oneLetterName());
    final char r = Character.toUpperCase(basePair.right().oneLetterName());
    final String pair = String.format("%c%c", l, r);

    if ("CG".equals(pair) || "GC".equals(pair)) {
      return Saenger.XIX;
    }
    if ("AU".equals(pair) || "UA".equals(pair)) {
      return Saenger.XX;
    }
    if ("GU".equals(pair) || "UG".equals(pair)) {
      return Saenger.XXVIII;
    }
    return Saenger.UNKNOWN;
  }

  /**
   * Checks if instance of Saenger represents a canonical base pair (XIX, XX and XXVIII).
   *
   * @param saenger An instance to be checked.
   * @return True if {@code saenger} is either XIX, XX or XXVIII.
   */
  public static boolean isCanonical(final Saenger saenger) {
    return (saenger == Saenger.XIX) || (saenger == Saenger.XX) || (saenger == Saenger.XXVIII);
  }

  @Override
  public String toString() {
    if (this == Saenger.UNKNOWN) {
      return "n/a";
    }
    return name();
  }

  /**
   * @return True if this instance is either XIX, XX or XXVIII.
   */
  public boolean isCanonical() {
    return Saenger.isCanonical(this);
  }
}
