package pl.poznan.put.notation;

import org.junit.Test;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.secondary.BasePair;

import static org.junit.Assert.assertEquals;

public class SaengerTest {
  @Test
  public void fromOrdinal() {
    assertEquals(Saenger.XX, Saenger.fromOrdinal(20));
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

    assertEquals(Saenger.XIX, Saenger.assumeCanonical(new BasePair(guanine, cytosine)));
    assertEquals(Saenger.XX, Saenger.assumeCanonical(new BasePair(adenine, uracil)));
    assertEquals(Saenger.XXVIII, Saenger.assumeCanonical(new BasePair(guanine, uracil)));
    assertEquals(Saenger.UNKNOWN, Saenger.assumeCanonical(new BasePair(guanine, guanine)));
  }
}
