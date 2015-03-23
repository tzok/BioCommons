package pl.poznan.put.structure.secondary.formats;

import java.util.List;

public class TerminalMissing {
    private final List<DotBracketSymbol> symbols;

    public TerminalMissing(List<DotBracketSymbol> symbols) {
        super();
        this.symbols = symbols;
    }

    public DotBracketSymbol getFirst() {
        return symbols.size() > 0 ? symbols.get(0) : null;
    }

    public DotBracketSymbol getLast() {
        return symbols.size() > 0 ? symbols.get(symbols.size() - 1) : null;
    }

    public int getLength() {
        return symbols.size();
    }

    public boolean contains(DotBracketSymbol symbol) {
        return symbols.contains(symbol);
    }

    @Override
    public String toString() {
        return "TerminalMissing, first " + getFirst() + ", last " + getLast();
    }
}
