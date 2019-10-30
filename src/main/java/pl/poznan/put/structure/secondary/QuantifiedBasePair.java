package pl.poznan.put.structure.secondary;

import lombok.EqualsAndHashCode;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.RNAInteractionType;

@EqualsAndHashCode(callSuper = true)
public class QuantifiedBasePair extends ClassifiedBasePair {
  private final double shear;
  private final double stretch;
  private final double stagger;
  private final double buckle;
  private final double propeller;
  private final double opening;

  public QuantifiedBasePair(
      final BasePair basePair,
      final Saenger saenger,
      final LeontisWesthof leontisWesthof,
      final BPh bph,
      final BR br,
      final double shear,
      final double stretch,
      final double stagger,
      final double buckle,
      final double propeller,
      final double opening) {
    super(
        basePair,
        RNAInteractionType.BASE_BASE,
        saenger,
        leontisWesthof,
        bph,
        br,
        HelixOrigin.UNKNOWN);
    this.shear = shear;
    this.stretch = stretch;
    this.stagger = stagger;
    this.buckle = buckle;
    this.propeller = propeller;
    this.opening = opening;
  }

  public final double getShear() {
    return shear;
  }

  public final double getStretch() {
    return stretch;
  }

  public final double getStagger() {
    return stagger;
  }

  public final double getBuckle() {
    return buckle;
  }

  public final double getPropeller() {
    return propeller;
  }

  public final double getOpening() {
    return opening;
  }

  @Override
  public final String toString() {
    return "QuantifiedBasePair{"
        + "basePair="
        + getBasePair()
        + ", saenger="
        + getSaenger()
        + ", leontisWesthof="
        + getLeontisWesthof()
        + ", shear="
        + shear
        + ", stretch="
        + stretch
        + ", stagger="
        + stagger
        + ", buckle="
        + buckle
        + ", propeller="
        + propeller
        + ", opening="
        + opening
        + '}';
  }
}
