package pl.poznan.put.structure.secondary;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.RNAInteractionType;

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

  public double getShear() {
    return shear;
  }

  public double getStretch() {
    return stretch;
  }

  public double getStagger() {
    return stagger;
  }

  public double getBuckle() {
    return buckle;
  }

  public double getPropeller() {
    return propeller;
  }

  public double getOpening() {
    return opening;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }

    final QuantifiedBasePair other = (QuantifiedBasePair) o;
    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(shear, other.shear)
        .append(stretch, other.stretch)
        .append(stagger, other.stagger)
        .append(buckle, other.buckle)
        .append(propeller, other.propeller)
        .append(opening, other.opening)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .appendSuper(super.hashCode())
        .append(shear)
        .append(stretch)
        .append(stagger)
        .append(buckle)
        .append(propeller)
        .append(opening)
        .toHashCode();
  }

  @Override
  public String toString() {
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
