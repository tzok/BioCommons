package pl.poznan.put.structure.formats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.SingleTypedResidueCollection;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.pseudoknots.Region;

/** An RNA secondary structure encoded in CT (connect) format. */
@Value.Immutable
@JsonSerialize(as = ImmutableCt.class)
@JsonDeserialize(as = ImmutableCt.class)
public abstract class Ct implements Serializable {
  /**
   * Parses string into an instance of this class.
   *
   * @param data The string with CT data.
   * @return An instance of this class with parsed data.
   */
  public static Ct fromString(final String data) {
    final List<ExtendedEntry> entries =
        Arrays.stream(data.split("\n"))
            .map(String::trim)
            .map(line -> line.indexOf('#') == -1 ? line : line.substring(0, line.indexOf('#')))
            .filter(StringUtils::isNotBlank)
            .skip(1L)
            .map(ExtendedEntry::fromString)
            .collect(Collectors.toList());
    return ImmutableCt.of(entries);
  }

  /**
   * Converts RNA secondary structure in BPSEQ format to CT format.
   *
   * @param bpSeq The data in BPSEQ format.
   * @return An instance of this class with converted data.
   */
  public static Ct fromBpSeq(final BpSeq bpSeq) {
    final List<ExtendedEntry> entries =
        bpSeq.entries().stream().map(ExtendedEntry::fromEntry).collect(Collectors.toList());
    return ImmutableCt.of(entries);
  }

  /**
   * Converts RNA secondary structure in BPSEQ format to CT format, taking into account information
   * from 3D coordinates (residue numbering, chain sizes).
   *
   * @param bpSeq The data in BPSEQ format.
   * @param model The 3D data.
   * @return An instance of this class with converted data.
   */
  public static Ct fromBpSeqAndPdbModel(final BpSeq bpSeq, final PdbModel model) {
    final List<PdbResidue> residues =
        model.chains().stream()
            .filter(chain -> chain.moleculeType() == MoleculeType.RNA)
            .map(PdbChain::residues)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    final List<BpSeq.Entry> entries = new ArrayList<>(bpSeq.entries());

    if (residues.size() != entries.size()) {
      throw new IllegalArgumentException(
          String.format(
              "Failed to create CT from BPSEQ and PDB data, because there are %d BPSEQ entries and"
                  + " %d residues",
              entries.size(), residues.size()));
    }

    final List<ExtendedEntry> extendedEntries = new ArrayList<>();

    for (int i = 0; i < residues.size(); i++) {
      final PdbResidue residue = residues.get(i);
      final BpSeq.Entry entry = entries.get(i);
      final SingleTypedResidueCollection chain = model.findChainContainingResidue(residue);
      final int before = chain.indexOf(residue) == 0 ? 0 : entry.index() - 1;
      final int after =
          chain.indexOf(residue) == chain.residues().size() - 1 ? 0 : entry.index() + 1;
      extendedEntries.add(
          ImmutableExtendedEntry.of(
                  entry.index(), entry.seq(), before, after, entry.pair(), residue.residueNumber())
              .withComment(entry.comment()));
    }

    return ImmutableCt.of(extendedEntries);
  }

  /**
   * Converts RNA secondary structure in dot-bracket format to CT format.
   *
   * @param dotBracket The data in dot-bracket format.
   * @return An instance of this class with converted data.
   */
  public static Ct fromDotBracket(final DotBracket dotBracket) {
    final Map<DotBracketSymbol, DotBracketSymbol> pairs = dotBracket.pairs();
    final List<ExtendedEntry> entries = new ArrayList<>();
    int index = 1;

    for (final Strand strand : dotBracket.strands()) {
      final List<DotBracketSymbol> symbols = strand.symbols();

      for (int i = 0; i < symbols.size(); i++) {
        final boolean first = i == 0;
        final boolean last = i == symbols.size() - 1;

        final DotBracketSymbol symbol = symbols.get(i);
        final int pair =
            pairs.containsKey(symbol) ? dotBracket.symbols().indexOf(pairs.get(symbol)) + 1 : 0;
        entries.add(
            ImmutableExtendedEntry.of(
                index,
                symbol.sequence(),
                first ? 0 : index - 1,
                last ? 0 : index + 1,
                pair,
                dotBracket.originalIndex(symbol)));
        index++;
      }
    }
    return ImmutableCt.of(entries);
  }

  /**
   * @return The list of CT entries.
   */
  @Value.Parameter(order = 1)
  @Value.NaturalOrder
  public abstract SortedSet<ExtendedEntry> entries();

