package pl.poznan.put.structure.secondary.formats;

import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CombinedStrand {
    private final List<Strand> strands;

    public CombinedStrand(final List<Strand> strands) {
        super();
        this.strands = new ArrayList<>(strands);
    }

    public Iterable<Strand> getStrands() {
        return Collections.unmodifiableList(strands);
    }

    public int getLength() {
        int length = 0;
        for (final Strand strand : strands) {
            length += strand.getLength();
        }
        return length;
    }

    public List<DotBracketSymbol> getSymbols() {
        final List<DotBracketSymbol> result = new ArrayList<>();
        for (final Strand strand : strands) {
            result.addAll(strand.getSymbols());
        }
        return result;
    }

    public Iterable<TerminalMissing> getTerminalMissing() {
        final Collection<TerminalMissing> result = new ArrayList<>();
        for (final Strand strand : strands) {
            result.add(strand.getMissingBegin());
            result.add(strand.getMissingEnd());
        }
        return result;
    }

    public List<DotBracketSymbol> getInternalMissing() {
        final List<DotBracketSymbol> result = new ArrayList<>();

        for (final Strand strand : strands) {
            final TerminalMissing missingBegin = strand.getMissingBegin();
            final TerminalMissing missingEnd = strand.getMissingEnd();
            final List<DotBracketSymbol> symbols = strand.getSymbols();

            DotBracketSymbol symbol =
                    (missingBegin.getLength() > 0) ? missingBegin.getLast()
                                                                 .getNext()
                                                   : symbols.get(0);
            final DotBracketSymbol lastSymbol =
                    (missingEnd.getLength() > 0) ? missingEnd.getFirst()
                                                 : symbols
                            .get(symbols.size() - 1);

            while ((symbol != null) && !Objects.equals(symbol, lastSymbol)) {
                if (symbol.isMissing()) {
                    result.add(symbol);
                }
                symbol = symbol.getNext();
            }
        }

        return result;
    }

    public int getPseudoknotOrder() {
        int order = 0;
        for (final Strand strand : strands) {
            order = Math.max(order, strand.getPseudoknotOrder());
        }
        return order;
    }

    public boolean contains(final DotBracketSymbol symbol) {
        for (final Strand strand : strands) {
            if (strand.contains(symbol)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (final Strand strand : strands) {
            builder.append(strand.getName());
        }

        return ">strand_" + builder + '\n' + getSequence() + '\n'
               + getStructure();
    }

    public String getSequence() {
        final StringBuilder builder = new StringBuilder();
        for (final Strand strand : strands) {
            builder.append(strand.getSequence());
        }
        return builder.toString();
    }

    public String getStructure() {
        final StringBuilder builder = new StringBuilder();
        for (final Strand strand : strands) {
            builder.append(strand.getStructure());
        }
        return builder.toString();
    }

    /**
     * Check if the strand is invalid i.e. if it contains ONLY dots and minuses
     * (no base-pairs).
     *
     * @return True if the strand contains only dots or minuses.
     */
    public boolean isInvalid() {
        for (final Strand strand : strands) {
            for (final char c : strand.getStructure().toCharArray()) {
                if ((c != '.') && (c != '-')) {
                    return false;
                }
            }
        }

        return true;
    }
}
