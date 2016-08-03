package pl.poznan.put.structure.secondary.formats;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.structure.secondary.BasePair;
import pl.poznan.put.structure.secondary.ClassifiedBasePair;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class BpSeq implements Serializable {
    private static final long serialVersionUID = 7463893480150381692L;
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public static class Entry implements Comparable<BpSeq.Entry>, Serializable {
        private static final long serialVersionUID = -2263073800915995485L;
        private final int index;
        private final int pair;
        private final char seq;
        private final String comment;

        public Entry(final int index, final int pair, final char seq) {
            super();
            this.index = index;
            this.pair = pair;
            this.seq = seq;
            comment = "";
        }

        public Entry(final int index, final int pair, final char seq,
                     final String comment) {
            super();
            this.index = index;
            this.pair = pair;
            this.seq = seq;
            this.comment = comment;
        }

        public final int getIndex() {
            return index;
        }

        public final int getPair() {
            return pair;
        }

        public final char getSeq() {
            return seq;
        }

        public final String getComment() {
            return comment;
        }

        public final boolean isPaired() {
            return pair != 0;
        }

        @Override
        public final int compareTo(final BpSeq.Entry t) {
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
        public final int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + index;
            result = (prime * result) + pair;
            result = (prime * result) + seq;
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
            BpSeq.Entry other = (BpSeq.Entry) obj;
            return (index == other.index) && (pair == other.pair) &&
                   (seq == other.seq);
        }

        @Override
        public final String toString() {
            StringBuilder builder = new StringBuilder(10 + comment.length());
            if (StringUtils.isNotBlank(comment)) {
                builder.append('#');
                builder.append(comment);
                builder.append(System.lineSeparator());
            }
            builder.append(index);
            builder.append(' ');
            builder.append(seq);
            builder.append(' ');
            builder.append(pair);
            return builder.toString();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BpSeq.class);

    public static BpSeq fromString(final String data)
            throws InvalidSecondaryStructureException {
        List<BpSeq.Entry> entries = new ArrayList<>();

        for (String line : data.split(System.lineSeparator())) {
            line = line.trim();

            int hash = line.indexOf('#');
            if (hash != -1) {
                line = line.substring(0, hash);
            }

            if (line.isEmpty()) {
                continue;
            }

            String[] split = BpSeq.WHITESPACE.split(line);

            if ((split.length != 3) || (split[1].length() != 1)) {
                throw new InvalidSecondaryStructureException(String.format(
                        "Line does not conform to BPSEQ format: %s", line));
            }

            int index;
            int pair;
            char seq;

            try {
                index = Integer.valueOf(split[0]);
                seq = split[1].charAt(0);
                pair = Integer.valueOf(split[2]);
            } catch (NumberFormatException e) {
                throw new InvalidSecondaryStructureException(String.format(
                        "Line does not conform to BPSEQ format: %s", line), e);
            }

            entries.add(new BpSeq.Entry(index, pair, seq));
        }

        return new BpSeq(entries);
    }

    public static BpSeq fromCt(final Ct ct)
            throws InvalidSecondaryStructureException {
        List<BpSeq.Entry> bpseqEntries = new ArrayList<>();

        for (Ct.Entry e : ct.getEntries()) {
            bpseqEntries.add(
                    new BpSeq.Entry(e.getIndex(), e.getPair(), e.getSeq()));
        }

        return new BpSeq(bpseqEntries);
    }

    public static BpSeq fromDotBracket(final DotBracket db)
            throws InvalidSecondaryStructureException {
        List<BpSeq.Entry> entries = new ArrayList<>();

        for (DotBracketSymbol symbol : db.getSymbols()) {
            DotBracketSymbol pair = symbol.getPair();
            int index = symbol.getIndex() + 1;
            int pairIndex = (pair != null) ? (pair.getIndex() + 1) : 0;
            char sequence = symbol.getSequence();

            entries.add(new BpSeq.Entry(index, pairIndex, sequence));
        }

        return new BpSeq(entries);
    }

    public static BpSeq fromResidueCollection(
            final ResidueCollection residueCollection,
            final Iterable<ClassifiedBasePair> basePairs)
            throws InvalidSecondaryStructureException {
        Collection<BasePair> allBasePairs = new ArrayList<>();
        Map<BasePair, String> basePairToComment = new HashMap<>();

        for (ClassifiedBasePair classifiedBasePair : basePairs) {
            BasePair basePair = classifiedBasePair.getBasePair();
            allBasePairs.add(basePair);

            String comment = classifiedBasePair.isCanonical() ? ""
                                                              :
                             classifiedBasePair
                                     .generateComment();
            basePairToComment.put(basePair, comment);
            basePairToComment.put(basePair.invert(), comment);
        }

        List<BpSeq.Entry> entries = new ArrayList<>();
        entries.addAll(
                BpSeq.generateEntriesForPaired(residueCollection, allBasePairs,
                                               basePairToComment));
        entries.addAll(BpSeq.generateEntriesForUnpaired(residueCollection,
                                                        allBasePairs));
        return new BpSeq(entries);
    }

    private static Collection<BpSeq.Entry> generateEntriesForUnpaired(
            final ResidueCollection residueCollection,
            final Iterable<BasePair> allBasePairs) {
        List<PdbResidue> residues = residueCollection.getResidues();
        Collection<PdbResidueIdentifier> paired = new HashSet<>();

        for (BasePair basePair : allBasePairs) {
            paired.add(basePair.getLeft());
            paired.add(basePair.getRight());
        }

        Collection<BpSeq.Entry> entries = new ArrayList<>();
        for (int i = 0; i < residues.size(); i++) {
            PdbResidue residue = residues.get(i);
            if (!paired.contains(residue.getResidueIdentifier())) {
                entries.add(
                        new BpSeq.Entry(i + 1, 0, residue.getOneLetterName()));
            }
        }

        return entries;
    }

    private static Collection<BpSeq.Entry> generateEntriesForPaired(
            final ResidueCollection residueCollection,
            final Iterable<BasePair> basePairs,
            final Map<BasePair, String> basePairToComment) {
        Collection<BpSeq.Entry> entries = new ArrayList<>();
        List<PdbResidue> residues = residueCollection.getResidues();

        for (BasePair basePair : basePairs) {
            PdbResidue left = residueCollection.findResidue(basePair.getLeft());
            PdbResidue right = residueCollection.findResidue(
                    basePair.getRight());
            int indexL = 1 + residues.indexOf(left);
            int indexR = 1 + residues.indexOf(right);
            entries.add(new BpSeq.Entry(indexL, indexR, left.getOneLetterName(),
                                        basePairToComment.get(basePair)));
            entries.add(
                    new BpSeq.Entry(indexR, indexL, right.getOneLetterName(),
                                    basePairToComment.get(basePair)));
            BpSeq.LOGGER.trace("Storing pair ({} -> {}) which is ({} -> {})",
                               indexL, indexR, left, right);
        }

        return entries;
    }

    private final SortedSet<BpSeq.Entry> entries;

    public BpSeq(final Collection<BpSeq.Entry> entries)
            throws InvalidSecondaryStructureException {
        super();
        this.entries = new TreeSet<>(entries);
        validate();
    }

    /*
     * Check if all pairs match.
     */
    private void validate() throws InvalidSecondaryStructureException {
        Map<Integer, Integer> map = new HashMap<>();

        for (BpSeq.Entry e : entries) {
            if (e.getIndex() == e.getPair()) {
                throw new InvalidSecondaryStructureException(String.format(
                        "Invalid line in BPSEQ data, a residue cannot be " +
                        "paired with itself! Line: %s", e));
            }

            map.put(e.getIndex(), e.getPair());
        }

        int previous = 0;

        for (BpSeq.Entry e : entries) {
            if ((e.getIndex() - previous) != 1) {
                throw new InvalidSecondaryStructureException(String.format(
                        "Inconsistent numbering in BPSEQ format: previous=%d," +
                        " current=%d", previous, e.getIndex()));
            }
            previous = e.getIndex();

            int pair = map.get(e.getIndex());
            if (pair != 0) {
                if (!map.containsKey(pair)) {
                    throw new InvalidSecondaryStructureException(String.format(
                            "Inconsistency in BPSEQ format: (%d -> %d)",
                            e.getIndex(), pair));
                }
                if (map.get(pair) != e.getIndex()) {
                    throw new InvalidSecondaryStructureException(String.format(
                            "Inconsistency in BPSEQ format: (%d -> %d) and " +
                            "(%d -> %d)", e.getIndex(), pair, pair,
                            map.get(pair)));
                }
            }
        }
    }

    public final SortedSet<BpSeq.Entry> getEntries() {
        return Collections.unmodifiableSortedSet(entries);
    }

    public final String getSequence() {
        StringBuilder builder = new StringBuilder(entries.size());
        for (BpSeq.Entry e : entries) {
            builder.append(e.getSeq());
        }
        return builder.toString();
    }

    public final SortedSet<BpSeq.Entry> getPaired() {
        SortedSet<BpSeq.Entry> sortedSet = new TreeSet<>();
        for (BpSeq.Entry entry : entries) {
            if (entry.getIndex() < entry.getPair()) {
                sortedSet.add(entry);
            }
        }
        return sortedSet;
    }

    public final void removePair(final BpSeq.Entry toRemove) {
        if (!toRemove.isPaired()) {
            return;
        }

        for (BpSeq.Entry entry : entries) {
            if (entry.getIndex() == toRemove.getPair()) {
                entries.remove(toRemove);
                entries.remove(entry);
                entries.add(new BpSeq.Entry(toRemove.index, 0, toRemove.seq));
                entries.add(new BpSeq.Entry(entry.index, 0, entry.seq));
                return;
            }
        }
    }

    public final int size() {
        return entries.size();
    }

    public final boolean hasAnyPair() {
        for (BpSeq.Entry entry : entries) {
            if (entry.isPaired()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        BpSeq bpSeq = (BpSeq) obj;
        return CollectionUtils.isEqualCollection(entries, bpSeq.entries);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(entries);
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder(10 * entries.size());

        for (BpSeq.Entry e : entries) {
            builder.append(e);
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }
}
