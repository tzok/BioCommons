package pl.poznan.put.rna;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Locale;

@EqualsAndHashCode
public final class RNAInteractionType implements Serializable, Comparable<RNAInteractionType> {
  public static final RNAInteractionType BASE_BASE =
      new RNAInteractionType(RNAResidueComponentType.BASE, RNAResidueComponentType.BASE, true);
  public static final RNAInteractionType BASE_BASE_1H =
      new RNAInteractionType(
          RNAResidueComponentType.BASE, RNAResidueComponentType.BASE, "base - base (1H)");
  public static final RNAInteractionType BASE_PHOSPHATE =
      new RNAInteractionType(
          RNAResidueComponentType.BASE, RNAResidueComponentType.PHOSPHATE, false);
  public static final RNAInteractionType BASE_RIBOSE =
      new RNAInteractionType(RNAResidueComponentType.BASE, RNAResidueComponentType.RIBOSE, false);
  public static final RNAInteractionType SUGAR_SUGAR =
      new RNAInteractionType(RNAResidueComponentType.RIBOSE, RNAResidueComponentType.RIBOSE, false);
  public static final RNAInteractionType STACKING =
      new RNAInteractionType(
          RNAResidueComponentType.BASE, RNAResidueComponentType.BASE, "stacking");
  public static final RNAInteractionType OTHER =
      new RNAInteractionType(
          RNAResidueComponentType.UNKNOWN, RNAResidueComponentType.UNKNOWN, "other");

  private final RNAResidueComponentType left;
  private final RNAResidueComponentType right;
  private final boolean isPairing;
  private final String description;

  public RNAInteractionType(
      final RNAResidueComponentType left,
      final RNAResidueComponentType right,
      final boolean isPairing) {
    super();
    this.left = left;
    this.right = right;
    this.isPairing = isPairing;
    description =
        left.name().toLowerCase(Locale.ENGLISH) + " - " + right.name().toLowerCase(Locale.ENGLISH);
  }

  public RNAInteractionType(
      final RNAResidueComponentType left,
      final RNAResidueComponentType right,
      final String description) {
    super();
    this.left = left;
    this.right = right;
    isPairing = false;
    this.description = description;
  }

  public RNAResidueComponentType getLeft() {
    return left;
  }

  public RNAResidueComponentType getRight() {
    return right;
  }

  public boolean isPairing() {
    return isPairing;
  }

  @Override
  public String toString() {
    return description;
  }

  @Override
  public int compareTo(final RNAInteractionType t) {
    final int mine = getInternalValue();
    final int theirs = t.getInternalValue();
    return Integer.compare(mine, theirs);
  }

  /*
   * The internal value ranks fragments like this: base, sugar, phosphate and
   * rest. This allows to sort interactions in ascending order. The top will
   * be taken by pairing base-base interactions, then non-pairing, then
   * base-sugar, etc. Also, 'stacking' interactions should be the last
   */
  private int getInternalValue() {
    if (equals(RNAInteractionType.STACKING)) {
      return Integer.MAX_VALUE;
    }

    int value = 0;
    value += RNAInteractionType.getNucleotideFragmentInternalValue(left);
    value += RNAInteractionType.getNucleotideFragmentInternalValue(right);
    if (isPairing) {
      value = -value;
    }
    return value;
  }

  private static int getNucleotideFragmentInternalValue(final RNAResidueComponentType type) {
    switch (type) {
      case BASE:
        return 1;
      case RIBOSE:
        return 10;
      case PHOSPHATE:
        return 100;
      case UNKNOWN:
      default:
        return 1000;
    }
  }

  public RNAInteractionType invert() {
    return new RNAInteractionType(right, left, isPairing);
  }
}
