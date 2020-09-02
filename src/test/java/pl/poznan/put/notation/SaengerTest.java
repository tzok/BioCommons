package pl.poznan.put.notation;

import org.junit.Test;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.secondary.BasePair;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SaengerTest {
  @Test
  public final void fromOrdinal() {
    assertThat(Saenger.fromOrdinal(20), is(Saenger.XX));
  }

  @Test
  public final void assumeCanonical() {
    final PdbNamedResidueIdentifier guanine = ImmutablePdbNamedResidueIdentifier.of("A", 1, " ", 'G');
    final PdbNamedResidueIdentifier adenine = ImmutablePdbNamedResidueIdentifier.of("A", 2, " ", 'A');
    final PdbNamedResidueIdentifier cytosine = ImmutablePdbNamedResidueIdentifier.of("A", 3, " ", 'C');
    final PdbNamedResidueIdentifier uracil = ImmutablePdbNamedResidueIdentifier.of("A", 4, " ", 'U');

    assertThat(Saenger.assumeCanonical(new BasePair(guanine, cytosine)), is(Saenger.XIX));
    assertThat(Saenger.assumeCanonical(new BasePair(adenine, uracil)), is(Saenger.XX));
    assertThat(Saenger.assumeCanonical(new BasePair(guanine, uracil)), is(Saenger.XXVIII));
    assertThat(Saenger.assumeCanonical(new BasePair(guanine, guanine)), is(Saenger.UNKNOWN));
  }
}
