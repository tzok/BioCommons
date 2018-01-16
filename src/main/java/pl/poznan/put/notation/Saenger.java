package pl.poznan.put.notation;

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

  @Override
  public String toString() {
    if (this == Saenger.UNKNOWN) {
      return "n/a";
    }
    return name();
  }

  public static Saenger fromOrdinal(final int ordinal) {
    if ((ordinal >= 1) && (ordinal <= 28)) {
      return Saenger.values()[ordinal];
    }
    return Saenger.UNKNOWN;
  }

  public boolean isCanonical() {
    return Saenger.isCanonical(this);
  }

  public static boolean isCanonical(final Saenger s) {
    return (s == Saenger.XIX) || (s == Saenger.XX) || (s == Saenger.XXVIII);
  }
}
