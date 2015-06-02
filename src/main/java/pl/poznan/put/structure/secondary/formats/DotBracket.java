package pl.poznan.put.structure.secondary.formats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.poznan.put.structure.secondary.DotBracketSymbol;

public class DotBracket implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DotBracket.class);
    private static final Pattern DOTBRACKET_PATTERN = Pattern.compile(">(strand_)?(.+)\n([ACGUTRYNacgutryn]+)\n([-.()\\[\\]{}<>A-Za-z]+)");

    /*
     * This is just a simple and naive implementation. For a robust solution, go
     * check RNApdbee http://rnapdbee.cs.put.poznan.pl
     */
    public static DotBracket fromBpSeq(BpSeq bpSeq) {
        // FIXME
        return null;
    }

    public static DotBracket fromString(String data) throws InvalidSecondaryStructureException {
        List<Pair<Integer, Integer>> pairBeginEnd = new ArrayList<Pair<Integer, Integer>>();
        List<String> strandNames = new ArrayList<String>();
        StringBuilder sequenceBuilder = new StringBuilder();
        StringBuilder structureBuilder = new StringBuilder();
        int begin = 0;
        int end = 0;

        Matcher matcher = DotBracket.DOTBRACKET_PATTERN.matcher(data);

        while (matcher.find()) {
            String strandName = matcher.group(2);
            String sequence = matcher.group(3);
            String structure = matcher.group(4);

            if (sequence.length() != structure.length()) {
                throw new InvalidSecondaryStructureException("Invalid dot-bracket string:\n" + data);
            }

            strandNames.add(strandName);
            sequenceBuilder.append(sequence);
            structureBuilder.append(structure);

            end += sequence.length();
            pairBeginEnd.add(Pair.of(begin, end));
            begin = end;
        }

        DotBracket dotBracket = new DotBracket(sequenceBuilder.toString(), structureBuilder.toString());
        dotBracket.strands.clear();

        int index = 0;
        for (Pair<Integer, Integer> pair : pairBeginEnd) {
            dotBracket.strands.add(new Strand(dotBracket, strandNames.get(index), pair.getLeft(), pair.getRight()));
            index += 1;
        }

        return dotBracket;
    }

    // FIXME
    protected final List<Strand> strands = new ArrayList<Strand>();

    protected final List<DotBracketSymbol> symbols = new ArrayList<DotBracketSymbol>();
    protected final String sequence;
    protected final String structure;

    public DotBracket(String sequence, String structure) throws InvalidSecondaryStructureException {
        super();
        this.sequence = sequence;
        this.structure = structure;

        if (!sequence.matches("[ACGUTRYNacgutryn]+") || !structure.matches("[-.()\\[\\]{}<>A-Za-z]+")) {
            throw new InvalidSecondaryStructureException("Invalid dot-bracket:\n" + sequence + "\n" + structure);
        }

        buildSymbolList();
        analyzePairing();

        strands.add(new Strand(this, "", 0, structure.length()));
    }

    private void buildSymbolList() {
        char[] seq = sequence.toCharArray();
        char[] str = structure.toCharArray();
        assert seq.length == str.length;

        DotBracketSymbol current = new DotBracketSymbol(seq[0], str[0], 0);

        for (int i = 1; i < seq.length; i++) {
            DotBracketSymbol next = new DotBracketSymbol(seq[i], str[i], i);
            current.setNext(next);
            next.setPrevious(current);
            symbols.add(current);
            current = next;
        }
        symbols.add(current);

        assert symbols.size() == seq.length;
    }

    private void analyzePairing() throws InvalidSecondaryStructureException {
        BidiMap<Character, Character> parentheses = new TreeBidiMap<Character, Character>();
        parentheses.put('(', ')');
        parentheses.put('[', ']');
        parentheses.put('{', '}');
        parentheses.put('<', '>');

        for (char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
            parentheses.put(c, Character.toLowerCase(c));
        }

        Map<Character, Stack<DotBracketSymbol>> parenthesesStacks = new HashMap<Character, Stack<DotBracketSymbol>>();
        for (char c : parentheses.keySet()) {
            parenthesesStacks.put(c, new Stack<DotBracketSymbol>());
        }

        for (DotBracketSymbol symbol : symbols) {
            char str = symbol.getStructure();

            // catch dot '.'
            if (str == '.' || str == '-') {
                symbol.setPair(null);
                continue;
            }

            // catch opening '(', '[', etc.
            if (parentheses.containsKey(str)) {
                Stack<DotBracketSymbol> stack = parenthesesStacks.get(str);
                stack.push(symbol);
                continue;
            }

            // catch closing ')', ']', etc.
            if (parentheses.containsValue(str)) {
                char opening = parentheses.getKey(str);
                Stack<DotBracketSymbol> stack = parenthesesStacks.get(opening);

                if (stack.empty()) {
                    throw new InvalidSecondaryStructureException("Invalid dot-bracket input:\n" + sequence + "\n" + structure);
                }

                DotBracketSymbol pair = stack.pop();
                symbol.setPair(pair);
                pair.setPair(symbol);
                continue;
            }

            DotBracket.LOGGER.error("Unknown symbol in dot-bracket string: " + str);
        }
    }

    @Override
    public String toString() {
        return ">strand\n" + sequence + "\n" + structure;
    }

    public String toStringWithStrands() {
        StringBuilder builder = new StringBuilder();
        for (Strand strand : strands) {
            builder.append(strand);
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (sequence == null ? 0 : sequence.hashCode());
        result = prime * result + (structure == null ? 0 : structure.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DotBracket other = (DotBracket) obj;
        if (sequence == null) {
            if (other.sequence != null) {
                return false;
            }
        } else if (!sequence.equals(other.sequence)) {
            return false;
        }
        if (structure == null) {
            if (other.structure != null) {
                return false;
            }
        } else if (!structure.equals(other.structure)) {
            return false;
        }
        return true;
    }

    public String getSequence() {
        return sequence;
    }

    public String getStructure() {
        return structure;
    }

    public int getLength() {
        return structure.length();
    }

    public DotBracketSymbol getSymbol(int index) {
        assert index < symbols.size();
        return symbols.get(index);
    }

    public List<DotBracketSymbol> getSymbols() {
        return Collections.unmodifiableList(symbols);
    }

    public List<Strand> getStrands() {
        return Collections.unmodifiableList(strands);
    }

    public int getStrandCount() {
        return strands.size();
    }

    public void splitStrands(Ct ct) {
        strands.clear();

        int start = 0;
        int i = 0;

        for (Ct.Entry e : ct.getEntries()) {
            if (e.getAfter() == 0) {
                Strand strand = new Strand(this, "", start, i + 1);
                strands.add(strand);
                start = i + 1;
            }

            i += 1;
        }
    }

    public List<CombinedStrand> combineStrands() {
        List<CombinedStrand> result = new ArrayList<CombinedStrand>();
        List<Strand> toCombine = new ArrayList<Strand>();
        int level = 0;

        for (Strand strand : strands) {
            toCombine.add(strand);

            for (DotBracketSymbol symbol : strand.getSymbols()) {
                level += symbol.isOpening() ? 1 : 0;
                level -= symbol.isClosing() ? 1 : 0;
            }

            if (level == 0) {
                result.add(new CombinedStrand(toCombine));
                toCombine.clear();
            }
        }

        return result;
    }

    @SuppressWarnings("static-method")
    protected int getCtOriginalColumn(DotBracketSymbol symbol) {
        return symbol.getIndex() + 1;
    }
}
