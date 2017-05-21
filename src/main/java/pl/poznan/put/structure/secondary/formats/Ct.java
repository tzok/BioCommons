package pl.poznan.put.structure.secondary.formats;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class Ct implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ct.class);
    private static final boolean FIX_LAST_ENTRY = true;
    private static boolean printComments = true;
    private final SortedSet<Entry> entries;

    public Ct(final List<Entry> entries) throws InvalidStructureException {
        super();
        this.entries = new TreeSet<>(entries);
        validate();
    }

    /*
     * Check if all pairs match.
     */
    private void validate() throws InvalidStructureException {
        if (Ct.LOGGER.isTraceEnabled()) {
            Ct.LOGGER.trace("CT to be validated:\n{}", toString());
        }

        final Map<Integer, Integer> map = new HashMap<>();

        for (final Entry e : entries) {
            map.put(e.index, e.pair);
        }

        int previous = 0;

        for (final Entry e : entries) {
            if ((e.index - previous) != 1) {
                throw new InvalidStructureException(
                        "Inconsistent numbering in CT format: previous="
                        + previous + ", current" + e.index);
            }

            previous = e.index;
            final int pair = map.get(e.index);

            if (pair != 0) {
                if (!map.containsKey(pair)) {
                    throw new InvalidStructureException(
                            "Inconsistency in CT format: (" + e.index + " -> "
                            + pair + ')');
                }

                if (map.get(pair) != e.index) {
                    throw new InvalidStructureException(
                            "Inconsistency in CT format: (" + e.index + " -> "
                            + pair + ") and (" + pair + " -> " + map.get(pair)
                            + ')');
                }
            }
        }

        // previous == maximum index

        for (final Entry e : entries) {
            if ((e.before < 0) || (e.before >= previous)) {
                throw new InvalidStructureException(
                        "Inconsistency in CT format. Third column has invalid"
                        + " value in entry: " + e);
            }

            if ((e.after == 1) || (e.after < 0) || (e.after > (previous + 1))) {
                throw new InvalidStructureException(
                        "Inconsistency in CT format. Fourth column has "
                        + "invalid value in entry: " + e);
            }
        }

        /*
         * Check if strands' ends are correct
         */
        boolean expectNewStrand = true;
        Entry prevEntry = null;

        for (final Entry e : entries) {
            if (((e.getBefore() != 0) && expectNewStrand) || (
                    (e.getBefore() == 0) && !expectNewStrand)) {
                throw new InvalidStructureException(
                        "Inconsistency in CT format. The field 'before' is "
                        + "non-zero for the first entry in a strand: " + e);
            }

            if ((prevEntry != null) && (
                    ((prevEntry.getAfter() != 0) && expectNewStrand) || (
                            (prevEntry.getAfter() == 0) && !expectNewStrand))) {
                throw new InvalidStructureException(
                        "Inconsistency in CT format. The field 'after' is "
                        + "non-zero for the last entry in a strand: "
                        + prevEntry);
            }

            expectNewStrand = e.getAfter() == 0;
            prevEntry = e;
        }

        final Entry lastEntry = entries.last();

        if (lastEntry.after != 0) {
            if (Ct.FIX_LAST_ENTRY) {
                entries.remove(lastEntry);
                entries.add(new Entry(lastEntry.index, lastEntry.pair,
                                      lastEntry.before, 0, lastEntry.original,
                                      lastEntry.seq));
            } else {
                throw new InvalidStructureException(
                        "The field 'after' in the last entry is non-zero: "
                        + lastEntry);
            }
        }
    }

    public static Ct fromString(final String data)
            throws InvalidStructureException {
        final List<Entry> entries = new ArrayList<>();
        boolean firstLine = true;

        for (String line : data.split("\n")) {
            line = line.trim();

            final int hash = line.indexOf('#');
            if (hash != -1) {
                line = line.substring(0, hash);
            }

            if (line.isEmpty()) {
                continue;
            }

            final String[] split = line.split("\\s+");

            if (firstLine) {
                try {
                    final int lineCount = Integer.parseInt(split[0]);
                    if (lineCount < 0) {
                        throw new InvalidStructureException(
                                "Invalid CT format. Line count < 0 detected: "
                                + line);
                    }
                } catch (final NumberFormatException e) {
                    throw new InvalidStructureException(
                            "Invalid CT format. Failed to parse line count: "
                            + line, e);
                }
                firstLine = false;
                continue;
            }

            if (split.length != 6) {
                throw new InvalidStructureException(
                        "Invalid CT format. Six columns not found in line: "
                        + line);
            }

            final int index;
            final int pair;
            final int before;
            final int after;
            final int original;
            final char seq;

            try {
                index = Integer.valueOf(split[0]);
                seq = split[1].charAt(0);
                before = Integer.valueOf(split[2]);
                after = Integer.valueOf(split[3]);
                pair = Integer.valueOf(split[4]);
                original = Integer.valueOf(split[5]);
            } catch (final NumberFormatException e) {
                throw new InvalidStructureException(
                        "Invalid CT format. Failed to parse column values: "
                        + line, e);
            }

            entries.add(new Entry(index, pair, before, after, original, seq));
        }

        return new Ct(entries);
    }

    public static Ct fromBpSeq(final BpSeq bpSeq)
            throws InvalidStructureException {
        final List<Entry> ctEntries = new ArrayList<>();
        final SortedSet<BpSeq.Entry> entries = bpSeq.getEntries();
        final int size = entries.size();

        for (final BpSeq.Entry entry : entries) {
            final int index = entry.getIndex();
            final int pair = entry.getPair();
            final char seq = entry.getSeq();
            final String comment = entry.getComment();
            ctEntries.add(new Entry(index, pair, index - 1,
                                       (index + 1) % (size + 1), index, seq,
                                       comment));
        }

        return new Ct(ctEntries);
    }

    public static Ct fromBpSeqAndPdbModel(
            final BpSeq bpSeq, final PdbModel model)
            throws InvalidStructureException {
        final PdbModel rna;
        try {
            rna = model.filteredNewInstance(MoleculeType.RNA);
        } catch (final PdbParsingException e) {
            throw new InvalidStructureException("Failed to filter RNA chains",
                                                e);
        }

        final List<Entry> ctEntries = new ArrayList<>();
        final List<PdbResidue> residues = rna.getResidues();
        final SortedSet<BpSeq.Entry> entries = bpSeq.getEntries();
        int i = 0;

        for (final BpSeq.Entry entry : entries) {
            final PdbResidue residue = residues.get(i);
            final PdbChain chain = rna.findChainContainingResidue(
                    residue.getResidueIdentifier());
            final List<PdbResidue> chainResidues = chain.getResidues();

            final int index = entry.getIndex();
            final int pair = entry.getPair();
            final int before = chainResidues.indexOf(residue);
            final int after = (before + 2) % (chainResidues.size() + 1);
            final int original = residue.getResidueNumber();
            final char seq = entry.getSeq();
            final String comment = entry.getComment();
            ctEntries
                    .add(new Entry(index, pair, before, after, original, seq,
                                      comment));

            i += 1;
        }

        return new Ct(ctEntries);
    }

    public static Ct fromDotBracket(final DotBracket dotBracket)
            throws InvalidStructureException {
        final List<Entry> entries = new ArrayList<>();

        for (final Strand s : dotBracket.getStrands()) {
            for (int i = 0, j = s.getFrom(); j < s.getTo(); i++, j++) {
                final DotBracketSymbol symbol = dotBracket.getSymbol(j);
                final DotBracketSymbol pair = symbol.getPair();

                final int index = symbol.getIndex() + 1;
                final int pairIndex = (pair != null) ? (pair.getIndex() + 1) : 0;
                final int after = (j == (s.getTo() - 1)) ? 0 : (i + 2);
                final int original = dotBracket.getCtOriginalColumn(symbol);
                final char seq = symbol.getSequence();

                entries.add(new Entry(index, pairIndex, i, after, original,
                                         seq));
            }
        }

        return new Ct(entries);
    }

    public static void setPrintComments(final boolean printComments) {
        Ct.printComments = printComments;
    }

    public int getStrandCount() {
        int count = 0;
        for (final Entry entry : entries) {
            if (entry.getAfter() == 0) {
                count += 1;
            }
        }
        return count;
    }

    public Iterable<Entry> getEntries() {
        return Collections.unmodifiableSortedSet(entries);
    }

    public static class Entry implements Serializable, Comparable<Entry> {
        private final int index;
        private final int pair;
        private final int before;
        private final int after;
        private final int original;
        private final char seq;
        private final String comment;

        public Entry(
                final int index, final int pair, final int before,
                final int after, final int original, final char seq) {
            super();
            this.index = index;
            this.pair = pair;
            this.before = before;
            this.after = after;
            this.seq = seq;
            this.original = original;
            comment = "";
        }

        public Entry(
                final int index, final int pair, final int before,
                final int after, final int original, final char seq,
                final String comment) {
            super();
            this.index = index;
            this.pair = pair;
            this.before = before;
            this.after = after;
            this.seq = seq;
            this.original = original;
            this.comment = comment;
        }

        public int getIndex() {
            return index;
        }

        public int getPair() {
            return pair;
        }

        public int getBefore() {
            return before;
        }

        public int getAfter() {
            return after;
        }

        public int getOriginal() {
            return original;
        }

        public char getSeq() {
            return seq;
        }

        @Override
        public int compareTo(final Entry t) {
            if (t == null) {
                throw new NullPointerException();
            }

            if (equals(t)) {
                return 0;
            }

            if (index < t.index) {
                return -1;
            }
            if (index > t.index) {
                return 1;
            }
            return 0;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + after;
            result = (prime * result) + before;
            result = (prime * result) + ((comment == null) ? 0 : comment
                    .hashCode());
            result = (prime * result) + index;
            result = (prime * result) + original;
            result = (prime * result) + pair;
            result = (prime * result) + seq;
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (getClass() != o.getClass()) {
                return false;
            }
            final Entry other = (Entry) o;
            if (after != other.after) {
                return false;
            }
            if (before != other.before) {
                return false;
            }
            if (comment == null) {
                if (other.comment != null) {
                    return false;
                }
            } else if (!Objects.equals(comment, other.comment)) {
                return false;
            }
            return (index == other.index) && (original == other.original) && (
                    pair == other.pair) && (seq == other.seq);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(index);
            builder.append(' ');
            builder.append(seq);
            builder.append(' ');
            builder.append(before);
            builder.append(' ');
            builder.append(after);
            builder.append(' ');
            builder.append(pair);
            builder.append(' ');
            builder.append(original);
            if (Ct.printComments && !StringUtils.isBlank(comment)) {
                builder.append(" # ");
                builder.append(comment);
            }
            return builder.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(entries.size());
        builder.append('\n');

        for (final Entry e : entries) {
            builder.append(e);
            builder.append('\n');
        }

        return builder.toString();
    }
}
