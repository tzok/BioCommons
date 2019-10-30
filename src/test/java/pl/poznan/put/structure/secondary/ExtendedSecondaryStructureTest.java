package pl.poznan.put.structure.secondary;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;
import org.junit.Test;

public class ExtendedSecondaryStructureTest {
    // @formatter:on

  @Test
  public final void simple() {
      final String simple = "seq ACGUACGUACGU\n" + "cWW ....((..))..\n" + "cWH (([[..))]]..\n";
      final ExtendedSecondaryStructure secondaryStructure =
        ExtendedSecondaryStructure.fromMultilineDotBracket(simple);
    assertThat(secondaryStructure.toString(), is(simple));

    final String sequence = secondaryStructure.getSequence();
    assertThat(sequence, is("ACGUACGUACGU"));

    final Collection<ClassifiedBasePair> basePairs = secondaryStructure.getBasePairs();
    assertThat(basePairs.size(), is(6));
  }

  @Test
  public final void quadruplex() {
      // @formatter:off
      final String quadruplex = "seq uAGGGUUAGGGUuAGGGUUAGGGU\n"
              + "cWH ..([{...)]}...([{...)]}.\n"
              + "cWH ........([{...)]}.......\n"
              + "cHW ..([{...............)]}.\n";
      final ExtendedSecondaryStructure secondaryStructure =
        ExtendedSecondaryStructure.fromMultilineDotBracket(quadruplex);
    assertThat(secondaryStructure.toString(), is(quadruplex));

    final String sequence = secondaryStructure.getSequence();
    assertThat(sequence, is("uAGGGUUAGGGUuAGGGUUAGGGU"));

    final Collection<ClassifiedBasePair> basePairs = secondaryStructure.getBasePairs();
    assertThat(basePairs.size(), is(12));
  }
}