  /**
   * @return The number of strands.
   */
  public final int strandCount() {
    return (int) entries().stream().filter(entry -> entry.after() == 0).count();
  }

  /**
   * Creates a copy of this instance, but with the given pair removed.
   *
   * @param entry The pair to remove.
   * @return A copy of this instance without the given pair.
   */
  public final Ct withoutPair(final ExtendedEntry entry) {
    if (!entry.isPaired()) {
      return ImmutableCt.copyOf(this);
    }

    final SortedSet<ExtendedEntry> entrySet = new TreeSet<>(entries());
    entrySet.remove(entry);
    entrySet.add(ImmutableExtendedEntry.copyOf(entry).withPair(0));

    final Optional<ExtendedEntry> paired =
        entries().stream().filter(e -> e.pair() == entry.index()).findFirst();

    if (paired.isPresent()) {
      entrySet.remove(paired.get());
      entrySet.add(ImmutableExtendedEntry.copyOf(paired.get()).withPair(0));
    }

    return ImmutableCt.of(entrySet);
  }

  /**
   * Finds all isolated base pairs and creates a copy of this instance without them.
   *
   * @return A copy of this instance, but with all isolated base pairs removed.
   */
  public final Ct withoutIsolatedPairs() {
    Ct copy = ImmutableCt.copyOf(this);
    for (final Region region : Region.createRegions(BpSeq.fromCt(this))) {
      if (region.length() == 1) {
        final Optional<ExtendedEntry> entry =
            entries().stream()
                .filter(e -> e.index() == region.entries().get(0).index())
                .findFirst();
        if (entry.isPresent()) {
          copy = copy.withoutPair(entry.get());
        }
      }
    }
    return copy;
  }

  @Override
  public final String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(entries().size());
    builder.append('\n');

    for (final ExtendedEntry e : entries()) {
      builder.append(e);
      builder.append('\n');
    }

