package pl.poznan.put.structure.formats;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.ResidueTypeDetector;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.pseudoknots.Region;

/** RNA secondary structure in BPSEQ format. */
@Value.Immutable
public abstract class BpSeq implements Serializable {
  /**
   * Parses string into an instance of this class.
   *
   * @param data The string with BPSEQ data.
   * @return An instance of this class with parsed data.
   */
  public static BpSeq fromString(final String data) {
    final Collection<Entry> entries =
        Arrays.stream(data.split("\n"))
            .map(String::trim)
            .map(line -> line.indexOf('#') == -1 ? line : line.substring(0, line.indexOf('#')))
            .filter(StringUtils::isNotBlank)
            .map(Entry::fromString)
            .collect(Collectors.toList());
    return ImmutableBpSeq.of(entries);
  }

  /**
   * Converts RNA secondary structure in CT format to BPSEQ format.
   *
   * @param ct The data in CT format.
   * @return An instance of this class with converted data.
   */
  public static BpSeq fromCt(final Ct ct) {
    final List<Entry> entries =
        ct.entries().stream()
            .map(entry -> ImmutableEntry.of(entry.index(), entry.seq(), entry.pair()))
            .collect(Collectors.toList());
    return ImmutableBpSeq.of(entries);
  }

  /**
   * Converts RNA secondary structure in dot-bracket format to BPSEQ format.
   *
   * @param db The data in dot-bracket format.
   * @return An instance of this class with converted data.
   */
  public static BpSeq fromDotBracket(final DotBracket db) {
    final Map<DotBracketSymbol, DotBracketSymbol> pairs = db.pairs();

    final List<Entry> entries =
        db.symbols().stream()
            .map(
                symbol ->
                    ImmutableEntry.of(
                        symbol.index() + 1,
                        symbol.sequence(),
                        pairs.containsKey(symbol) ? pairs.get(symbol).index() + 1 : 0))
            .collect(Collectors.toList());
    return ImmutableBpSeq.of(entries);
  }

  /**
   * Creates an instance of BPSEQ from a list of residue identifiers and a list of base pairs. The
   * PDB chains are automatically reordered so that connecting ones are next to each other.
   *
   * @param residues The list of residue identifiers with names.
   * @param basePairs The list of base pairs.
   * @return An instance of this class.
   */
  public static BpSeq fromBasePairs(
      final List<PdbNamedResidueIdentifier> residues,
      final Collection<? extends ClassifiedBasePair> basePairs) {
    final List<Entry> entries =
        Stream.concat(
                BpSeq.generateEntriesForPaired(residues, basePairs).stream(),
                BpSeq.generateEntriesForUnpaired(residues, basePairs).stream())
            .collect(Collectors.toList());
    return ImmutableBpSeq.of(entries);
  }

  private static Collection<Entry> generateEntriesForPaired(
      final List<PdbNamedResidueIdentifier> residues,
      final Collection<? extends ClassifiedBasePair> basePairs) {
    final Map<BasePair, String> comments =
        basePairs.stream()
            .filter(basePair -> !basePair.isCanonical())
            .flatMap(basePair -> Stream.of(basePair, basePair.invert()))
            .distinct()
            .collect(
                Collectors.toMap(
                    ClassifiedBasePair::basePair, ClassifiedBasePair::generateComment));
    return basePairs.stream()
        .flatMap(basePair -> Stream.of(basePair, basePair.invert()))
        .map(ClassifiedBasePair::basePair)
        .map(
            basePair ->
                ImmutableEntry.of(
                        residues.indexOf(basePair.left()) + 1,
                        basePair.left().oneLetterName(),
                        residues.indexOf(basePair.right()) + 1)
                    .withComment(comments.getOrDefault(basePair, "")))
        .collect(Collectors.toList());
  }

  private static Collection<Entry> generateEntriesForUnpaired(
      final List<PdbNamedResidueIdentifier> residues,
      final Collection<? extends ClassifiedBasePair> basePairs) {
    final Set<PdbNamedResidueIdentifier> paired =
        basePairs.stream()
            .map(ClassifiedBasePair::basePair)
            .flatMap(basePair -> Stream.of(basePair.left(), basePair.right()))
            .collect(Collectors.toSet());
    return IntStream.range(0, residues.size())
        .filter(i -> !paired.contains(residues.get(i)))
        .mapToObj(i -> ImmutableEntry.of(i + 1, residues.get(i).oneLetterName(), 0))
        .collect(Collectors.toList());
  }

  /**
   * @return The list of BPSEQ entries.
   */
  @Value.Parameter(order = 1)
  @Value.NaturalOrder
  public abstract SortedSet<Entry> entries();

  /**
   * @return The sequence of nucleotides stored in this object.
   */
  public final String sequence() {
    return entries().stream().map(e -> String.valueOf(e.seq())).collect(Collectors.joining());
  }

  /**
   * @return The set of paired BPSEQ entries without duplicates. For example, pair (4, 15) will not
   *     be repeated as (15, 4).
   */
  public final SortedSet<Entry> paired() {
    return entries().stream()
        .filter(entry -> entry.index() < entry.pair())
        .collect(Collectors.toCollection(TreeSet::new));
  }

