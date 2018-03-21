package pl.poznan.put.structure.secondary.formats;

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
import pl.poznan.put.structure.secondary.pseudoknots.Region;

public class BpSeq implements Serializable {
  private static final long serialVersionUID = 7463893480150381692L;
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");
  private static final boolean WRITE_COMMENTS = false;
  private static final Logger LOGGER = LoggerFactory.getLogger(BpSeq.class);
  private final SortedSet<Entry> entries;

  public BpSeq(final Collection<Entry> entries) throws InvalidStructureException {
    super();
    this.entries = new TreeSet<>(entries);
    validate();
  }

  /*
   * Check if all pairs match.
   */
  private void validate() throws InvalidStructureException {
    final Map<Integer, Integer> map = new HashMap<>();

    for (final Entry e : entries) {
      if (e.getIndex() == e.getPair()) {
        throw new InvalidStructureException(
            String.format(
                "Invalid line in BPSEQ data, a residue cannot be " + "paired with itself! Line: %s",
                e));
      }

      map.put(e.getIndex(), e.getPair());
    }

    int previous = 0;

    for (final Entry e : entries) {
      if ((e.getIndex() - previous) != 1) {
        throw new InvalidStructureException(
            String.format(
                "Inconsistent numbering in BPSEQ format: previous=%d," + " current=%d",
                previous, e.getIndex()));
      }
      previous = e.getIndex();

      final int pair = map.get(e.getIndex());
      if (pair != 0) {
        if (!map.containsKey(pair)) {
          throw new InvalidStructureException(
              String.format("Inconsistency in BPSEQ format: (%d -> %d)", e.getIndex(), pair));
        }
        if (map.get(pair) != e.getIndex()) {
          throw new InvalidStructureException(
              String.format(
                  "Inconsistency in BPSEQ format: (%d -> %d) and " + "(%d -> %d)",
                  e.getIndex(), pair, pair, map.get(pair)));
        }
      }
    }
  }

  public static BpSeq fromString(final String data) throws InvalidStructureException {
    final List<Entry> entries = new ArrayList<>();

    for (String line : data.split(System.lineSeparator())) {
      line = line.trim();

      final int hash = line.indexOf('#');
      if (hash != -1) {
        line = line.substring(0, hash);
      }

      if (line.isEmpty()) {
        continue;
      }

      final String[] split = BpSeq.WHITESPACE.split(line);

      if ((split.length != 3) || (split[1].length() != 1)) {
        throw new InvalidStructureException(
            String.format("Line does not conform to BPSEQ format: %s", line));
      }

      final int index;
      final int pair;
      final char seq;

      try {
        index = Integer.valueOf(split[0]);
        seq = split[1].charAt(0);
        pair = Integer.valueOf(split[2]);
      } catch (final NumberFormatException e) {
        throw new InvalidStructureException(
            String.format("Line does not conform to BPSEQ format: %s", line), e);
      }

      entries.add(new Entry(index, pair, seq));
    }

    return new BpSeq(entries);
  }

  public static BpSeq fromCt(final Ct ct) throws InvalidStructureException {
    final List<Entry> bpseqEntries = new ArrayList<>();

    for (final Ct.Entry e : ct.getEntries()) {
      bpseqEntries.add(new Entry(e.getIndex(), e.getPair(), e.getSeq()));
    }

    return new BpSeq(bpseqEntries);
  }

  public static BpSeq fromDotBracket(final DotBracketInterface db)
      throws InvalidStructureException {
    final List<Entry> entries = new ArrayList<>();

    for (final DotBracketSymbol symbol : db.getSymbols()) {
      final DotBracketSymbol pair = symbol.getPair();
      final int index = symbol.getIndex() + 1;
      final int pairIndex = (pair != null) ? (pair.getIndex() + 1) : 0;
      final char sequence = symbol.getSequence();

      entries.add(new Entry(index, pairIndex, sequence));
    }

    return new BpSeq(entries);
  }

  public static BpSeq fromResidueCollection(
      final ResidueCollection residueCollection, final Iterable<ClassifiedBasePair> basePairs)
      throws InvalidStructureException {
    final Collection<BasePair> allBasePairs = new ArrayList<>();
    final Map<BasePair, String> basePairToComment = new HashMap<>();

    for (final ClassifiedBasePair classifiedBasePair : basePairs) {
      final BasePair basePair = classifiedBasePair.getBasePair();
      allBasePairs.add(basePair);

      if (classifiedBasePair.isCanonical()) {
        basePairToComment.put(basePair, "");
        basePairToComment.put(basePair.invert(), "");
      } else {
        basePairToComment.put(basePair, classifiedBasePair.generateComment());
        basePairToComment.put(basePair.invert(), classifiedBasePair.invert().generateComment());
      }
    }

    final List<Entry> entries = new ArrayList<>();
    entries.addAll(
        BpSeq.generateEntriesForPaired(residueCollection, allBasePairs, basePairToComment));
    entries.addAll(BpSeq.generateEntriesForUnpaired(residueCollection, allBasePairs));
    return new BpSeq(entries);
  }

