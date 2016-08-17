package pl.poznan.put.structure.secondary.formats;

import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CombinedStrand {
    private final List<Strand> strands;

    public CombinedStrand(List<Strand> strands) {
        super();
        this.strands = new ArrayList<>(strands);
    }

    public List<Strand> getStrands() {
        return Collections.unmodifiableList(strands);
    }

    public int getLength() {
        int length = 0;
        for (Strand strand : strands) {
            length += strand.getLength();
        }
        return length;
    }

    public List<DotBracketSymbol> getSymbols() {
        List<DotBracketSymbol> result = new ArrayList<>();
        for (Strand strand : strands) {
            result.addAll(strand.getSymbols());
        }
        return result;
    }

    public List<TerminalMissing> getTerminalMissing() {
        List<TerminalMissing> result = new ArrayList<>();
        for (Strand strand : strands) {
            result.add(strand.getMissingBegin());
            result.add(strand.getMissingEnd());
        }
        return result;
    }

    public List<DotBracketSymbol> getInternalMissing() {
        List<DotBracketSymbol> result = new ArrayList<>();

        for (Strand strand : strands) {
            TerminalMissing missingBegin = strand.getMissingBegin();
            TerminalMissing missingEnd = strand.getMissingEnd();
            List<DotBracketSymbol> symbols = strand.getSymbols();

            DotBracketSymbol symbol =
                    missingBegin.getLength() > 0 ? missingBegin.getLast()
                                                               .getNext()
                                                 : symbols.get(0);
            DotBracketSymbol lastSymbol =
                    missingEnd.getLength() > 0 ? missingEnd.getFirst() : symbols
                            .get(symbols.size() - 1);

            while (symbol != null && !symbol.equals(lastSymbol)) {
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
        for (Strand strand : strands) {
            order = Math.max(order, strand.getPseudoknotOrder());
        }
        return order;
    }

    public boolean contains(DotBracketSymbol symbol) {
        for (Strand strand : strands) {
            if (strand.contains(symbol)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Strand strand : strands) {
            builder.append(strand.getName());
        }

        return ">strand_" + builder.toString() + "\n" + getSequence() + "\n"
               + getStructure();
    }

    public String getSequence() {
        StringBuilder builder = new StringBuilder();
        for (Strand strand : strands) {
            builder.append(strand.getSequence());
        }
        return builder.toString();
    }

    public String getStructure() {
        StringBuilder builder = new StringBuilder();
        for (Strand strand : strands) {
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
        for (Strand strand : strands) {
            for (char c : strand.getStructure().toCharArray()) {
                if (c != '.' && c != '-') {
                    return false;
                }
            }
        }

        return true;
    }
}
