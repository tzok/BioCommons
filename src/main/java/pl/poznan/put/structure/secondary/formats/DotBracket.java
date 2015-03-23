package pl.poznan.put.structure.secondary.formats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DotBracket implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(DotBracket.class);

    public static DotBracket fromString(String data) throws InvalidSecondaryStructureException {
        List<Pair<Integer, Integer>> pairBeginEnd = new ArrayList<Pair<Integer, Integer>>();
        StringBuilder seqBuilder = new StringBuilder();
        StringBuilder strBuilder = new StringBuilder();
        String[] split = data.split("\n");
        int begin = 0;
        int end = 0;
        int i = 0;

        while (i < split.length) {
            // skip empty and non-sequence lines in the beginning
            if (StringUtils.isBlank(split[i]) || !split[i].matches("[ACGUTRYNacgutryn]+")) {
                i++;
                continue;
            }

            seqBuilder.append(split[i++]);

            if (!split[i].matches("[-.()\\[\\]{}<>A-Za-z]+")) {
                throw new InvalidSecondaryStructureException("Not valid dot-bracket input:\n" + data);
            }

            strBuilder.append(split[i]);
            assert seqBuilder.length() == strBuilder.length();

            end += split[i++].length();
            pairBeginEnd.add(Pair.of(begin, end));
            begin = end;
        }

        DotBracket dotBracket = new DotBracket(seqBuilder.toString(), strBuilder.toString());
        dotBracket.strands.clear();

        for (Pair<Integer, Integer> pair : pairBeginEnd) {
            dotBracket.strands.add(new Strand(dotBracket, pair.toString(), pair.getLeft(), pair.getRight()));
        }

        return dotBracket;
    }

    // FIXME
    protected final List<Strand> strands = new ArrayList<Strand>();

    private final List<DotBracketSymbol> symbols = new ArrayList<DotBracketSymbol>();
    private final String sequence;
    private final String structure;

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

        DotBracketSymbol current = new DotBracketSymbol(this, seq[0], str[0], 0);

        for (int i = 1; i < seq.length; i++) {
            DotBracketSymbol next = new DotBracketSymbol(this, seq[i], str[i], i);
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

            DotBracket.logger.error("Unknown symbol in dot-bracket string: " + str);
        }
    }

    @Override
    public String toString() {
        return ">strand\n" + sequence + "\n" + structure;
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
        return strands;
    }

    public void splitStrands(Ct ct) {
        strands.clear();

        List<Ct.Entry> entries = ct.getEntries();
        int start = 0;

        for (int i = 0; i < entries.size(); i++) {
            Ct.Entry e = entries.get(i);

            if (e.getAfter() == 0) {
                Strand strand = new Strand(this, "", start, i + 1);
                strands.add(strand);
                start = i + 1;
            }
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

    public Ct toCt() throws InvalidSecondaryStructureException {
        List<Ct.Entry> entries = new ArrayList<Ct.Entry>();

        for (Strand s : getStrands()) {
            for (int i = 0, j = s.getFrom(); j < s.getTo(); i++, j++) {
                DotBracketSymbol symbol = getSymbol(j);
                DotBracketSymbol pair = symbol.getPair();

                int index = symbol.getIndex() + 1;
                int pairIndex = pair != null ? pair.getIndex() + 1 : 0;
                int before = i;
                int after = (i + 2) % (s.getTo() + 1);
                int original = index;
                char seq = symbol.getSequence();

                entries.add(new Ct.Entry(index, pairIndex, before, after, original, seq));
            }
        }

        return new Ct(entries);
    }
}
