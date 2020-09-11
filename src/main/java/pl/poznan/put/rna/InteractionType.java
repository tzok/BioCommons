package pl.poznan.put.rna;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Locale;

@Value.Immutable
public abstract class InteractionType implements Serializable, Comparable<InteractionType> {
  public static final InteractionType BASE_BASE =
      ImmutableInteractionType.builder()
          .left(NucleotideComponentType.BASE)
          .right(NucleotideComponentType.BASE)
          .isPairing(true)
          .build();
  public static final InteractionType BASE_BASE_1H =
      ImmutableInteractionType.builder()
          .left(NucleotideComponentType.BASE)
          .right(NucleotideComponentType.BASE)
          .isPairing(false)
          .description("base - base (1H)")
          .build();
  public static final InteractionType BASE_PHOSPHATE =
      ImmutableInteractionType.builder()
          .left(NucleotideComponentType.BASE)
          .right(NucleotideComponentType.PHOSPHATE)
          .isPairing(false)
          .build();
  public static final InteractionType BASE_RIBOSE =
      ImmutableInteractionType.builder()
          .left(NucleotideComponentType.BASE)
          .right(NucleotideComponentType.RIBOSE)
          .isPairing(false)
          .build();
  public static final InteractionType SUGAR_SUGAR =
      ImmutableInteractionType.builder()
          .left(NucleotideComponentType.RIBOSE)
          .right(NucleotideComponentType.RIBOSE)
          .isPairing(false)
          .build();
  public static final InteractionType STACKING =
      ImmutableInteractionType.builder()
          .left(NucleotideComponentType.BASE)
          .right(NucleotideComponentType.BASE)
          .isPairing(false)
          .description("stacking")
          .build();
  public static final InteractionType OTHER =
      ImmutableInteractionType.builder()
          .left(NucleotideComponentType.UNKNOWN)
          .right(NucleotideComponentType.UNKNOWN)
          .isPairing(false)
          .description("other")
          .build();

  private static int fragmentInternalValue(final NucleotideComponentType type) {
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

  public abstract NucleotideComponentType left();

  public abstract NucleotideComponentType right();

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
  public final int compareTo(final InteractionType t) {
    return Integer.compare(internalValue(), t.internalValue());
  }

  public final InteractionType invert() {
    return ImmutableInteractionType.builder()
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
    if (equals(InteractionType.STACKING)) {
      return Integer.MAX_VALUE;
    }

    int value = 0;
    value += InteractionType.fragmentInternalValue(left());
    value += InteractionType.fragmentInternalValue(right());
    if (isPairing()) {
      value = -value;
    }
    return value;
  }
}
