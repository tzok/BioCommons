package pl.poznan.put.structure.secondary;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.CompareToBuilder;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.RNAInteractionType;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

@EqualsAndHashCode
public class ClassifiedBasePair implements Serializable, Comparable<ClassifiedBasePair> {
  private final BasePair basePair;
  private final RNAInteractionType interactionType;
  private final Saenger saenger;
  private final LeontisWesthof leontisWesthof;
  private final BPh bph;
  private final BR br;

  @EqualsAndHashCode.Exclude private HelixOrigin helixOrigin;
  @EqualsAndHashCode.Exclude private boolean isRepresented;

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

  public static ClassifiedBasePair assumeCanonical(final BasePair pair) {
    return new ClassifiedBasePair(
        pair,
        RNAInteractionType.BASE_BASE,
        Saenger.assumeCanonical(pair),
        LeontisWesthof.CWW,
        BPh.UNKNOWN,
        BR.UNKNOWN,
        HelixOrigin.UNKNOWN);
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
  public final Boolean getIsRepresented() {
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

  public final boolean is5to3() {
    return basePair.is5to3();
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

    return new CompareToBuilder()
        .append(basePair, t.basePair)
        .append(interactionType, t.interactionType)
        .append(leontisWesthof, t.leontisWesthof)
        .append(saenger, t.saenger)
        .append(bph, t.bph)
        .append(br, t.br)
        .toComparison();
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
