package pl.poznan.put.structure.secondary;

import org.apache.commons.lang3.builder.CompareToBuilder;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.RNAInteractionType;

import java.io.Serializable;
import java.util.Objects;

public interface ClassifiedBasePair extends Serializable, Comparable<ClassifiedBasePair> {
  BasePair basePair();

  RNAInteractionType interactionType();

  Saenger saenger();

  LeontisWesthof leontisWesthof();

  BPh bph();

  BR br();

  HelixOrigin helixOrigin();

  boolean isRepresented();

  ClassifiedBasePair invert();

  default String generateComment() {
    if ((saenger() != Saenger.UNKNOWN) && (leontisWesthof() != LeontisWesthof.UNKNOWN)) {
      return "S:" + saenger() + ", LW:" + leontisWesthof();
    }
    if (saenger() != Saenger.UNKNOWN) {
      return "S:" + saenger();
    }
    if (leontisWesthof() != LeontisWesthof.UNKNOWN) {
      return "LW:" + leontisWesthof();
    }
    return "unknown classification";
  }

  default boolean is5to3() {
    return basePair().is5to3();
  }

  default boolean isBaseRibose() {
    return Objects.equals(interactionType(), RNAInteractionType.BASE_RIBOSE)
        || Objects.equals(interactionType().invert(), RNAInteractionType.BASE_RIBOSE);
  }

  default boolean isStacking() {
    return Objects.equals(interactionType(), RNAInteractionType.STACKING);
  }

  default boolean isBasePhosphate() {
    return Objects.equals(interactionType(), RNAInteractionType.BASE_PHOSPHATE)
        || Objects.equals(interactionType().invert(), RNAInteractionType.BASE_PHOSPHATE);
  }

  default boolean isPairing() {
    return interactionType().isPairing();
  }

  default boolean isNonCanonical() {
    return !Saenger.isCanonical(saenger())
        && (Objects.equals(interactionType(), RNAInteractionType.BASE_BASE)
            || Objects.equals(interactionType(), RNAInteractionType.BASE_BASE_1H));
  }

  default boolean isCanonical() {
    return Saenger.isCanonical(saenger());
  }

  default int compareTo(final ClassifiedBasePair t) {
    if (t == null) {
      throw new IllegalArgumentException("null value passed to `compareTo` method");
    }

    if (equals(t)) {
      return 0;
    }

    return new CompareToBuilder()
        .append(basePair(), t.basePair())
        .append(interactionType(), t.interactionType())
        .append(leontisWesthof(), t.leontisWesthof())
        .append(saenger(), t.saenger())
        .append(bph(), t.bph())
        .append(br(), t.br())
        .toComparison();
  }
}
