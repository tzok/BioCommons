package pl.poznan.put.structure.secondary;

import org.immutables.value.Value;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.InteractionType;

@Value.Modifiable
public abstract class QuantifiedBasePair implements ClassifiedBasePair {
  @Value.Parameter(order = 1)
  public abstract BasePair basePair();

  @Value.Parameter(order = 2)
  public abstract InteractionType interactionType();

  @Value.Parameter(order = 3)
  public abstract Saenger saenger();

  @Value.Parameter(order = 4)
  public abstract LeontisWesthof leontisWesthof();

  @Value.Parameter(order = 5)
  public abstract BPh bph();

  @Value.Parameter(order = 6)
  public abstract BR br();

  @Value.Parameter(order = 7)
  @Value.Auxiliary
  public abstract HelixOrigin helixOrigin();

  @Value.Parameter(order = 8)
  @Value.Auxiliary
  public abstract boolean isRepresented();

  @Override
  public ClassifiedBasePair invert() {
    return ModifiableQuantifiedBasePair.create(
        basePair().invert(),
        interactionType().invert(),
        saenger(),
        leontisWesthof().invert(),
        bph(),
        br(),
        helixOrigin(),
        isRepresented(),
        shear(),
        stretch(),
        stagger(),
        buckle(),
        propeller(),
        opening());
  }

  @Value.Parameter(order = 9)
  @Value.Auxiliary
  public abstract double shear();

  @Value.Parameter(order = 10)
  @Value.Auxiliary
  public abstract double stretch();

  @Value.Parameter(order = 11)
  @Value.Auxiliary
  public abstract double stagger();

  @Value.Parameter(order = 12)
  @Value.Auxiliary
  public abstract double buckle();

  @Value.Parameter(order = 13)
  @Value.Auxiliary
  public abstract double propeller();

  @Value.Parameter(order = 14)
  @Value.Auxiliary
  public abstract double opening();
}
