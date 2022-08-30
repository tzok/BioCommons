package pl.poznan.put.structure;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;
import org.junit.Test;
import pl.poznan.put.structure.formats.MultiLineDotBracket;

public class MultiLineDotBracketTest {
  // @formatter:on

  @Test
  public final void simple() {
    final String simple = "seq ACGUACGUACGU\n" + "cWW ....((..))..\n" + "cWH (([[..))]]..\n";
    final MultiLineDotBracket secondaryStructure = MultiLineDotBracket.fromString(simple);
    assertThat(secondaryStructure.toString(), is(simple));

    final String sequence = secondaryStructure.sequence();
    assertThat(sequence, is("ACGUACGUACGU"));

    final Collection<? extends ClassifiedBasePair> basePairs = secondaryStructure.basePairs();
    assertThat(basePairs.size(), is(6));
  }

  @Test
  public final void quadruplex() {
    // @formatter:off
    final String quadruplex =
        "seq uAGGGUUAGGGUuAGGGUUAGGGU\n"
            + "cWH ..([{...)]}...([{...)]}.\n"
            + "cWH ........([{...)]}.......\n"
            + "cHW ..([{...............)]}.\n";
    final MultiLineDotBracket secondaryStructure = MultiLineDotBracket.fromString(quadruplex);
    assertThat(secondaryStructure.toString(), is(quadruplex));

    final String sequence = secondaryStructure.sequence();
    assertThat(sequence, is("uAGGGUUAGGGUuAGGGUUAGGGU"));

    final Collection<? extends ClassifiedBasePair> basePairs = secondaryStructure.basePairs();
    assertThat(basePairs.size(), is(12));
  }
}
