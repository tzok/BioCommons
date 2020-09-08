package pl.poznan.put.notation;

import pl.poznan.put.structure.secondary.BasePair;

import java.util.Arrays;
import java.util.Objects;

/**
 * Classification of RNA base pairs described in Principles of Nucleic Acid Structure. W. Saenger.
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
   * Find enum that matches the given name or return UNKNOWN otherwise.
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
   * This is not a "real" ordinal, but an integer in range [1; 28] that matches one of the Sanger
   * roman numerals.
   *
   * @param ordinal An integer in range [1; 28].
   * @return An instance of this class that matches {@code ordinal} or UNKNOWN if none does.
   */
  public static Saenger fromOrdinal(final int ordinal) {
    if ((ordinal >= 1) && (ordinal <= 28)) {
      return Saenger.values()[ordinal - 1];
    }
    return Saenger.UNKNOWN;
  }

  /**
   * Check which one of {@link Saenger} instances matches a canonical base pair of provided
   * sequence. C-G canonical base pairs are XIX, A-U are XX and G-U are XXVIII. If base pair
   * sequence is other than C-G, A-U or G-U, then this method returns UNKNOWN.
   *
   * @param basePair A base pair to be checked.
   * @return XIX for C-G base pair, XX for A-U base pair, XXVIII for G-U base pair and UNKNOWN for
   *     all others.
   */
  public static Saenger assumeCanonical(final BasePair basePair) {
    final char l = Character.toUpperCase(basePair.getLeft().oneLetterName());
    final char r = Character.toUpperCase(basePair.getRight().oneLetterName());
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
   * Check if instance of {@link Saenger} represents a canonical base pair (XIX, XX and XXVIII).
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

  /** @return True if this instance is either XIX, XX or XXVIII. */
  public boolean isCanonical() {
    return Saenger.isCanonical(this);
  }
}
