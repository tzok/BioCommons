package pl.poznan.put.structure.secondary.formats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

@EqualsAndHashCode
public class CombinedStrand implements DotBracketInterface {
  private final List<Strand> strands;
  protected final List<DotBracketSymbol> symbols;

  public CombinedStrand(final List<Strand> strands) {
    super();
    this.strands = new ArrayList<>(strands);
    symbols = CombinedStrand.reindexSymbols(strands);
  }

  private static List<DotBracketSymbol> reindexSymbols(final Iterable<Strand> strands) {
    final Map<DotBracketSymbol, Integer> symbolToIndex = new HashMap<>();
    int i = 0;
    for (final Strand strand : strands) {
      for (final DotBracketSymbol symbol : strand.getSymbols()) {
        symbolToIndex.put(symbol, i);
        i += 1;
      }
    }

    final List<DotBracketSymbol> symbols = new ArrayList<>();
    for (final Strand strand : strands) {
      for (final DotBracketSymbol symbol : strand.getSymbols()) {
        final char sequence = symbol.getSequence();
        final char structure = symbol.getStructure();
        final int index = symbolToIndex.get(symbol);
        final DotBracketSymbol renumbered = new DotBracketSymbol(sequence, structure, index);
        symbols.add(renumbered);
      }
    }

    for (final Strand strand : strands) {
      for (final DotBracketSymbol symbol : strand.getSymbols()) {
        if (symbol.isPairing()) {
          final DotBracketSymbol u = symbols.get(symbolToIndex.get(symbol));
          final DotBracketSymbol v = symbols.get(symbolToIndex.get(symbol.getPair()));
          u.setPair(v);
          v.setPair(u);
        }
      }
    }

    return symbols;
  }

  public final List<Strand> getStrands() {
    return Collections.unmodifiableList(strands);
  }

  public final int getLength() {
    int length = 0;
    for (final Strand strand : strands) {
      length += strand.getLength();
    }
    return length;
  }

  @Override
  public final List<DotBracketSymbol> getSymbols() {
    return Collections.unmodifiableList(symbols);
  }

  @Override
  public final DotBracketSymbol getSymbol(final int index) {
    return symbols.get(index);
  }

  public final Iterable<TerminalMissing> getTerminalMissing() {
    final Collection<TerminalMissing> result = new ArrayList<>();
    for (final Strand strand : strands) {
      result.add(strand.getMissingBegin());
      result.add(strand.getMissingEnd());
    }
    return result;
  }

  public final List<DotBracketSymbol> getInternalMissing() {
    final List<DotBracketSymbol> result = new ArrayList<>();

    for (final Strand strand : strands) {
      final TerminalMissing missingBegin = strand.getMissingBegin();
      final TerminalMissing missingEnd = strand.getMissingEnd();
      final List<DotBracketSymbol> symbols = strand.getSymbols();

      DotBracketSymbol symbol =
          (missingBegin.getLength() > 0) ? missingBegin.getLast().getNext() : symbols.get(0);
      final DotBracketSymbol lastSymbol =
          (missingEnd.getLength() > 0) ? missingEnd.getFirst() : symbols.get(symbols.size() - 1);

      while ((symbol != null) && !Objects.equals(symbol, lastSymbol)) {
        if (symbol.isMissing()) {
          result.add(symbol);
        }
        symbol = symbol.getNext();
      }
    }

    return result;
  }

  public final int getPseudoknotOrder() {
    int order = 0;
    for (final Strand strand : strands) {
      order = Math.max(order, strand.getPseudoknotOrder());
    }
    return order;
  }

  public final boolean contains(final DotBracketSymbol symbol) {
    for (final Strand strand : strands) {
      if (strand.contains(symbol)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public final String toString() {
    final StringBuilder builder = new StringBuilder();

    for (final Strand strand : strands) {
      builder.append(strand.getName());
    }

    return ">strand_" + builder + '\n' + getSequence(false) + '\n' + getStructure(false);
  }

  @Override
  public final String toStringWithStrands() {
    final StringBuilder builder = new StringBuilder();
    for (final Strand strand : strands) {
      builder.append(strand);
      builder.append('\n');
    }
    return builder.toString();
  }

  @Override
  public final List<? extends CombinedStrand> combineStrands() {
    return Collections.singletonList(this);
  }

  public final String getSequence(final boolean separateStrands) {
    final StringBuilder builder = new StringBuilder();
    for (final Strand strand : strands) {
      builder.append(strand.getSequence());
      if (separateStrands) {
        builder.append('&');
      }
    }
    return builder.toString();
  }

  public final String getStructure(final boolean separateStrands) {
    final StringBuilder builder = new StringBuilder();
    for (final Strand strand : strands) {
      builder.append(strand.getStructure());
      if (separateStrands) {
        builder.append('&');
      }
    }
    return builder.toString();
  }

  @Override
  public final String getSequence() {
    return getSequence(false);
  }

  @Override
  public final String getStructure() {
    return getStructure(false);
  }

  /**
   * Check if the strand is invalid i.e. if it contains ONLY dots and minuses (no base-pairs).
   *
   * @return True if the strand contains only dots or minuses.
   */
  public final boolean isInvalid() {
    for (final Strand strand : strands) {
      for (final char c : strand.getStructure().toCharArray()) {
        if ((c != '.') && (c != '-')) {
          return false;
        }
      }
    }

    return true;
  }

  public final int indexOfSymbol(final DotBracketSymbol symbol) {
    int baseIndex = 0;
    for (final Strand strand : strands) {
      if (strand.contains(symbol)) {
        return baseIndex + strand.indexOfSymbol(symbol);
      }
      baseIndex += strand.getLength();
    }
    throw new IllegalArgumentException("Failed to find symbol " + symbol + " in strands:\n" + this);
  }
}
