package pl.poznan.put.structure.secondary.formats;

import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

public class CombinedStrandTest {

  @Test
  public final void getInternalMissing() throws InvalidStructureException {
    final DotBracket dotBracket = DotBracket.fromString(">strand_A\nACGUACGUACGU\n.((------)).");
    final CombinedStrand combinedStrand =
        new CombinedStrand(
            Collections.singletonList(new StrandView("", dotBracket, 0, dotBracket.getLength())));
    final List<DotBracketSymbol> internalMissing = combinedStrand.getInternalMissing();
    Assert.assertEquals(6, internalMissing.size());
  }
}
