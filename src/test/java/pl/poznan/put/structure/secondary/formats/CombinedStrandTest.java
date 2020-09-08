package pl.poznan.put.structure.secondary.formats;

import org.junit.Test;
import pl.poznan.put.structure.secondary.DotBracketSymbol;
import pl.poznan.put.structure.secondary.ImmutableDotBracketSymbol;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CombinedStrandTest {
  @Test
  public final void getInternalMissingOneStrand() {
    final DotBracket dotBracket = DotBracket.fromString(">strand_A\nACGUACGUACGU\n.((------)).");
    final CombinedStrand combinedStrand =
        new CombinedStrand(
            Collections.singletonList(new StrandView("", dotBracket, 0, dotBracket.getLength())));
    final List<DotBracketSymbol> internalMissing = combinedStrand.getInternalMissing();
    assertThat(internalMissing.size(), is(6));
  }

  @Test
  public final void getInternalMissingTwoStrands() {
    final Strand strandFirst =
        new StrandDirect(
            "A",
            Arrays.asList(
                ImmutableDotBracketSymbol.of('A', '.', 1),
                ImmutableDotBracketSymbol.of('A', '-', 2)));
    final Strand strandSecond =
        new StrandDirect(
            "B",
            Arrays.asList(
                ImmutableDotBracketSymbol.of('A', '-', 1),
                ImmutableDotBracketSymbol.of('B', '.', 2)));

    // each strand has 0 internal missing residues
    final CombinedStrand combinedFirst = new CombinedStrand(Collections.singletonList(strandFirst));
    assertThat(combinedFirst.getInternalMissing().size(), is(0));
    final CombinedStrand combinedSecond =
        new CombinedStrand(Collections.singletonList(strandSecond));
    assertThat(combinedSecond.getInternalMissing().size(), is(0));

    // combined, the strands still have 0 internal missing residues, even though they form ".--."
    // structure
    final CombinedStrand combinedBoth =
        new CombinedStrand(Arrays.asList(strandFirst, strandSecond));
    assertThat(combinedBoth.getInternalMissing().size(), is(0));
  }
}
