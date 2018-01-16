package pl.poznan.put.structure.secondary;

import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.RNAInteractionType;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

public class ClassifiedBasePair implements Serializable, Comparable<ClassifiedBasePair> {
  private static final long serialVersionUID = -7311037449944786616L;

  private final BasePair basePair;
  private final RNAInteractionType interactionType;
  private final Saenger saenger;
  private final LeontisWesthof leontisWesthof;
  private final BPh bph;
  private final BR br;
  private HelixOrigin helixOrigin;
  private boolean isRepresented;

  public ClassifiedBasePair(
      final BasePair basePair,
      final RNAInteractionType interactionType,
      final Saenger saenger,
      final LeontisWesthof leontisWesthof,
      final BPh bph,
      final BR br,
      final HelixOrigin helixOrigin) {
    super();
    this.basePair = basePair;
    this.interactionType = interactionType;
    this.saenger = saenger;
    this.leontisWesthof = leontisWesthof;
    this.bph = bph;
    this.br = br;
    this.helixOrigin = helixOrigin;
  }

  public final BasePair getBasePair() {
    return basePair;
  }

  public final RNAInteractionType getInteractionType() {
    return interactionType;
  }

  public final Saenger getSaenger() {
    return saenger;
  }

  public final LeontisWesthof getLeontisWesthof() {
    return leontisWesthof;
  }

  public final BPh getBph() {
    return bph;
  }

  public final BR getBr() {
    return br;
  }

  public final HelixOrigin getHelixOrigin() {
    return helixOrigin;
  }

  public final void setHelixOrigin(final HelixOrigin helixOrigin) {
    this.helixOrigin = helixOrigin;
  }

  public final boolean isRepresented() {
    return isRepresented;
  }

  public final void setRepresented(final boolean isRepresented) {
    this.isRepresented = isRepresented;
  }

  // required for Spring to get "isRepresented" field
  public final boolean getIsRepresented() {
    return isRepresented;
  }

  public final boolean canUseInSecondaryStructure() {
    return interactionType.isPairing();
  }

  public final boolean isCanonical() {
    return Saenger.isCanonical(saenger);
  }

  public final boolean isNonCanonical() {
    return !Saenger.isCanonical(saenger)
        && (Objects.equals(interactionType, RNAInteractionType.BASE_BASE)
            || Objects.equals(interactionType, RNAInteractionType.BASE_BASE_1H));
  }

  public final boolean isStacking() {
    return Objects.equals(interactionType, RNAInteractionType.STACKING);
  }

  public final boolean isBasePhosphate() {
    return Objects.equals(interactionType, RNAInteractionType.BASE_PHOSPHATE)
        || Objects.equals(interactionType.invert(), RNAInteractionType.BASE_PHOSPHATE);
  }

  public final boolean isBaseRibose() {
    return Objects.equals(interactionType, RNAInteractionType.BASE_RIBOSE)
        || Objects.equals(interactionType.invert(), RNAInteractionType.BASE_RIBOSE);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    final ClassifiedBasePair other = (ClassifiedBasePair) o;
    return Objects.equals(basePair, other.basePair)
        && Objects.equals(interactionType, other.interactionType)
        && (saenger == other.saenger)
        && (leontisWesthof == other.leontisWesthof)
        && (bph == other.bph)
        && (br == other.br);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basePair, interactionType, saenger, leontisWesthof, bph, br);
  }

  @Override
  public String toString() {
    return "ClassifiedBasePair [basePair="
        + basePair
        + ", interactionType="
        + interactionType
        + ", saenger="
        + saenger
        + ", leontisWesthof="
        + leontisWesthof
        + ", helixOrigin="
        + helixOrigin
        + ", isRepresented="
        + isRepresented
        + ']';
  }

  @Override
  public final int compareTo(@Nonnull final ClassifiedBasePair t) {
    if (equals(t)) {
      return 0;
    }

    final int interactionComparison = interactionType.compareTo(t.interactionType);
    if (interactionComparison != 0) {
      return interactionComparison;
    }

    return basePair.compareTo(t.basePair);
  }

  public final String generateComment() {
    if ((saenger != Saenger.UNKNOWN) && (leontisWesthof != LeontisWesthof.UNKNOWN)) {
      return "S:" + saenger + ", LW:" + leontisWesthof;
    }
    if (saenger != Saenger.UNKNOWN) {
      return "S:" + saenger;
    }
    if (leontisWesthof != LeontisWesthof.UNKNOWN) {
      return "LW:" + leontisWesthof;
    }
    return "unknown classification";
  }

  public final ClassifiedBasePair invert() {
    return new ClassifiedBasePair(
        basePair.invert(),
        interactionType.invert(),
        saenger,
        leontisWesthof.invert(),
        bph,
        br,
        helixOrigin);
  }
}
