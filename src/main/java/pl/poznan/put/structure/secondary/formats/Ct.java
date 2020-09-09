package pl.poznan.put.structure.secondary.formats;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.StructureModel;
import pl.poznan.put.structure.secondary.DotBracketSymbol;
import pl.poznan.put.structure.secondary.pseudoknots.Region;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Ct implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(Ct.class);
  private static final boolean FIX_LAST_ENTRY = true;
  private static boolean printComments = true;
  private final SortedSet<ExtendedEntry> entries;

  private Ct(final List<ExtendedEntry> entries) {
    super();
    this.entries = new TreeSet<>(entries);
    validate();
  }

  public static Ct fromString(final String data) {
    final List<ExtendedEntry> entries = new ArrayList<>();
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
                "Invalid CT format. Line count < 0 detected: " + line);
          }
        } catch (final NumberFormatException e) {
          throw new InvalidStructureException(
              "Invalid CT format. Failed to parse line count: " + line, e);
        }
        firstLine = false;
        continue;
      }

      if (split.length != 6) {
        throw new InvalidStructureException(
            "Invalid CT format. Six columns not found in line: " + line);
      }

      final int index;
      final int pair;
      final int before;
      final int after;
      final int original;
      final char seq;

      try {
        index = Integer.parseInt(split[0]);
        seq = split[1].charAt(0);
        before = Integer.parseInt(split[2]);
        after = Integer.parseInt(split[3]);
        pair = Integer.parseInt(split[4]);
        original = Integer.parseInt(split[5]);
      } catch (final NumberFormatException e) {
        throw new InvalidStructureException(
            "Invalid CT format. Failed to parse column values: " + line, e);
      }

      entries.add(ImmutableExtendedEntry.of(index, pair, before, after, original, seq));
    }

    return new Ct(entries);
  }

  public static Ct fromBpSeq(final BpSeq bpSeq) {
    final List<ExtendedEntry> ctEntries = new ArrayList<>();
    final SortedSet<BpSeq.Entry> entries = bpSeq.entries();
    final int size = entries.size();

    for (final BpSeq.Entry entry : entries) {
      final int index = entry.index();
      final int pair = entry.pair();
      final char seq = entry.seq();
      final String comment = entry.comment();
      ctEntries.add(
          ImmutableExtendedEntry.builder()
              .index(index)
              .pair(pair)
              .before(index - 1)
              .after((index + 1) % (size + 1))
              .original(index)
              .seq(seq)
              .comment(comment)
              .build());
    }

    return new Ct(ctEntries);
  }

  public static Ct fromBpSeqAndPdbModel(final BpSeq bpSeq, final StructureModel model) {
    final StructureModel rna;
    try {
      rna = model.filteredNewInstance(MoleculeType.RNA);
    } catch (final PdbParsingException e) {
      throw new InvalidStructureException("Failed to filter RNA chains", e);
    }

    final List<ExtendedEntry> ctEntries = new ArrayList<>();
    final List<PdbResidue> residues = rna.residues();
    final SortedSet<BpSeq.Entry> entries = bpSeq.entries();
    int i = 0;

    for (final BpSeq.Entry entry : entries) {
      final PdbResidue residue = residues.get(i);
      final PdbChain chain = rna.findChainContainingResidue(residue.toResidueIdentifer());
      final List<PdbResidue> chainResidues = chain.residues();

      final int index = entry.index();
      final int pair = entry.pair();
      final int before = chainResidues.indexOf(residue);
      final int after = (before + 2) % (chainResidues.size() + 1);
      final int original = residue.residueNumber();
      final char seq = entry.seq();
      final String comment = entry.comment();
      ctEntries.add(
          ImmutableExtendedEntry.builder()
              .index(index)
              .pair(pair)
              .before(before)
              .after(after)
              .original(original)
              .seq(seq)
              .comment(comment)
              .build());

      i += 1;
    }

    return new Ct(ctEntries);
  }

  public static Ct fromDotBracket(final DotBracket dotBracket) {
    final List<ExtendedEntry> entries = new ArrayList<>();

    for (final Strand strand : dotBracket.strands()) {
      final List<DotBracketSymbol> symbols = strand.symbols();

      for (int i = 0, symbolsSize = symbols.size(); i < symbolsSize; i++) {
        final DotBracketSymbol symbol = symbols.get(i);
        final Optional<DotBracketSymbol> pair = symbol.pair();

        final int index = symbol.index() + 1;
        final int pairIndex =
            pair.map(dotBracketSymbol -> (dotBracketSymbol.index() + 1)).orElse(0);
        final int after = (i == (symbolsSize - 1)) ? 0 : (i + 2);
        final int original = dotBracket.getRealSymbolIndex(symbol);
        final char seq = symbol.sequence();

        entries.add(ImmutableExtendedEntry.of(index, pairIndex, i, after, original, seq));
      }
    }

    return new Ct(entries);
  }

  public static void setPrintComments(final boolean printComments) {
    Ct.printComments = printComments;
  }

  public int getStrandCount() {
    return (int) entries.stream().filter(entry -> entry.after() == 0).count();
  }

  public Iterable<ExtendedEntry> getEntries() {
    return Collections.unmodifiableSortedSet(entries);
  }

  public void removeIsolatedPairs() {
    final BpSeq bpSeq = BpSeq.fromCt(this);
    final List<Region> regions = Region.createRegions(bpSeq);
    regions.stream()
        .filter(region -> region.getLength() == 1)
        .mapToInt(region -> region.getEntries().get(0).index())
        .forEach(this::removePair);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(entries.size());
    builder.append('\n');

    for (final ExtendedEntry e : entries) {
      builder.append(e);
      builder.append('\n');
    }

    return builder.toString();
  }

  /*
   * Check if all pairs match.
   */
  private void validate() {
    if (Ct.LOGGER.isTraceEnabled()) {
      Ct.LOGGER.trace("CT to be validated:\n{}", this);
    }

    final Map<Integer, Integer> map = new HashMap<>();

    for (final ExtendedEntry e : entries) {
      map.put(e.index(), e.pair());
    }

    int previous = 0;

    for (final ExtendedEntry e : entries) {
      if ((e.index() - previous) != 1) {
        throw new InvalidStructureException(
            "Inconsistent numbering in CT format: previous=" + previous + ", current=" + e.index());
      }

      previous = e.index();
      final int pair = map.get(e.index());

      if (pair != 0) {
        if (!map.containsKey(pair)) {
          throw new InvalidStructureException(
              "Inconsistency in CT format: (" + e.index() + " -> " + pair + ')');
        }

        if (map.get(pair) != e.index()) {
          throw new InvalidStructureException(
              String.format(
                  "Inconsistency in CT format: (%d -> %d) and (%d -> %d)",
                  e.index(), pair, pair, map.get(pair)));
        }
      }
    }

    // previous == maximum index

    for (final ExtendedEntry e : entries) {
      if ((e.before() < 0) || (e.before() >= previous)) {
        throw new InvalidStructureException(
            "Inconsistency in CT format. Third column has invalid" + " value in entry: " + e);
      }

      if ((e.after() == 1) || (e.after() < 0) || (e.after() > (previous + 1))) {
        throw new InvalidStructureException(
            "Inconsistency in CT format. Fourth column has " + "invalid value in entry: " + e);
      }
    }

    /*
     * Check if strands' ends are correct
     */
    boolean expectNewStrand = true;

    for (final ExtendedEntry e : entries) {
      if ((e.before() != 0) == expectNewStrand) {
        throw new InvalidStructureException(
            "Inconsistency in CT format. The field 'before' is "
                + "non-zero for the first entry in a strand: "
                + e);
      }

      expectNewStrand = e.after() == 0;
    }

    final ExtendedEntry lastEntry = entries.last();

    if (lastEntry.after() != 0) {
      if (Ct.FIX_LAST_ENTRY) {
        entries.remove(lastEntry);
        entries.add(
            ImmutableExtendedEntry.of(
                lastEntry.index(),
                lastEntry.pair(),
                lastEntry.before(),
                0,
                lastEntry.original(),
                lastEntry.seq()));
      } else {
        throw new InvalidStructureException(
            "The field 'after' in the last entry is non-zero: " + lastEntry);
      }
    }
  }

  private void removePair(final int index) {
    final Optional<ExtendedEntry> entry =
        entries.stream().filter(e -> e.index() == index).findFirst();
    final Optional<ExtendedEntry> paired =
        entries.stream().filter(e -> e.pair() == index).findFirst();

    if (entry.isPresent()) {
      final ExtendedEntry o = entry.get();
      entries.remove(o);
      entries.add(
          ImmutableExtendedEntry.builder()
              .index(o.index())
              .pair(0)
              .before(o.before())
              .after(o.after())
              .original(o.original())
              .seq(o.seq())
              .comment(o.comment())
              .build());
    }

    if (paired.isPresent()) {
      final ExtendedEntry o = paired.get();
      entries.remove(o);
      entries.add(
          ImmutableExtendedEntry.builder()
              .index(o.index())
              .pair(0)
              .before(o.before())
              .after(o.after())
              .original(o.original())
              .seq(o.seq())
              .comment(o.comment())
              .build());
    }
  }

  @Value.Immutable
  public abstract static class ExtendedEntry implements Comparable<ExtendedEntry> {
    @Value.Parameter(order = 1)
    public abstract int index();

    @Value.Parameter(order = 2)
    public abstract int pair();

    @Value.Parameter(order = 3)
    public abstract int before();

    @Value.Parameter(order = 4)
    public abstract int after();

    @Value.Parameter(order = 5)
    public abstract int original();

    @Value.Parameter(order = 6)
    public abstract char seq();

    @Value.Default
    public String comment() {
      return "";
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
      if (Ct.printComments && !StringUtils.isBlank(comment())) {
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
