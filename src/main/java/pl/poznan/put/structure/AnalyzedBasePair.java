package pl.poznan.put.structure;

import org.immutables.value.Value;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.InteractionType;

/** A pair of residues with metadata taken from analysis tool. */
@Value.Modifiable
public abstract class AnalyzedBasePair implements ClassifiedBasePair {
  @Value.Parameter(order = 1)
  public abstract BasePair basePair();

  @Value.Default
  public InteractionType interactionType() {
    return ClassifiedBasePair.super.interactionType();
  }

  @Value.Default
  public Saenger saenger() {
    return ClassifiedBasePair.super.saenger();
  }

  @Value.Default
  public LeontisWesthof leontisWesthof() {
    return ClassifiedBasePair.super.leontisWesthof();
  }

  @Value.Default
  public BPh bph() {
    return ClassifiedBasePair.super.bph();
  }

  @Value.Default
  public BR br() {
    return ClassifiedBasePair.super.br();
  }

  @Value.Auxiliary
  @Value.Default
  public HelixOrigin helixOrigin() {
    return ClassifiedBasePair.super.helixOrigin();
  }

  @Value.Auxiliary
  @Value.Default
  public boolean isRepresented() {
    return ClassifiedBasePair.super.isRepresented();
  }

  @Override
  public final ClassifiedBasePair invert() {
    return ModifiableAnalyzedBasePair.create()
        .from(this)
        .setBasePair(basePair().invert())
        .setInteractionType(interactionType().invert())
        .setLeontisWesthof(leontisWesthof().invert());
  }

  /**
   * Returns the value of {@code isRepresented()}, but this naming is required by Spring.
   *
   * @return The value of isRepresented();
   */
  public final Boolean getIsRepresented() {
    return isRepresented();
  }
}
