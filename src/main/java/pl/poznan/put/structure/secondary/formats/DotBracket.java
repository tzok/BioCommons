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
import java.util.Objects;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DotBracket implements Serializable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DotBracket.class);
    private static final long serialVersionUID = -517503434874402102L;

    /*
     * Regex:
     * (>.+\r?\n)?([ACGUTRYNacgutryn]+)\r?\n([-.()\[\]{}<>A-Za-z]+)
     *
     * Groups:
     *  1: strand name with leading '>' or null
     *  2: sequence
     *  3: structure
     */
    private static final Pattern DOTBRACKET_PATTERN = Pattern.compile(
            "(>.+\\r?\\n)?([ACGUTRYNacgutryn]+)\\r?\\n([-.()" +
            "\\[\\]{}<>A-Za-z]+)");
    private static final Pattern SEQUENCE_PATTERN =
            Pattern.compile("[ACGUTRYNacgutryn]+");
    private static final Pattern STRUCTURE_PATTERN =
            Pattern.compile("[-.()\\[\\]{}<>A-Za-z]+");

    protected final List<Strand> strands = new ArrayList<>();
    protected final List<DotBracketSymbol> symbols = new ArrayList<>();
    protected final String sequence;
    protected final String structure;

    public DotBracket(final String sequence, final String structure)
            throws InvalidStructureException {
        super();
        this.sequence = sequence;
        this.structure = structure;

        if (!DotBracket.SEQUENCE_PATTERN.matcher(sequence).matches() ||
            !DotBracket.STRUCTURE_PATTERN.matcher(structure).matches()) {
            throw new InvalidStructureException(
                    "Invalid dot-bracket:\n" + sequence + '\n' + structure);
        }

        buildSymbolList();
        analyzePairing();

        strands.add(new Strand(this, "", 0, structure.length()));
    }

    private void buildSymbolList() {
        final char[] seq = sequence.toCharArray();
        final char[] str = structure.toCharArray();
        assert seq.length == str.length;

        DotBracketSymbol current = new DotBracketSymbol(seq[0], str[0], 0);

        for (int i = 1; i < seq.length; i++) {
            final DotBracketSymbol next =
                    new DotBracketSymbol(seq[i], str[i], i);
            current.setNext(next);
            next.setPrevious(current);
            symbols.add(current);
            current = next;
        }
        symbols.add(current);

        assert symbols.size() == seq.length;
    }

    private void analyzePairing() throws InvalidStructureException {
        final BidiMap<Character, Character> parentheses = new TreeBidiMap<>();
        parentheses.put('(', ')');
        parentheses.put('[', ']');
        parentheses.put('{', '}');
        parentheses.put('<', '>');

        for (final char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
            parentheses.put(c, Character.toLowerCase(c));
        }

        final Map<Character, Stack<DotBracketSymbol>> parenthesesStacks =
                new HashMap<>();
        for (final char c : parentheses.keySet()) {
            parenthesesStacks.put(c, new Stack<>());
        }

        for (final DotBracketSymbol symbol : symbols) {
            final char str = symbol.getStructure();

            // catch dot '.'
            if ((str == '.') || (str == '-')) {
                symbol.setPair(null);
                continue;
            }

            // catch opening '(', '[', etc.
            if (parentheses.containsKey(str)) {
                final Stack<DotBracketSymbol> stack =
                        parenthesesStacks.get(str);
                stack.push(symbol);
                continue;
            }

            // catch closing ')', ']', etc.
            if (parentheses.containsValue(str)) {
                final char opening = parentheses.getKey(str);
                final Stack<DotBracketSymbol> stack =
                        parenthesesStacks.get(opening);

                if (stack.empty()) {
                    throw new InvalidStructureException(
                            "Invalid dot-bracket input:\n" + sequence + '\n' +
                            structure);
                }

                final DotBracketSymbol pair = stack.pop();
                symbol.setPair(pair);
                pair.setPair(symbol);
                continue;
            }

            DotBracket.LOGGER
                    .error("Unknown symbol in dot-bracket string: {}", str);
        }
    }

    public static DotBracket fromString(final String data)
            throws InvalidStructureException {
        final Matcher matcher = DotBracket.DOTBRACKET_PATTERN.matcher(data);

        final Collection<Pair<Integer, Integer>> pairBeginEnd =
                new ArrayList<>();
        final List<String> strandNames = new ArrayList<>();
        final StringBuilder sequenceBuilder = new StringBuilder(data.length());
        final StringBuilder structureBuilder = new StringBuilder(data.length());
        int begin = 0;
        int end = 0;

        while (matcher.find()) {
            final String strandName =
                    (matcher.group(1) != null) ? matcher.group(1).substring(1)
                                                        .trim() : "";
            final String sequence = matcher.group(2);
            final String structure = matcher.group(3);

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

        if ((sequenceBuilder.length() == 0) ||
            (structureBuilder.length() == 0)) {
            throw new InvalidStructureException(
                    "Cannot parse dot-bracket:\n" + data);
        }

        final DotBracket dotBracket = new DotBracket(sequenceBuilder.toString(),
                                                     structureBuilder
                                                             .toString());
        dotBracket.strands.clear();

        int index = 0;
        for (final Pair<Integer, Integer> pair : pairBeginEnd) {
            dotBracket.strands
                    .add(new Strand(dotBracket, strandNames.get(index),
                                    pair.getLeft(), pair.getRight()));
            index += 1;
        }

        return dotBracket;
    }

    public final String toStringWithStrands() {
        final StringBuilder builder =
                new StringBuilder(sequence.length() + structure.length());
        for (final Strand strand : strands) {
            builder.append(strand);
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        final DotBracket other = (DotBracket) o;
        return Objects.equals(sequence, other.sequence) &&
               Objects.equals(structure, other.structure);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(sequence, structure);
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

        for (final Ct.Entry e : ct.getEntries()) {
            if (e.getAfter() == 0) {
                final Strand strand = new Strand(this, "", start, i + 1);
                strands.add(strand);
                start = i + 1;
            }

            i += 1;
        }
    }

    public final List<CombinedStrand> combineStrands() {
        final List<CombinedStrand> result = new ArrayList<>();
        final List<Strand> toCombine = new ArrayList<>();
        int level = 0;

        for (final Strand strand : strands) {
            toCombine.add(strand);

            for (final DotBracketSymbol symbol : strand.getSymbols()) {
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