    return builder.toString();
  }

  @Value.Check
  protected Ct validate() {
    Validate.isTrue(!entries().isEmpty(), "Invalid CT (empty entries list)");

    final List<ExtendedEntry> list = new ArrayList<>(entries());

    // fix the first entry if required
    final ExtendedEntry firstEntry = list.get(0);
    if (firstEntry.before() != 0) {
      list.remove(0);
      list.add(0, ImmutableExtendedEntry.copyOf(firstEntry).withBefore(0));
      return ImmutableCt.of(list);
    }

    // fix the last entry if required
    final ExtendedEntry lastEntry = list.get(list.size() - 1);
    if (lastEntry.after() != 0) {
      list.remove(list.size() - 1);
      list.add(ImmutableExtendedEntry.copyOf(lastEntry).withAfter(0));
      return ImmutableCt.of(list);
    }

    // fix the `before` and `after` columns if required
    final var toRecreate =
        list.stream().filter(ExtendedEntry::hasInvalidBeforeOrAfter).collect(Collectors.toList());
    if (!toRecreate.isEmpty()) {
      for (final var entry : toRecreate) {
        final var before = entry.before() == 0 ? 0 : entry.index() - 1;
        final var after = entry.after() == 0 ? 0 : entry.index() + 1;
        list.remove(entry);
        list.add(ImmutableExtendedEntry.copyOf(entry).withBefore(before).withAfter(after));
      }
      return ImmutableCt.of(list);
    }

    // sanity check
    Validate.isTrue(
        firstEntry.index() == 1,
        "Invalid `index` column in the first entry (not equal to one):%n  %s",
        firstEntry);
    Validate.isTrue(
        firstEntry.before() == 0,
        "Invalid `before` column in the first entry (not zero):%n  %s",
        firstEntry);
    Validate.isTrue(
        firstEntry.after() >= 0,
        "Invalid `after` column in the first entry (negative):%n  %s",
        firstEntry);

    for (int i = 1; i < list.size(); i++) {
      final ExtendedEntry previous = list.get(i - 1);
      final ExtendedEntry current = list.get(i);

      // sanity check
      Validate.isTrue(current.index() > 0, "Invalid `index` column (non-positive):%n  %s", current);
      Validate.isTrue(current.before() >= 0, "Invalid `before` column (negative):%n  %s", current);
      Validate.isTrue(current.after() >= 0, "Invalid `after` column (negative):%n  %s", current);

      // check correctness of `index` column
      Validate.isTrue(
          current.index() - previous.index() == 1,
          "Invalid `index` column (expected next value than its predecessor):%n  %s%n  %s",
          previous,
          current);

      // check correctness of `before` column
      if (current.before() == 0) {
        Validate.isTrue(
            previous.after() == 0,
            "Invalid `before` column (expected 0 for new strand):%n  %s%n  %s",
            previous,
            current);
      }

      // check correctness of `after` column
      if (previous.after() == 0) {
        Validate.isTrue(
            current.before() == 0,
            "Invalid `before` column (expected 0 for new strand):%n  %s%n  %s",
            previous,
            current);
      }
    }

    final Map<Integer, Integer> map =
        entries().stream().collect(Collectors.toMap(ExtendedEntry::index, ExtendedEntry::pair));
    final int lastIndex = lastEntry.index();

    for (final ExtendedEntry entry : list) {
      if (entry.pair() != 0) {
        // checks on `pair` column
        Validate.isTrue(map.containsKey(entry.index()), "Missing mapping for:%n  %s", entry);
        Validate.isTrue(map.containsKey(entry.pair()), "Missing mapping for:%n  %s", entry);
        Validate.isTrue(
            map.get(entry.index()) == entry.pair(),
            "Incorrect mapping:%n  %s%n  mapping[entry.index]=%d",
            entry,
            map.get(entry.index()));
        Validate.isTrue(
            map.get(entry.pair()) == entry.index(),
            "Incorrect mapping:%n  %s%n  mapping[entry.pair]=%d",
            entry,
            map.get(entry.pair()));
      }
    }

    return this;
  }

  /** A single entry in the CT formatted structure. */
  @Value.Immutable
  @JsonSerialize(as = ImmutableExtendedEntry.class)
  @JsonDeserialize(as = ImmutableExtendedEntry.class)
  public abstract static class ExtendedEntry implements Comparable<ExtendedEntry> {
    /**
     * Creates an instance from a string in format: int string int int int int.
     *
     * @param line A line of text formatted as a CT content line.
     * @return An instance of this class.
     */
    public static ExtendedEntry fromString(final String line) {
      final String[] split = StringUtils.split(line);
      if (split.length != 6) {
        throw new IllegalArgumentException("Line does not conform to CT format: " + line);
      }
      try {
        final int index = Integer.parseInt(split[0]);
        final char seq = split[1].charAt(0);
        final int before = Integer.parseInt(split[2]);
        final int after = Integer.parseInt(split[3]);
        final int pair = Integer.parseInt(split[4]);
        final int original = Integer.parseInt(split[5]);
        return ImmutableExtendedEntry.of(index, seq, before, after, pair, original);
      } catch (final NumberFormatException e) {
        throw new IllegalArgumentException(
            "Invalid CT format. Failed to parse column values: " + line, e);
      }
    }

    /**
     * Converts a BPSEQ entry into an instance of this class.
     *
     * @param entry A BPSEQ entry to convert.
     * @return An instance of this class.
     */
    public static ExtendedEntry fromEntry(final BpSeq.Entry entry) {
      return ImmutableExtendedEntry.of(
          entry.index(),
          entry.seq(),
          entry.index() - 1,
          entry.index() + 1,
          entry.pair(),
          entry.index());
    }

    /**
     * @return The value of `index` column.
     */
    @Value.Parameter(order = 1)
    public abstract int index();

    /**
     * @return The value of `seq` column.
     */
    @Value.Parameter(order = 2)
    public abstract char seq();

    /**
     * @return The value of `before` column.
     */
    @Value.Parameter(order = 3)
    public abstract int before();

    /**
     * @return The value of `after` column.
     */
    @Value.Parameter(order = 4)
    public abstract int after();

    /**
     * @return The value of `pair` column.
     */
    @Value.Parameter(order = 5)
    public abstract int pair();

    /**
     * @return The value of `original` column.
     */
    @Value.Parameter(order = 6)
    public abstract int original();

    /**
     * @return An optional comment.
     */
    @Value.Default
    public String comment() {
      return "";
    }

    /**
     * @return True if `pair` column is non-zero.
     */
    public boolean isPaired() {
      return pair() != 0;
    }

    /**
     * @return True if `before` or `after` column are not correct.
     */
    public boolean hasInvalidBeforeOrAfter() {
      return (before() != 0 && before() != index() - 1) || (after() != 0 && after() != index() + 1);
    }

    @Override
    public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append(index());
      builder.append(' ');
      builder.append(seq());
      builder.append(' ');
      builder.append(before());
      builder.append(' ');
      builder.append(after());
      builder.append(' ');
      builder.append(pair());
      builder.append(' ');
      builder.append(original());
      if (!StringUtils.isBlank(comment())) {
        builder.append(" # ");
        builder.append(comment());
      }
      return builder.toString();
    }

    @Override
    public int compareTo(final ExtendedEntry t) {
      return Integer.compare(index(), t.index());
    }
  }
}
