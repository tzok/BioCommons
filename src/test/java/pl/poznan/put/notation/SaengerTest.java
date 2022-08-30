package pl.poznan.put.notation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;
import org.junit.Test;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.ImmutableBasePair;

public class SaengerTest {
  @Test
  public final void fromOrdinal() {
    assertThat(Saenger.fromNumber(20), is(Saenger.XX));
  }

  @Test
  public final void assumeCanonical() {
    final PdbNamedResidueIdentifier guanine =
        ImmutablePdbNamedResidueIdentifier.of("A", 1, Optional.empty(), 'G');
    final PdbNamedResidueIdentifier adenine =
        ImmutablePdbNamedResidueIdentifier.of("A", 2, Optional.empty(), 'A');
    final PdbNamedResidueIdentifier cytosine =
        ImmutablePdbNamedResidueIdentifier.of("A", 3, Optional.empty(), 'C');
    final PdbNamedResidueIdentifier uracil =
        ImmutablePdbNamedResidueIdentifier.of("A", 4, Optional.empty(), 'U');

    assertThat(Saenger.assumeCanonical(ImmutableBasePair.of(guanine, cytosine)), is(Saenger.XIX));
    assertThat(Saenger.assumeCanonical(ImmutableBasePair.of(adenine, uracil)), is(Saenger.XX));
    assertThat(Saenger.assumeCanonical(ImmutableBasePair.of(guanine, uracil)), is(Saenger.XXVIII));
    assertThat(
        Saenger.assumeCanonical(ImmutableBasePair.of(guanine, guanine)), is(Saenger.UNKNOWN));
  }
}
