package pl.poznan.put.structure.secondary;

import org.immutables.value.Value;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.RNAInteractionType;

@Value.Modifiable
public abstract class AnalyzedBasePair implements ClassifiedBasePair {
  public static ModifiableAnalyzedBasePair assumeCanonical(final BasePair pair) {
    return ModifiableAnalyzedBasePair.create(
        pair,
        RNAInteractionType.BASE_BASE,
        Saenger.assumeCanonical(pair),
        LeontisWesthof.CWW,
        BPh.UNKNOWN,
        BR.UNKNOWN,
        HelixOrigin.UNKNOWN,
        false);
  }

  @Value.Parameter(order = 1)
  public abstract BasePair basePair();

  @Value.Parameter(order = 2)
  public abstract RNAInteractionType interactionType();

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
  public final ClassifiedBasePair invert() {
    return ModifiableAnalyzedBasePair.create(
        basePair().invert(),
        interactionType().invert(),
        saenger(),
        leontisWesthof().invert(),
        bph(),
        br(),
        helixOrigin(),
        isRepresented());
  }

  // required for Spring to get "isRepresented" field
  public final Boolean getIsRepresented() {
    return isRepresented();
  }
}
