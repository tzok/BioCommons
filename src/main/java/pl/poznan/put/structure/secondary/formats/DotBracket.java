package pl.poznan.put.structure.secondary.formats;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DotBracket implements Serializable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DotBracket.class);
    private static final Pattern DOTBRACKET_PATTERN = Pattern.compile(
            "(>(strand_)?(.+)\n)?([ACGUTRYNacgutryn]+)\n([-.()"
            + "\\[\\]{}<>A-Za-z]+)");
    private static final long serialVersionUID = -517503434874402102L;
    private static final Pattern SEQUENCE_PATTERN =
            Pattern.compile("[ACGUTRYNacgutryn]+");
    private static final Pattern STRUCTURE_PATTERN =
            Pattern.compile("[-.()\\[\\]{}<>A-Za-z]+");
    // FIXME
    protected final List<Strand> strands = new ArrayList<>();
    protected final List<DotBracketSymbol> symbols = new ArrayList<>();
    protected final String sequence;
    protected final String structure;

    public DotBracket(final String sequence, final String structure)
            throws InvalidStructureException {
        super();
        this.sequence = sequence;
        this.structure = structure;

        if (!DotBracket.SEQUENCE_PATTERN.matcher(sequence).matches()
            || !DotBracket.STRUCTURE_PATTERN.matcher(structure).matches()) {
            throw new InvalidStructureException(
                    "Invalid dot-bracket:\n" + sequence + '\n' + structure);
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

    private void analyzePairing() throws InvalidStructureException {
        BidiMap<Character, Character> parentheses = new TreeBidiMap<>();
        parentheses.put('(', ')');
        parentheses.put('[', ']');
        parentheses.put('{', '}');
        parentheses.put('<', '>');

        for (char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
            parentheses.put(c, Character.toLowerCase(c));
        }

        Map<Character, Stack<DotBracketSymbol>> parenthesesStacks =
                new HashMap<>();
        for (char c : parentheses.keySet()) {
            parenthesesStacks.put(c, new Stack<DotBracketSymbol>());
        }

        for (DotBracketSymbol symbol : symbols) {
            char str = symbol.getStructure();

            // catch dot '.'
            if ((str == '.') || (str == '-')) {
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
                    throw new InvalidStructureException(
                            "Invalid dot-bracket input:\n" + sequence + '\n'
                            + structure);
                }

                DotBracketSymbol pair = stack.pop();
                symbol.setPair(pair);
                pair.setPair(symbol);
                continue;
            }

            DotBracket.LOGGER
                    .error("Unknown symbol in dot-bracket string: {}", str);
        }
    }

    /*
     * This is just a simple and naive implementation (a greedy heuristic). For
     * a robust solution, go check RNApdbee http://rnapdbee.cs.put.poznan.pl
     */
    public static DotBracket fromBpSeq(final BpSeq bpSeq)
            throws InvalidStructureException {
        String sequence = bpSeq.getSequence();
        String structure = DotBracket.bpSeqToStructure(bpSeq);
        return new DotBracket(sequence, structure);
    }

    private static String bpSeqToStructure(final BpSeq bpSeq) {
        Stack<Integer> stack = new Stack<>();

        List<BpSeq.Entry> entries = new ArrayList<>(bpSeq.getEntries());
        Collection<BpSeq.Entry> current = new TreeSet<>();
        Collection<BpSeq.Entry> next = new TreeSet<>(entries);

        int[] levels = new int[entries.size()];
        int currentLevel = 0;

        while (!next.isEmpty()) {
            current.clear();
            current.addAll(next);
            next.clear();

            for (BpSeq.Entry entry : current) {
                int index = entry.getIndex();
                int pair = entry.getPair();

                if (pair == 0) {
                    levels[index - 1] = -1;
                } else if (index < pair) {
                    stack.push(index);
                } else if (index > pair) {
                    if (stack.contains(pair)) {
                        while (stack.peek() != pair) {
                            next.add(entries.get(stack.pop() - 1));
                        }
                        stack.pop();
                        levels[index - 1] = currentLevel;
                        levels[pair - 1] = currentLevel;
                    } else {
                        next.add(entry);
                    }
                }
            }

            currentLevel += 1;
        }

        StringBuilder builder = new StringBuilder(bpSeq.size());
        char[] bracketsOpening = "([{<ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] bracketsClosing = ")]}>abcdefghijklmnopqrstuvwxyz".toCharArray();

        for (BpSeq.Entry entry : entries) {
            int index = entry.getIndex();
            int pair = entry.getPair();

            if (pair == 0) {
                builder.append('.');
            } else if (index < pair) {
                builder.append(bracketsOpening[levels[index - 1]]);
            } else {
                builder.append(bracketsClosing[levels[index - 1]]);
            }
        }

        return builder.toString();
    }

    public static DotBracket fromString(final CharSequence data)
            throws InvalidStructureException {
        Matcher matcher = DotBracket.DOTBRACKET_PATTERN.matcher(data);

        Collection<Pair<Integer, Integer>> pairBeginEnd = new ArrayList<>();
        List<String> strandNames = new ArrayList<>();
        StringBuilder sequenceBuilder = new StringBuilder(data.length());
        StringBuilder structureBuilder = new StringBuilder(data.length());
        int begin = 0;
        int end = 0;

        while (matcher.find()) {
            String strandName =
                    (matcher.group(3) != null) ? matcher.group(3) : "";
            String sequence = matcher.group(4);
            String structure = matcher.group(5);

            if (sequence.length() != structure.length()) {
                throw new InvalidStructureException(
                        "Invalid dot-bracket string:\n" + data);
            }

            strandNames.add(strandName);
            sequenceBuilder.append(sequence);
            structureBuilder.append(structure);

            end += sequence.length();
            pairBeginEnd.add(Pair.of(begin, end));
            begin = end;
        }

        if ((sequenceBuilder.length() == 0) || (structureBuilder.length()
                                                == 0)) {
            throw new InvalidStructureException(
                    "Cannot parse dot-bracket:\n" + data);
        }

        DotBracket dotBracket = new DotBracket(sequenceBuilder.toString(),
                                               structureBuilder.toString());
        dotBracket.strands.clear();

        int index = 0;
        for (Pair<Integer, Integer> pair : pairBeginEnd) {
            dotBracket.strands
                    .add(new Strand(dotBracket, strandNames.get(index),
                                    pair.getLeft(), pair.getRight()));
            index += 1;
        }

        return dotBracket;
    }

    public final String toStringWithStrands() {
        StringBuilder builder =
                new StringBuilder(sequence.length() + structure.length());
        for (Strand strand : strands) {
            builder.append(strand);
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((sequence == null) ? 0
                                                        : sequence.hashCode());
        result = (prime * result) + ((structure == null) ? 0 : structure
                .hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
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

    @Override
    public final String toString() {
        return ">strand\n" + sequence + '\n' + structure;
    }

    public final String getSequence() {
        return sequence;
    }

    public final String getStructure() {
        return structure;
    }

    public final int getLength() {
        return structure.length();
    }

    public final DotBracketSymbol getSymbol(final int index) {
        assert index < symbols.size();
        return symbols.get(index);
    }

    public final List<DotBracketSymbol> getSymbols() {
        return Collections.unmodifiableList(symbols);
    }

    public final List<Strand> getStrands() {
        return Collections.unmodifiableList(strands);
    }

    public final int getStrandCount() {
        return strands.size();
    }

    public final void splitStrands(final Ct ct) {
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

    public final List<CombinedStrand> combineStrands() {
        List<CombinedStrand> result = new ArrayList<>();
        List<Strand> toCombine = new ArrayList<>();
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

    protected int getCtOriginalColumn(final DotBracketSymbol symbol) {
        return symbol.getIndex() + 1;
    }
}
