package pl.poznan.put.notation;

import pl.poznan.put.structure.secondary.BasePair;

import java.util.Objects;

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

  public static Saenger fromString(final String str) {
    for (final Saenger s : Saenger.values()) {
      if (Objects.equals(s.toString(), str)) {
        return s;
      }
    }
    return Saenger.UNKNOWN;
  }

  public static Saenger fromOrdinal(final int ordinal) {
    if ((ordinal >= 1) && (ordinal <= 28)) {
      return Saenger.values()[ordinal - 1];
    }
    return Saenger.UNKNOWN;
  }

  public static boolean isCanonical(final Saenger s) {
    return (s == Saenger.XIX) || (s == Saenger.XX) || (s == Saenger.XXVIII);
  }

  public static Saenger assumeCanonical(final BasePair basePair) {
    final char l = Character.toUpperCase(basePair.getLeft().getResidueOneLetterName());
    final char r = Character.toUpperCase(basePair.getRight().getResidueOneLetterName());
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

  @Override
  public String toString() {
    if (this == Saenger.UNKNOWN) {
      return "n/a";
    }
    return name();
  }

  public boolean isCanonical() {
    return Saenger.isCanonical(this);
  }
}
