package pl.poznan.put.structure.secondary.formats;

import org.junit.Test;
import pl.poznan.put.structure.secondary.DotBracketSymbol;
import pl.poznan.put.structure.secondary.ModifiableDotBracketSymbol;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CombinedStrandTest {
  @Test
  public final void getInternalMissingOneStrand() {
    final DefaultDotBracket dotBracket =
        DefaultDotBracket.fromString(">strand_A\nACGUACGUACGU\n.((------)).");
    final CombinedStrand combinedStrand =
        ImmutableCombinedStrand.of(
            Collections.singletonList(
                ImmutableStrandView.of("", dotBracket, 0, dotBracket.length())));
    final List<DotBracketSymbol> internalMissing = combinedStrand.getInternalMissing();
    assertThat(internalMissing.size(), is(6));
  }

  @Test
  public final void getInternalMissingTwoStrands() {
    final Strand strandFirst =
        ImmutableStrandDirect.of(
            "A",
            Arrays.asList(
                ModifiableDotBracketSymbol.create('A', '.', 1),
                ModifiableDotBracketSymbol.create('A', '-', 2)));
    final Strand strandSecond =
        ImmutableStrandDirect.of(
            "B",
            Arrays.asList(
                ModifiableDotBracketSymbol.create('A', '-', 1),
                ModifiableDotBracketSymbol.create('B', '.', 2)));

    // each strand has 0 internal missing residues
    final CombinedStrand combinedFirst =
        ImmutableCombinedStrand.of(Collections.singletonList(strandFirst));
    assertThat(combinedFirst.getInternalMissing().size(), is(0));
    final CombinedStrand combinedSecond =
        ImmutableCombinedStrand.of(Collections.singletonList(strandSecond));
    assertThat(combinedSecond.getInternalMissing().size(), is(0));

    // combined, the strands still have 0 internal missing residues, even though they form ".--."
    // structure
    final CombinedStrand combinedBoth =
        ImmutableCombinedStrand.of(Arrays.asList(strandFirst, strandSecond));
    assertThat(combinedBoth.getInternalMissing().size(), is(0));
  }
}
