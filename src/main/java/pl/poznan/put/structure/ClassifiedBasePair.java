package pl.poznan.put.structure;

import org.apache.commons.lang3.builder.CompareToBuilder;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.InteractionType;

import java.io.Serializable;
import java.util.Objects;

/** A pair of interacting nucleotides with classification info. */
public interface ClassifiedBasePair extends Serializable, Comparable<ClassifiedBasePair> {
  /** @return The pair of nucleotides which interact. */
  BasePair basePair();

  /** @return The type of interaction. */
  default InteractionType interactionType() {
    return InteractionType.BASE_BASE;
  }

  /** @return The classification by Saenger (if applicable). */
  default Saenger saenger() {
    return Saenger.assumeCanonical(basePair());
  }

  /** @return The classification by Leontis and Westhof (if applicable). */
  default LeontisWesthof leontisWesthof() {
    return LeontisWesthof.CWW;
  }

  /** @return The classification of base-phosphate interaction (if applicable). */
  default BPh bph() {
    return BPh.UNKNOWN;
  }

  /** @return The classification of base-ribose interaction (if applicable). */
  default BR br() {
    return BR.UNKNOWN;
  }

  /** @return Information about origination of this pair from helical analysis (if applicable). */
  default HelixOrigin helixOrigin() {
    return HelixOrigin.UNKNOWN;
  }

  /** @return True if this interaction is represented in secondary structure. */
  default boolean isRepresented() {
    return false;
  }

  /** @return An instance of this class, but with interacting partners inverted. */
  ClassifiedBasePair invert();

  /** @return A comment containing Saenger and Leontis-Westhof classifications. */
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

  /** @return True if this instance describes a base-ribose interaction. */
  default boolean isBaseRibose() {
    return Objects.equals(interactionType(), InteractionType.BASE_RIBOSE)
        || Objects.equals(interactionType().invert(), InteractionType.BASE_RIBOSE);
  }

  /** @return True if this instance describes a stacking interaction. */
  default boolean isStacking() {
    return Objects.equals(interactionType(), InteractionType.STACKING);
  }

  /** @return True if this instance describes a base-phosphate interaction. */
  default boolean isBasePhosphate() {
    return Objects.equals(interactionType(), InteractionType.BASE_PHOSPHATE)
        || Objects.equals(interactionType().invert(), InteractionType.BASE_PHOSPHATE);
  }

  /** @return True if this instance describes a base-base interaction. */
  default boolean isPairing() {
    return interactionType().isPairing();
  }

  /** @return True if this instance describes a non-canonical base-base interaction. */
  default boolean isNonCanonical() {
    return !Saenger.isCanonical(saenger())
        && (Objects.equals(interactionType(), InteractionType.BASE_BASE)
            || Objects.equals(interactionType(), InteractionType.BASE_BASE_1H));
  }

  /** @return True if this instance describes a canonical base-base interaction. */
  default boolean isCanonical() {
    return Saenger.isCanonical(saenger())
        && Objects.equals(interactionType(), InteractionType.BASE_BASE);
  }

  default int compareTo(final ClassifiedBasePair t) {
    return new CompareToBuilder()
        .append(basePair(), t.basePair())
        .append(interactionType(), t.interactionType())
        .append(leontisWesthof(), t.leontisWesthof())
        .append(saenger(), t.saenger())
        .append(bph(), t.bph())
        .append(br(), t.br())
        .build();
  }
}
