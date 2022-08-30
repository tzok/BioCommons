package pl.poznan.put.structure.formats;

import java.util.List;
import org.immutables.value.Value;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.DotBracketSymbol;

/** A strand which is defined as a fragment of a dot-bracket structure. */
@Value.Immutable
public abstract class StrandView implements Strand {
  @Override
  @Value.Parameter(order = 1)
  public abstract String name();

  @Override
  public final String description() {
    if (parent() instanceof DotBracketFromPdb) {
      final DotBracketFromPdb fromPdb = (DotBracketFromPdb) parent();
      final PdbResidueIdentifier from = fromPdb.identifier(fromPdb.symbols().get(begin()));
      final PdbResidueIdentifier to = fromPdb.identifier(fromPdb.symbols().get(end() - 1));
      return String.format("%s %s %s %s %s", from, to, sequence(), structure(), sequenceRY());
    }

    return String.format(
        "%d %d %s %s %s", begin() + 1, end(), sequence(), structure(), sequenceRY());
  }

  @Override
  @Value.Parameter(order = 3)
  public abstract int begin();

  @Override
  @Value.Parameter(order = 4)
  public abstract int end();

  /**
   * @return The parent dot-bracket structure of this strand.
   */
  @Value.Parameter(order = 2)
  public abstract DotBracket parent();

  @Override
  @Value.Lazy
  public List<DotBracketSymbol> symbols() {
    return parent().symbols().subList(begin(), end());
  }

  @Override
  public final String toString() {
    return String.format(">strand_%s\n%s\n%s", name(), sequence(), structure());
  }
}
