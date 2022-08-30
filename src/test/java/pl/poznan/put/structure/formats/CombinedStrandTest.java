package pl.poznan.put.structure.formats;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ImmutableDotBracketSymbol;

public class CombinedStrandTest {
  @Test
  public final void getInternalMissingOneStrand() {
    final DefaultDotBracket dotBracket =
        DefaultDotBracket.fromString(">strand_A\nACGUACGUACGU\n.((------)).");
    final CombinedStrand combinedStrand =
        ImmutableCombinedStrand.of(
            Collections.singletonList(
                ImmutableStrandView.of("", dotBracket, 0, dotBracket.length())));
    final List<DotBracketSymbol> internalMissing = combinedStrand.missingInternal();
    assertThat(internalMissing.size(), is(6));
  }

  @Test
  public final void getInternalMissingTwoStrands() {
    final Strand strandFirst =
        ImmutableDefaultStrand.of(
            "A",
            Arrays.asList(
                ImmutableDotBracketSymbol.of('A', '.', 1),
                ImmutableDotBracketSymbol.of('A', '-', 2)));
    final Strand strandSecond =
        ImmutableDefaultStrand.of(
            "B",
            Arrays.asList(
                ImmutableDotBracketSymbol.of('A', '-', 1),
                ImmutableDotBracketSymbol.of('B', '.', 2)));

    // each strand has 0 internal missing residues
    final CombinedStrand combinedFirst =
        ImmutableCombinedStrand.of(Collections.singletonList(strandFirst));
    assertThat(combinedFirst.missingInternal().size(), is(0));
    final CombinedStrand combinedSecond =
        ImmutableCombinedStrand.of(Collections.singletonList(strandSecond));
    assertThat(combinedSecond.missingInternal().size(), is(0));

    // combined, the strands still have 0 internal missing residues, even though they form ".--."
    // structure
    final CombinedStrand combinedBoth =
        ImmutableCombinedStrand.of(Arrays.asList(strandFirst, strandSecond));
    assertThat(combinedBoth.missingInternal().size(), is(0));
  }
}