  private static Collection<Entry> generateEntriesForPaired(
      final ResidueCollection residueCollection,
      final Iterable<BasePair> basePairs,
      final Map<BasePair, String> basePairToComment) {
    final Collection<Entry> entries = new ArrayList<>();
    final List<PdbResidue> residues = residueCollection.getResidues();

    for (final BasePair basePair : basePairs) {
      final PdbResidue left = residueCollection.findResidue(basePair.getLeft());
      final PdbResidue right = residueCollection.findResidue(basePair.getRight());
      final int indexL = 1 + residues.indexOf(left);
      final int indexR = 1 + residues.indexOf(right);
      entries.add(
          new Entry(indexL, indexR, left.getOneLetterName(), basePairToComment.get(basePair)));
      entries.add(
          new Entry(
              indexR, indexL, right.getOneLetterName(), basePairToComment.get(basePair.invert())));
      BpSeq.LOGGER.trace(
          "Storing pair ({} -> {}) which is ({} -> {})", indexL, indexR, left, right);
    }

    return entries;
  }

  private static Collection<Entry> generateEntriesForUnpaired(
      final ResidueCollection residueCollection, final Iterable<BasePair> allBasePairs) {
    final List<PdbResidue> residues = residueCollection.getResidues();
    final Collection<PdbResidueIdentifier> paired = new HashSet<>();

    for (final BasePair basePair : allBasePairs) {
      paired.add(basePair.getLeft());
      paired.add(basePair.getRight());
    }

    final Collection<Entry> entries = new ArrayList<>();
    for (int i = 0; i < residues.size(); i++) {
      final PdbResidue residue = residues.get(i);
      if (!paired.contains(residue.getResidueIdentifier())) {
        entries.add(new Entry(i + 1, 0, residue.getOneLetterName()));
      }
    }

    return entries;
  }

  public final SortedSet<Entry> getEntries() {
    return Collections.unmodifiableSortedSet(entries);
  }

  public final String getSequence() {
    final StringBuilder builder = new StringBuilder(entries.size());
    for (final Entry e : entries) {
      builder.append(e.getSeq());
    }
    return builder.toString();
  }

  public final SortedSet<Entry> getPaired() {
    final SortedSet<Entry> sortedSet = new TreeSet<>();
    for (final Entry entry : entries) {
      if (entry.getIndex() < entry.getPair()) {
        sortedSet.add(entry);
      }
    }
    return sortedSet;
  }

  public final boolean removePair(final Entry toRemove) {
    if (!toRemove.isPaired()) {
      return false;
    }

    for (final Entry entry : entries) {
      if (entry.getIndex() == toRemove.getPair()) {
        entries.remove(toRemove);
        entries.remove(entry);
        entries.add(new Entry(toRemove.index, 0, toRemove.seq));
        entries.add(new Entry(entry.index, 0, entry.seq));
        return true;
      }
    }
    return false;
  }

  public final int size() {
    return entries.size();
  }

  public final boolean hasAnyPair() {
    for (final Entry entry : entries) {
      if (entry.isPaired()) {
        return true;
      }
    }
    return false;
  }

  public final boolean removeIsolatedPairs() {
    final List<Region> regions = Region.createRegions(this);
    final boolean[] flag = {false};
    regions.forEach(
        region -> {
          if (region.getLength() == 1) {
            flag[0] |= removePair(region.getEntries().get(0));
          }
        });
    return flag[0];
  }

  public static class Entry implements Comparable<Entry>, Serializable {
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

    public Entry(final int index, final int pair, final char seq, final String comment) {
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

    public final boolean contains(final int node) {
      return (node > index) && (node < pair);
    }

    public final int length() {
      return (pair == 0) ? 0 : (pair - index);
    }

    @Override
    public final int compareTo(final Entry t) {
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
    public final boolean equals(final Object o) {
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
      return (index == other.index) && (pair == other.pair) && (seq == other.seq);
    }

    @Override
    public final String toString() {
      final StringBuilder builder = new StringBuilder(10 + comment.length());
      if (BpSeq.WRITE_COMMENTS && StringUtils.isNotBlank(comment)) {
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

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    final BpSeq bpSeq = (BpSeq) o;
    return CollectionUtils.isEqualCollection(entries, bpSeq.entries);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(entries);
  }

  @Override
  public final String toString() {
    final StringBuilder builder = new StringBuilder(10 * entries.size());

    for (final Entry e : entries) {
      builder.append(e);
      builder.append(System.lineSeparator());
    }

    return builder.toString();
  }
}
