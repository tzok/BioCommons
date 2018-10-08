package pl.poznan.put.structure.secondary.formats;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

public class CombinedStrandTest {
  @Test
  public final void getInternalMissingOneStrand() throws InvalidStructureException {
    final DotBracket dotBracket = DotBracket.fromString(">strand_A\nACGUACGUACGU\n.((------)).");
    final CombinedStrand combinedStrand =
        new CombinedStrand(
            Collections.singletonList(new StrandView("", dotBracket, 0, dotBracket.getLength())));
    final List<DotBracketSymbol> internalMissing = combinedStrand.getInternalMissing();
    assertEquals(6, internalMissing.size());
  }

  @Test
  public final void getInternalMissingTwoStrands() {
    final Strand strandFirst =
        new StrandDirect(
            "A",
            Arrays.asList(new DotBracketSymbol('A', '.', 1), new DotBracketSymbol('A', '-', 2)));
    final Strand strandSecond =
        new StrandDirect(
            "B",
            Arrays.asList(new DotBracketSymbol('A', '-', 1), new DotBracketSymbol('B', '.', 2)));

    // each strand has 0 internal missing residues
    final CombinedStrand combinedFirst = new CombinedStrand(Collections.singletonList(strandFirst));
    assertEquals(0, combinedFirst.getInternalMissing().size());
    final CombinedStrand combinedSecond =
        new CombinedStrand(Collections.singletonList(strandSecond));
    assertEquals(0, combinedSecond.getInternalMissing().size());

    // but combined, the strands have 2 internal missing residues, because they form ".--."
    // structure
    final CombinedStrand combinedBoth =
        new CombinedStrand(Arrays.asList(strandFirst, strandSecond));
    assertEquals(2, combinedBoth.getInternalMissing().size());
  }
}
