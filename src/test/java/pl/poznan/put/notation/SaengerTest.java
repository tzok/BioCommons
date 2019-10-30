package pl.poznan.put.notation;

import org.junit.Assert;
import org.junit.Test;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.secondary.BasePair;

import static org.hamcrest.Matchers.*;

public class SaengerTest {
  @Test
  public final void fromOrdinal() {
    Assert.assertThat(Saenger.fromOrdinal(20), is(Saenger.XX));
  }

  @Test
  public final void assumeCanonical() {
    final PdbResidueIdentifier guanine = new PdbResidueIdentifier("A", 1, null);
    guanine.setResidueOneLetterName('G');
    final PdbResidueIdentifier adenine = new PdbResidueIdentifier("A", 2, null);
    adenine.setResidueOneLetterName('A');
    final PdbResidueIdentifier cytosine = new PdbResidueIdentifier("A", 3, null);
    cytosine.setResidueOneLetterName('C');
    final PdbResidueIdentifier uracil = new PdbResidueIdentifier("A", 4, null);
    uracil.setResidueOneLetterName('U');

    Assert.assertThat(Saenger.assumeCanonical(new BasePair(guanine, cytosine)), is(Saenger.XIX));
    Assert.assertThat(Saenger.assumeCanonical(new BasePair(adenine, uracil)), is(Saenger.XX));
    Assert.assertThat(Saenger.assumeCanonical(new BasePair(guanine, uracil)), is(Saenger.XXVIII));
    Assert.assertThat(Saenger.assumeCanonical(new BasePair(guanine, guanine)), is(Saenger.UNKNOWN));
  }
}
