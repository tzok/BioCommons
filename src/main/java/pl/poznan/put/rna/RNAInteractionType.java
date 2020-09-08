package pl.poznan.put.rna;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Locale;

@Value.Immutable
public abstract class RNAInteractionType implements Serializable, Comparable<RNAInteractionType> {
  public static final RNAInteractionType BASE_BASE =
      ImmutableRNAInteractionType.builder()
          .left(RNAResidueComponentType.BASE)
          .right(RNAResidueComponentType.BASE)
          .isPairing(true)
          .build();
  public static final RNAInteractionType BASE_BASE_1H =
      ImmutableRNAInteractionType.builder()
          .left(RNAResidueComponentType.BASE)
          .right(RNAResidueComponentType.BASE)
          .isPairing(false)
          .description("base - base (1H)")
          .build();
  public static final RNAInteractionType BASE_PHOSPHATE =
      ImmutableRNAInteractionType.builder()
          .left(RNAResidueComponentType.BASE)
          .right(RNAResidueComponentType.PHOSPHATE)
          .isPairing(false)
          .build();
  public static final RNAInteractionType BASE_RIBOSE =
      ImmutableRNAInteractionType.builder()
          .left(RNAResidueComponentType.BASE)
          .right(RNAResidueComponentType.RIBOSE)
          .isPairing(false)
          .build();
  public static final RNAInteractionType SUGAR_SUGAR =
      ImmutableRNAInteractionType.builder()
          .left(RNAResidueComponentType.RIBOSE)
          .right(RNAResidueComponentType.RIBOSE)
          .isPairing(false)
          .build();
  public static final RNAInteractionType STACKING =
      ImmutableRNAInteractionType.builder()
          .left(RNAResidueComponentType.BASE)
          .right(RNAResidueComponentType.BASE)
          .isPairing(false)
          .description("stacking")
          .build();
  public static final RNAInteractionType OTHER =
      ImmutableRNAInteractionType.builder()
          .left(RNAResidueComponentType.UNKNOWN)
          .right(RNAResidueComponentType.UNKNOWN)
          .isPairing(false)
          .description("other")
          .build();

  private static int fragmentInternalValue(final RNAResidueComponentType type) {
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

  public abstract RNAResidueComponentType left();

  public abstract RNAResidueComponentType right();

  public abstract boolean isPairing();

  @Value.Default
  public String description() {
    return StringUtils.lowerCase(String.format("%s - %s", right(), left()), Locale.US);
  }

  @Override
  public final String toString() {
    return description();
  }

  @Override
  public final int compareTo(final RNAInteractionType t) {
    return Integer.compare(internalValue(), t.internalValue());
  }

  public final RNAInteractionType invert() {
    return ImmutableRNAInteractionType.builder()
        .left(right())
        .right(left())
        .isPairing(isPairing())
        .build();
  }

  /*
   * The internal value ranks fragments like this: base, sugar, phosphate and
   * rest. This allows to sort interactions in ascending order. The top will
   * be taken by pairing base-base interactions, then non-pairing, then
   * base-sugar, etc. Also, 'stacking' interactions should be the last
   */
  private int internalValue() {
    if (equals(RNAInteractionType.STACKING)) {
      return Integer.MAX_VALUE;
    }

    int value = 0;
    value += RNAInteractionType.fragmentInternalValue(left());
    value += RNAInteractionType.fragmentInternalValue(right());
    if (isPairing()) {
      value = -value;
    }
    return value;
  }
}
