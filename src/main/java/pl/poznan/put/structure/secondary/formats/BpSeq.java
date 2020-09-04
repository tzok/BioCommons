package pl.poznan.put.structure.secondary.formats;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.secondary.BasePair;
import pl.poznan.put.structure.secondary.ClassifiedBasePair;
import pl.poznan.put.structure.secondary.DotBracketSymbol;
import pl.poznan.put.structure.secondary.pseudoknots.Region;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@EqualsAndHashCode
@Slf4j
public class BpSeq implements Serializable {
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");
  private static final boolean WRITE_COMMENTS = false;

  private final SortedSet<Entry> entries;

  public BpSeq(final Collection<Entry> entries) {
    super();
    this.entries = new TreeSet<>(entries);
    validate();
  }

  public static BpSeq fromString(final String data) {
    final List<Entry> entries = new ArrayList<>();

    for (String line : data.split("\n")) {
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
        index = Integer.parseInt(split[0]);
        seq = split[1].charAt(0);
        pair = Integer.parseInt(split[2]);
      } catch (final NumberFormatException e) {
        throw new InvalidStructureException(
            String.format("Line does not conform to BPSEQ format: %s", line), e);
      }

      entries.add(new Entry(index, pair, seq));
    }

    return new BpSeq(entries);
  }

  public static BpSeq fromCt(final Ct ct) {
    final List<Entry> bpseqEntries = new ArrayList<>();

    for (final Ct.ExtendedEntry e : ct.getEntries()) {
      bpseqEntries.add(new Entry(e.getIndex(), e.getPair(), e.getSeq()));
    }

    return new BpSeq(bpseqEntries);
  }

  public static BpSeq fromDotBracket(final DotBracketInterface db) {
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
      final List<PdbNamedResidueIdentifier> residues,
      final Iterable<? extends ClassifiedBasePair> basePairs) {
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
    entries.addAll(BpSeq.generateEntriesForPaired(residues, allBasePairs, basePairToComment));
    entries.addAll(BpSeq.generateEntriesForUnpaired(residues, allBasePairs));
    return new BpSeq(entries);
  }

  private static Collection<Entry> generateEntriesForPaired(
      final List<PdbNamedResidueIdentifier> residues,
      final Iterable<? extends BasePair> basePairs,
      final Map<BasePair, String> basePairToComment) {
    final Collection<Entry> entries = new ArrayList<>();

    for (final BasePair basePair : basePairs) {
      final PdbNamedResidueIdentifier left = basePair.getLeft();
      final PdbNamedResidueIdentifier right = basePair.getRight();
      final int indexL = 1 + residues.indexOf(left);
      final int indexR = 1 + residues.indexOf(right);
      entries.add(new Entry(indexL, indexR, left.oneLetterName(), basePairToComment.get(basePair)));
      entries.add(
          new Entry(
              indexR, indexL, right.oneLetterName(), basePairToComment.get(basePair.invert())));
      BpSeq.log.trace("Storing pair ({} -> {}) which is ({} -> {})", indexL, indexR, left, right);
    }

    return entries;
  }

  private static Collection<Entry> generateEntriesForUnpaired(
      final List<PdbNamedResidueIdentifier> residues,
      final Iterable<? extends BasePair> allBasePairs) {
    final Collection<PdbNamedResidueIdentifier> paired = new HashSet<>();

    for (final BasePair basePair : allBasePairs) {
      paired.add(basePair.getLeft());
      paired.add(basePair.getRight());
    }

    final Collection<Entry> entries = new ArrayList<>();
    for (int i = 0; i < residues.size(); i++) {
      final PdbNamedResidueIdentifier residue = residues.get(i);
      if (!paired.contains(residue)) {
        entries.add(new Entry(i + 1, 0, residue.oneLetterName()));
      }
    }

    return entries;
  }

  /*
   * Check if all pairs match.
   */
  private void validate() {
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

  public final SortedSet<Entry> getEntries() {
    return Collections.unmodifiableSortedSet(entries);
  }

  public final String getSequence() {
    return entries.stream().map(e -> String.valueOf(e.getSeq())).collect(Collectors.joining());
  }

  public final SortedSet<Entry> getPaired() {
    return entries.stream()
        .filter(entry -> entry.getIndex() < entry.getPair())
        .collect(Collectors.toCollection(TreeSet::new));
  }

  public final boolean removePair(final Entry toRemove) {
    if (!toRemove.isPaired()) {
      return false;
    }

    for (final Entry entry : entries) {
      if (entry.getIndex() == toRemove.getPair()) {
        entries.remove(toRemove);
        entries.remove(entry);
        entries.add(new Entry(toRemove.getIndex(), 0, toRemove.getSeq()));
        entries.add(new Entry(entry.getIndex(), 0, entry.getSeq()));
        return true;
      }
    }
    return false;
  }

  public final int size() {
    return entries.size();
  }

  public final boolean hasAnyPair() {
    return entries.stream().anyMatch(Entry::isPaired);
  }

  public final boolean removeIsolatedPairs() {
    final List<Region> regions = Region.createRegions(this);
    final boolean[] flag = {false};
    regions.stream()
        .filter(region -> region.getLength() == 1)
        .forEach(region -> flag[0] |= removePair(region.getEntries().get(0)));
    return flag[0];
  }

  @Override
  public final String toString() {
    return entries.stream().map(e -> e + System.lineSeparator()).collect(Collectors.joining());
  }

  @Data
  public static class Entry implements Comparable<Entry>, Serializable {
    protected final int index;
    protected final int pair;
    protected final char seq;
    protected final String comment;

    public Entry(final int index, final int pair, final char seq, final String comment) {
      super();
      this.index = index;
      this.pair = pair;
      this.seq = seq;
      this.comment = comment;
    }

    public Entry(final int index, final int pair, final char seq) {
      this(index, pair, seq, "");
    }

    public boolean isPaired() {
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
      return Integer.compare(index, t.index);
    }

    @Override
    public String toString() {
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
}
