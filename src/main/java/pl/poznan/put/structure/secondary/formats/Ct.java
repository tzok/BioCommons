package pl.poznan.put.structure.secondary.formats;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
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

      entries.add(new ExtendedEntry(index, pair, before, after, original, seq));
    }

    return new Ct(entries);
  }

  public static Ct fromBpSeq(final BpSeq bpSeq) {
    final List<ExtendedEntry> ctEntries = new ArrayList<>();
    final SortedSet<BpSeq.Entry> entries = bpSeq.getEntries();
    final int size = entries.size();

    for (final BpSeq.Entry entry : entries) {
      final int index = entry.getIndex();
      final int pair = entry.getPair();
      final char seq = entry.getSeq();
      final String comment = entry.getComment();
      ctEntries.add(
          new ExtendedEntry(index, pair, index - 1, (index + 1) % (size + 1), index, seq, comment));
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
    final SortedSet<BpSeq.Entry> entries = bpSeq.getEntries();
    int i = 0;

    for (final BpSeq.Entry entry : entries) {
      final PdbResidue residue = residues.get(i);
      final PdbChain chain = rna.findChainContainingResidue(residue.toResidueIdentifer());
      final List<PdbResidue> chainResidues = chain.residues();

      final int index = entry.getIndex();
      final int pair = entry.getPair();
      final int before = chainResidues.indexOf(residue);
      final int after = (before + 2) % (chainResidues.size() + 1);
      final int original = residue.residueNumber();
      final char seq = entry.getSeq();
      final String comment = entry.getComment();
      ctEntries.add(new ExtendedEntry(index, pair, before, after, original, seq, comment));

      i += 1;
    }

    return new Ct(ctEntries);
  }

  public static Ct fromDotBracket(final DotBracketInterface dotBracket) {
    final List<ExtendedEntry> entries = new ArrayList<>();

    for (final Strand strand : dotBracket.getStrands()) {
      final List<DotBracketSymbol> symbols = strand.getSymbols();

      for (int i = 0, symbolsSize = symbols.size(); i < symbolsSize; i++) {
        final DotBracketSymbol symbol = symbols.get(i);
        final DotBracketSymbol pair = symbol.getPair();

        final int index = symbol.getIndex() + 1;
        final int pairIndex = (pair != null) ? (pair.getIndex() + 1) : 0;
        final int after = (i == (symbolsSize - 1)) ? 0 : (i + 2);
        final int original = dotBracket.getRealSymbolIndex(symbol);
        final char seq = symbol.getSequence();

        entries.add(new ExtendedEntry(index, pairIndex, i, after, original, seq));
      }
    }

    return new Ct(entries);
  }

  public static void setPrintComments(final boolean printComments) {
    Ct.printComments = printComments;
  }

  public int getStrandCount() {
    return (int) entries.stream().filter(entry -> entry.getAfter() == 0).count();
  }

  public Iterable<ExtendedEntry> getEntries() {
    return Collections.unmodifiableSortedSet(entries);
  }

  public void removeIsolatedPairs() {
    final BpSeq bpSeq = BpSeq.fromCt(this);
    final List<Region> regions = Region.createRegions(bpSeq);
    regions.stream()
        .filter(region -> region.getLength() == 1)
        .mapToInt(region -> region.getEntries().get(0).getIndex())
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
      map.put(e.getIndex(), e.getPair());
    }

    int previous = 0;

    for (final ExtendedEntry e : entries) {
      if ((e.getIndex() - previous) != 1) {
        throw new InvalidStructureException(
            "Inconsistent numbering in CT format: previous="
                + previous
                + ", current="
                + e.getIndex());
      }

      previous = e.getIndex();
      final int pair = map.get(e.getIndex());

      if (pair != 0) {
        if (!map.containsKey(pair)) {
          throw new InvalidStructureException(
              "Inconsistency in CT format: (" + e.getIndex() + " -> " + pair + ')');
        }

        if (map.get(pair) != e.getIndex()) {
          throw new InvalidStructureException(
              String.format(
                  "Inconsistency in CT format: (%d -> %d) and (%d -> %d)",
                  e.getIndex(), pair, pair, map.get(pair)));
        }
      }
    }

    // previous == maximum index

    for (final ExtendedEntry e : entries) {
      if ((e.getBefore() < 0) || (e.getBefore() >= previous)) {
        throw new InvalidStructureException(
            "Inconsistency in CT format. Third column has invalid" + " value in entry: " + e);
      }

      if ((e.getAfter() == 1) || (e.getAfter() < 0) || (e.getAfter() > (previous + 1))) {
        throw new InvalidStructureException(
            "Inconsistency in CT format. Fourth column has " + "invalid value in entry: " + e);
      }
    }

    /*
     * Check if strands' ends are correct
     */
    boolean expectNewStrand = true;

    for (final ExtendedEntry e : entries) {
      if ((e.getBefore() != 0) == expectNewStrand) {
        throw new InvalidStructureException(
            "Inconsistency in CT format. The field 'before' is "
                + "non-zero for the first entry in a strand: "
                + e);
      }

      expectNewStrand = e.getAfter() == 0;
    }

    final ExtendedEntry lastEntry = entries.last();

    if (lastEntry.getAfter() != 0) {
      if (Ct.FIX_LAST_ENTRY) {
        entries.remove(lastEntry);
        entries.add(
            new ExtendedEntry(
                lastEntry.getIndex(),
                lastEntry.getPair(),
                lastEntry.getBefore(),
                0,
                lastEntry.getOriginal(),
                lastEntry.getSeq()));
      } else {
        throw new InvalidStructureException(
            "The field 'after' in the last entry is non-zero: " + lastEntry);
      }
    }
  }

  private void removePair(final int index) {
    final Optional<ExtendedEntry> entry =
        entries.stream().filter(e -> e.getIndex() == index).findFirst();
    final Optional<ExtendedEntry> paired =
        entries.stream().filter(e -> e.getPair() == index).findFirst();

    if (entry.isPresent()) {
      final ExtendedEntry o = entry.get();
      entries.remove(o);
      entries.add(
          new ExtendedEntry(
              o.getIndex(),
              0,
              o.getBefore(),
              o.getAfter(),
              o.getOriginal(),
              o.getSeq(),
              o.getComment()));
    }

    if (paired.isPresent()) {
      final ExtendedEntry o = paired.get();
      entries.remove(o);
      entries.add(
          new ExtendedEntry(
              o.getIndex(),
              0,
              o.getBefore(),
              o.getAfter(),
              o.getOriginal(),
              o.getSeq(),
              o.getComment()));
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static final class ExtendedEntry extends BpSeq.Entry {
    private final int before;
    private final int after;
    private final int original;

    private ExtendedEntry(
        final int index,
        final int pair,
        final int before,
        final int after,
        final int original,
        final char seq) {
      this(index, pair, before, after, original, seq, "");
    }

    private ExtendedEntry(
        final int index,
        final int pair,
        final int before,
        final int after,
        final int original,
        final char seq,
        final String comment) {
      super(index, pair, seq, comment);
      this.before = before;
      this.after = after;
      this.original = original;
    }

    public int getAfter() {
      return after;
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

    private int getBefore() {
      return before;
    }

    private int getOriginal() {
      return original;
    }
  }
}
