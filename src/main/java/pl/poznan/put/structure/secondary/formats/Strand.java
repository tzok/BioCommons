package pl.poznan.put.structure.secondary.formats;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

public class Strand implements Serializable {
  private static final long serialVersionUID = 8267967642039631099L;

  private final DotBracketInterface parent;
  private final String name;
  private final int from;
  private final int to;

  public Strand(final DotBracketInterface parent, final String name, final int from, final int to) {
    super();
    this.parent = parent;
    this.name = name;
    this.from = from;
    this.to = to;

    assert to >= from;
  }

  @Override
  public final String toString() {
    return String.format(">%s\n%s\n%s", name, getSequence(), getStructure());
  }

  public String getSequence() {
    return parent.getSequence().substring(from, to);
  }

  public String getStructure() {
    return parent.getStructure().substring(from, to);
  }

  public final String getName() {
    return name;
  }

  public final int getFrom() {
    return from;
  }

  public final int getTo() {
    return to;
  }

  public final int getLength() {
    return to - from;
  }

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

  public final int getPseudoknotOrder() {
    int order = 0;
    for (final DotBracketSymbol symbol : getSymbols()) {
      order = Math.max(order, symbol.getOrder());
    }
    return order;
  }

  public final List<DotBracketSymbol> getSymbols() {
    return parent.getSymbols().subList(from, to);
  }

  public final boolean contains(final DotBracketSymbol symbol) {
    return getSymbols().contains(symbol);
  }

  /**
   * Check if this strand is "single strand" which means that it does not have any base-pair
   * embedded inside its structure.
   *
   * @return True if there is no base-pair inside of this strand. An opening or closing bracket is
   *     allowed as long as it points somewhere outside this strand.
   */
  public final boolean isSingleStrand() {
    final List<DotBracketSymbol> symbols = getSymbols();

    for (int i = 1; i < (symbols.size() - 1); i++) {
      final DotBracketSymbol symbol = symbols.get(i);
      if (symbol.isPairing() && symbols.contains(symbol.getPair())) {
        return false;
      }
    }

    return true;
  }

  public final boolean containsMissing() {
    for (final DotBracketSymbol symbol : getSymbols()) {
      if (symbol.isMissing()) {
        return true;
      }
    }

    return false;
  }

  public final boolean containsFully(final Strand other) {
    return (from <= other.from) && (to >= other.to);
  }

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

  public final String getRSequence() {
    final char[] cs = getSequence().toCharArray();
    for (int i = 0; i < cs.length; i++) {
      cs[i] = ((cs[i] == 'A') || (cs[i] == 'G')) ? 'R' : 'Y';
    }
    return new String(cs);
  }

  public final DotBracketInterface getParent() {
    return parent;
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    final Strand strand = (Strand) o;
    return (from == strand.from) && (to == strand.to) && Objects.equals(parent, strand.parent);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(parent, from, to);
  }

  public final int indexOfSymbol(final DotBracketSymbol symbol) {
    return getSymbols().indexOf(symbol);
  }
}
