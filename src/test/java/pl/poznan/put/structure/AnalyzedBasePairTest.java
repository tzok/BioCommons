package pl.poznan.put.structure;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.notation.StackingTopology;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.rna.InteractionType;

public class AnalyzedBasePairTest {
  public void defaultValues() {
    final var left = ImmutablePdbNamedResidueIdentifier.of("A", 1, Optional.empty(), 'A');
    final var right = ImmutablePdbNamedResidueIdentifier.of("A", 10, Optional.empty(), 'U');
    final var basePair = ImmutableBasePair.builder().left(left).right(right).build();
    final var analyzedBasePair = ImmutableAnalyzedBasePair.builder().basePair(basePair).build();

    assertThat(analyzedBasePair.isPairing(), is(true));
    assertThat(analyzedBasePair.saenger(), is(Saenger.XX));
    assertThat(analyzedBasePair.leontisWesthof(), is(LeontisWesthof.CWW));
  }

  public void invert() {
    final var left = ImmutablePdbNamedResidueIdentifier.of("A", 1, Optional.empty(), 'A');
    final var right = ImmutablePdbNamedResidueIdentifier.of("A", 10, Optional.empty(), 'U');
    final var basePair = ImmutableBasePair.builder().left(left).right(right).build();
    final var analyzedBasePair =
        ImmutableAnalyzedBasePair.builder()
            .basePair(basePair)
            .interactionType(InteractionType.STACKING)
            .stackingTopology(StackingTopology.UPWARD)
            .build();

    assertThat(analyzedBasePair.stackingTopology(), is(StackingTopology.UPWARD));
    assertThat(analyzedBasePair.invert().stackingTopology(), is(StackingTopology.DOWNWARD));
  }
}