  /**
   * @return The number of BPSEQ entries.
   */
  public final int size() {
    return entries().size();
  }

  /**
   * @return True if at least one BPSEQ entry stands for a pair.
   */
  public final boolean hasAnyPair() {
    return entries().stream().anyMatch(Entry::isPaired);
  }

  /**
   * Finds all isolated base pairs and creates a copy of this instance without them.
   *
   * @return A copy of this instance, but with all isolated base pairs removed.
   */
  public final BpSeq withoutIsolatedPairs() {
    final List<BpSeq.Entry> toRemove =
        Region.createRegions(this).stream()
            .filter(region -> region.length() == 1)
            .map(region -> region.entries().get(0))
            .collect(Collectors.toList());

    BpSeq result = ImmutableBpSeq.copyOf(this);
    for (final BpSeq.Entry entry : toRemove) {
      result = result.withoutPair(entry);
    }
    return result;
  }

  /**
   * Creates a copy of this instance, but with the given pair removed.
   *
   * @param entry The pair to remove.
   * @return A copy of this instance without the given pair.
   */
  public final BpSeq withoutPair(final BpSeq.Entry entry) {
    if (!entry.isPaired()) {
      return ImmutableBpSeq.copyOf(this);
    }

    final SortedSet<Entry> entriesCopy = new TreeSet<>(entries());
    entriesCopy.remove(entry);
    entriesCopy.add(ImmutableEntry.copyOf(entry).withPair(0));

    final Optional<Entry> paired =
        entries().stream().filter(e -> e.index() == entry.pair()).findFirst();

    if (paired.isPresent()) {
      entriesCopy.remove(paired.get());
      entriesCopy.add(ImmutableEntry.copyOf(paired.get()).withPair(0));
    }

    return ImmutableBpSeq.of(entriesCopy);
  }

  @Override
  public final String toString() {
    return entries().stream().map(e -> e + System.lineSeparator()).collect(Collectors.joining());
  }

  @Value.Check
  protected void validate() {
    final Map<Integer, Integer> map =
        entries().stream().collect(Collectors.toMap(Entry::index, Entry::pair));

    int previous = 0;
    for (final Entry entry : entries()) {
      Validate.isTrue(
          entry.index() != entry.pair(),
          "Invalid line in BPSEQ data, a residue cannot be paired with itself! Line: %s",
          entry);
      Validate.isTrue(
          (entry.index() - previous) == 1,
          "Inconsistent numbering in BPSEQ format: previous=%d, current=%d",
          previous,
          entry.index());

      previous = entry.index();
      final int pair = map.get(entry.index());

      if (pair != 0) {
        Validate.isTrue(
            map.containsKey(pair),
            "Inconsistency in BPSEQ format: (%d -> %d)",
            entry.index(),
            pair);
        Validate.isTrue(
            (map.get(pair) == entry.index()),
            "Inconsistency in BPSEQ format: (%d -> %d) and (%d -> %d)",
            entry.index(),
            pair,
            pair,
            map.get(pair));
      }
    }
  }

  /** An entry of a BPSEQ data. */
  @Value.Immutable
  public abstract static class Entry implements Comparable<Entry>, Serializable {
    /**
     * Parses a string into a BPSEQ entry. Expected format: int char int
     *
     * @param line A line containing a BPSEQ entry.
     * @return An instance of this class.
     */
    public static Entry fromString(final String line) {
      final String[] split = StringUtils.split(line);
      if (split.length != 3) {
        throw new IllegalArgumentException("Line does not conform to BPSEQ format: " + line);
      }
      try {
        final int index = Integer.parseInt(split[0]);
        final char seq =
            ResidueTypeDetector.detectResidueType(split[1], Collections.emptySet()).oneLetterName();
        final int pair = Integer.parseInt(split[2]);
        return ImmutableEntry.of(index, seq, pair);
      } catch (final NumberFormatException e) {
        throw new IllegalArgumentException(
            String.format("Line does not conform to BPSEQ format: %s", line), e);
      }
    }

    /**
     * @return The value of index column.
     */
    @Value.Parameter(order = 1)
    public abstract int index();

    /**
     * @return The value of sequence column.
     */
    @Value.Parameter(order = 2)
    public abstract char seq();

    /**
     * @return The value of pair column.
     */
    @Value.Parameter(order = 3)
    public abstract int pair();

    /**
     * @return The optional comment.
     */
    @Value.Default
    public String comment() {
      return "";
    }

    /**
     * @return True if pair column is non-zero.
     */
    public boolean isPaired() {
      return pair() != 0;
    }

    /**
     * Checks for a paired BPSEQ entry (i, j) if a given index k lies between them: i &lt; k &lt; j.
     *
     * @param index The index to check
     * @return True if index lies between this entry and its pair.
     */
    public final boolean contains(final int index) {
      return (index > index()) && (index < pair());
    }

    /**
     * @return The difference between pair column and index column or 0 for unpaired entries.
     */
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
      builder.append(index());
      builder.append(' ');
      builder.append(seq());
      builder.append(' ');
      builder.append(pair());
      return builder.toString();
    }
  }
}
