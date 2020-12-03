package pl.poznan.put.structure;

import org.immutables.value.Value;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.InteractionType;

/** A base pair which is classified and quantified with numerical parameters. */
@Value.Immutable
public abstract class QuantifiedBasePair implements ClassifiedBasePair {
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

  @Value.Default
  @Value.Auxiliary
  public boolean isRepresented() {
    return ClassifiedBasePair.super.isRepresented();
  }

  @Override
  public ClassifiedBasePair invert() {
    return ImmutableQuantifiedBasePair.copyOf(this)
        .withBasePair(basePair().invert())
        .withInteractionType(interactionType().invert())
        .withLeontisWesthof(leontisWesthof().invert());
  }

  /** @return The value of shear parameter. */
  @Value.Parameter(order = 2)
  @Value.Auxiliary
  public abstract double shear();

  /** @return The value of stretch parameter. */
  @Value.Parameter(order = 3)
  @Value.Auxiliary
  public abstract double stretch();

  /** @return The value of stagger parameter. */
  @Value.Parameter(order = 4)
  @Value.Auxiliary
  public abstract double stagger();

  /** @return The value of buckle parameter. */
  @Value.Parameter(order = 5)
  @Value.Auxiliary
  public abstract double buckle();

  /** @return The value of propeller parameter. */
  @Value.Parameter(order = 6)
  @Value.Auxiliary
  public abstract double propeller();

  /** @return The value of opening parameter. */
  @Value.Parameter(order = 7)
  @Value.Auxiliary
  public abstract double opening();
}
