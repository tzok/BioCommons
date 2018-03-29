package pl.poznan.put.structure.secondary.formats;

import java.util.List;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

public class StrandView extends AbstractStrand {
  private final DotBracketInterface parent;
  private final int from;
  private final int to;

  public StrandView(
      final String name, final DotBracketInterface parent, final int from, final int to) {
    super(name);
    this.parent = parent;
    this.from = from;
    this.to = to;
  }

  public final DotBracketInterface getParent() {
    return parent;
  }

  @Override
  public final int getFrom() {
    return from;
  }

  @Override
  public final int getTo() {
    return to;
  }

  @Override
  public final List<DotBracketSymbol> getSymbols() {
    return parent.getSymbols().subList(from, to);
  }

  @Override
  public final TerminalMissing getMissingBegin() {
    int i = from;
    for (; i < to; i++) {
      final DotBracketSymbol symbol = parent.getSymbol(i);
      if (!symbol.isMissing()) {
        break;
      }
    }
    return new TerminalMissing(parent.getSymbols().subList(from, i));
  }

  @Override
  public final TerminalMissing getMissingEnd() {
    int i = to - 1;
    for (; i >= from; i--) {
      final DotBracketSymbol symbol = parent.getSymbol(i);
      if (!symbol.isMissing()) {
        break;
      }
    }
    return new TerminalMissing(parent.getSymbols().subList(i + 1, to));
  }

  @Override
  public final String getDescription() {
    if (parent instanceof DotBracketFromPdb) {
      final DotBracketFromPdb fromPdb = (DotBracketFromPdb) parent;
      final PdbResidueIdentifier fromIdentifier =
          fromPdb.getResidueIdentifier(fromPdb.getSymbol(from));
      final PdbResidueIdentifier toIdentifier =
          fromPdb.getResidueIdentifier(fromPdb.getSymbol(to - 1));

      return String.format(
          "%s %s %s %s %s",
          fromIdentifier, toIdentifier, getSequence(), getStructure(), getRSequence());
    }

    return String.format(
        "%d %d %s %s %s", from + 1, to, getSequence(), getStructure(), getRSequence());
  }
}
