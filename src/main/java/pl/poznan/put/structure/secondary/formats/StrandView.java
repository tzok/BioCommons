package pl.poznan.put.structure.secondary.formats;

import org.immutables.value.Value;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.List;

@Value.Immutable
public abstract class StrandView implements Strand {
  @Value.Parameter(order = 1)
  public abstract String name();

  @Override
  public final TerminalMissing missingBegin() {
    int i = begin();
    for (; i < end(); i++) {
      final DotBracketSymbol symbol = parent().getSymbol(i);
      if (!symbol.isMissing()) {
        break;
      }
    }
    return ImmutableTerminalMissing.of(parent().getSymbols().subList(begin(), i));
  }

  @Override
  public final TerminalMissing missingEnd() {
    int i = end() - 1;
    for (; i >= begin(); i--) {
      final DotBracketSymbol symbol = parent().getSymbol(i);
      if (!symbol.isMissing()) {
        break;
      }
    }
    return ImmutableTerminalMissing.of(parent().getSymbols().subList(i + 1, end()));
  }

  @Value.Lazy
  public List<DotBracketSymbol> symbols() {
    return parent().getSymbols().subList(begin(), end());
  }

  @Override
  public final String description() {
    if (parent() instanceof DotBracketFromPdb) {
      final DotBracketFromPdb fromPdb = (DotBracketFromPdb) parent();
      final PdbNamedResidueIdentifier fromIdentifier =
          fromPdb.getResidueIdentifier(fromPdb.getSymbol(begin()));
      final PdbNamedResidueIdentifier toIdentifier =
          fromPdb.getResidueIdentifier(fromPdb.getSymbol(end() - 1));

      return String.format(
          "%s %s %s %s %s", fromIdentifier, toIdentifier, sequence(), structure(), sequenceRY());
    }

    return String.format(
        "%d %d %s %s %s", begin() + 1, end(), sequence(), structure(), sequenceRY());
  }

  @Value.Parameter(order = 3)
  public abstract int begin();

  @Value.Parameter(order = 4)
  public abstract int end();

  @Value.Parameter(order = 2)
  public abstract DotBracketInterface parent();

  @Override
  public final String toString() {
    return String.format(">strand_%s\n%s\n%s", name(), sequence(), structure());
  }
}
