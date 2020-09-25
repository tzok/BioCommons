package pl.poznan.put.structure.formats;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.pseudoknots.Region;
import pl.poznan.put.structure.ClassifiedBasePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class BpSeq implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(BpSeq.class);
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");
  private static final boolean WRITE_COMMENTS = false;

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

      entries.add(ImmutableEntry.of(index, pair, seq));
    }

    return ImmutableBpSeq.of(entries);
  }

  public static BpSeq fromCt(final Ct ct) {
    final List<Entry> bpseqEntries = new ArrayList<>();

    for (final Ct.ExtendedEntry e : ct.getEntries()) {
      bpseqEntries.add(ImmutableEntry.of(e.index(), e.pair(), e.seq()));
    }

    return ImmutableBpSeq.of(bpseqEntries);
  }

  public static BpSeq fromDotBracket(final DotBracket db) {
    final List<Entry> entries = new ArrayList<>();

    for (final DotBracketSymbol symbol : db.symbols()) {
      final Optional<DotBracketSymbol> pair = symbol.pair();
      final int index = symbol.index() + 1;
      final int pairIndex = pair.map(dotBracketSymbol -> (dotBracketSymbol.index() + 1)).orElse(0);
      final char sequence = symbol.sequence();

      entries.add(ImmutableEntry.of(index, pairIndex, sequence));
    }

    return ImmutableBpSeq.of(entries);
  }

  public static BpSeq fromResidueCollection(
      final List<PdbNamedResidueIdentifier> residues,
      final Iterable<? extends ClassifiedBasePair> basePairs) {
    final Collection<BasePair> allBasePairs = new ArrayList<>();
    final Map<BasePair, String> basePairToComment = new HashMap<>();

    for (final ClassifiedBasePair classifiedBasePair : basePairs) {
      final BasePair basePair = classifiedBasePair.basePair();
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
    return ImmutableBpSeq.of(entries);
  }

  private static Collection<Entry> generateEntriesForPaired(
      final List<PdbNamedResidueIdentifier> residues,
      final Iterable<BasePair> basePairs,
      final Map<BasePair, String> basePairToComment) {
    final Collection<Entry> entries = new ArrayList<>();

    for (final BasePair basePair : basePairs) {
      final PdbNamedResidueIdentifier left = basePair.getLeft();
      final PdbNamedResidueIdentifier right = basePair.getRight();
      final int indexL = 1 + residues.indexOf(left);
      final int indexR = 1 + residues.indexOf(right);
      entries.add(
          ImmutableEntry.builder()
              .index(indexL)
              .pair(indexR)
              .seq(left.oneLetterName())
              .comment(basePairToComment.get(basePair))
              .build());
      entries.add(
          ImmutableEntry.builder()
              .index(indexR)
              .pair(indexL)
              .seq(right.oneLetterName())
              .comment(basePairToComment.get(basePair.invert()))
              .build());
      BpSeq.LOGGER.trace(
          "Storing pair ({} -> {}) which is ({} -> {})", indexL, indexR, left, right);
    }

    return entries;
  }

  private static Collection<Entry> generateEntriesForUnpaired(
      final List<PdbNamedResidueIdentifier> residues, final Iterable<BasePair> allBasePairs) {
    final Collection<PdbNamedResidueIdentifier> paired = new HashSet<>();

    for (final BasePair basePair : allBasePairs) {
      paired.add(basePair.getLeft());
      paired.add(basePair.getRight());
    }

    final Collection<Entry> entries = new ArrayList<>();
    for (int i = 0; i < residues.size(); i++) {
      final PdbNamedResidueIdentifier residue = residues.get(i);
      if (!paired.contains(residue)) {
        entries.add(ImmutableEntry.of(i + 1, 0, residue.oneLetterName()));
      }
    }

    return entries;
  }

  @Value.Parameter(order = 1)
  @Value.NaturalOrder
  public abstract SortedSet<Entry> entries();

  public final String getSequence() {
    return entries().stream().map(e -> String.valueOf(e.seq())).collect(Collectors.joining());
  }

  public final SortedSet<Entry> getPaired() {
    return entries().stream()
        .filter(entry -> entry.index() < entry.pair())
        .collect(Collectors.toCollection(TreeSet::new));
  }

  public final int size() {
    return entries().size();
  }

  public final boolean hasAnyPair() {
    return entries().stream().anyMatch(Entry::isPaired);
  }

  public final BpSeq withoutIsolatedPairs() {
    final List<BpSeq.Entry> toRemove =
        Region.createRegions(this).stream()
            .filter(region -> region.getLength() == 1)
            .map(region -> region.getEntries().get(0))
            .collect(Collectors.toList());

    BpSeq result = ImmutableBpSeq.copyOf(this);
    for (final BpSeq.Entry entry : toRemove) {
      result = result.withoutPair(entry);
    }
    return result;
  }

  public final BpSeq withoutPair(final BpSeq.Entry toRemove) {
    if (toRemove.isPaired()) {
      final SortedSet<Entry> entriesCopy = new TreeSet<>(entries());

      for (final Entry entry : entries()) {
        if (entry.index() == toRemove.pair()) {
          entriesCopy.remove(toRemove);
          entriesCopy.remove(entry);
          entriesCopy.add(ImmutableEntry.of(toRemove.index(), 0, toRemove.seq()));
          entriesCopy.add(ImmutableEntry.of(entry.index(), 0, entry.seq()));
          return ImmutableBpSeq.of(entriesCopy);
        }
      }
    }
    return ImmutableBpSeq.copyOf(this);
  }

  @Override
  public final String toString() {
    return entries().stream().map(e -> e + System.lineSeparator()).collect(Collectors.joining());
  }

  /*
   * Check if all pairs match.
   */
  @Value.Check
  protected void validate() {
    final Map<Integer, Integer> map = new HashMap<>();

    for (final Entry entry : entries()) {
      Validate.isTrue(
          entry.index() != entry.pair(),
          String.format(
              "Invalid line in BPSEQ data, a residue cannot be paired with itself! Line: %s",
              entry));

      map.put(entry.index(), entry.pair());
    }

    int previous = 0;

    for (final Entry e : entries()) {
      Validate.isTrue(
          (e.index() - previous) == 1,
          String.format(
              "Inconsistent numbering in BPSEQ format: previous=%d, current=%d",
              previous, e.index()));
      previous = e.index();

      final int pair = map.get(e.index());
      if (pair != 0) {
        Validate.isTrue(
            map.containsKey(pair),
            String.format("Inconsistency in BPSEQ format: (%d -> %d)", e.index(), pair));
        Validate.isTrue(
            (map.get(pair) == e.index()),
            String.format(
                "Inconsistency in BPSEQ format: (%d -> %d) and " + "(%d -> %d)",
                e.index(), pair, pair, map.get(pair)));
      }
    }
  }

  @Value.Immutable
  public abstract static class Entry implements Comparable<Entry>, Serializable {
    @Value.Parameter(order = 1)
    public abstract int index();

    @Value.Parameter(order = 2)
    public abstract int pair();

    @Value.Parameter(order = 3)
    public abstract char seq();

    @Value.Default
    public String comment() {
      return "";
    }

    public boolean isPaired() {
      return pair() != 0;
    }

    public final boolean contains(final int node) {
      return (node > index()) && (node < pair());
    }

    public final int length() {
      return (pair() == 0) ? 0 : (pair() - index());
    }

    @Override
    public final int compareTo(final Entry t) {
      return Integer.compare(index(), t.index());
    }

    @Override
    public String toString() {
      final StringBuilder builder = new StringBuilder(10 + comment().length());
      if (BpSeq.WRITE_COMMENTS && StringUtils.isNotBlank(comment())) {
        builder.append('#');
        builder.append(comment());
        builder.append(System.lineSeparator());
      }
      builder.append(index());
      builder.append(' ');
      builder.append(seq());
      builder.append(' ');
      builder.append(pair());
      return builder.toString();
    }
  }
}
