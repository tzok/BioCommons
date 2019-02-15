package pl.poznan.put.structure.secondary.formats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.structure.secondary.DotBracketSymbol;
import pl.poznan.put.structure.secondary.pseudoknots.Region;

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
      Ct.LOGGER.trace("CT to be validated:\n{}", this);
    }

    final Map<Integer, Integer> map = new HashMap<>();

    for (final Entry e : entries) {
      map.put(e.index, e.pair);
    }

    int previous = 0;

    for (final Entry e : entries) {
      if ((e.index - previous) != 1) {
        throw new InvalidStructureException(
            "Inconsistent numbering in CT format: previous=" + previous + ", current" + e.index);
      }

      previous = e.index;
      final int pair = map.get(e.index);

      if (pair != 0) {
        if (!map.containsKey(pair)) {
          throw new InvalidStructureException(
              "Inconsistency in CT format: (" + e.index + " -> " + pair + ')');
        }

        if (map.get(pair) != e.index) {
          throw new InvalidStructureException(
              String.format(
                  "Inconsistency in CT format: (%d -> %d) and (%d -> %d)",
                  e.index, pair, pair, map.get(pair)));
        }
      }
    }

    // previous == maximum index

    for (final Entry e : entries) {
      if ((e.before < 0) || (e.before >= previous)) {
        throw new InvalidStructureException(
            "Inconsistency in CT format. Third column has invalid" + " value in entry: " + e);
      }

      if ((e.after == 1) || (e.after < 0) || (e.after > (previous + 1))) {
        throw new InvalidStructureException(
            "Inconsistency in CT format. Fourth column has " + "invalid value in entry: " + e);
      }
    }

    /*
     * Check if strands' ends are correct
     */
    boolean expectNewStrand = true;
    Entry prevEntry = null;

    for (final Entry e : entries) {
      if (e.getBefore() != 0 ? expectNewStrand : !expectNewStrand) {
        throw new InvalidStructureException(
            "Inconsistency in CT format. The field 'before' is "
                + "non-zero for the first entry in a strand: "
                + e);
      }

      if ((prevEntry != null)
          && (prevEntry.getAfter() != 0 ? expectNewStrand : !expectNewStrand)) {
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
        entries.add(
            new Entry(
                lastEntry.index,
                lastEntry.pair,
                lastEntry.before,
                0,
                lastEntry.original,
                lastEntry.seq));
      } else {
        throw new InvalidStructureException(
            "The field 'after' in the last entry is non-zero: " + lastEntry);
      }
    }
  }

  public static Ct fromString(final String data) throws InvalidStructureException {
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
        index = Integer.valueOf(split[0]);
        seq = split[1].charAt(0);
        before = Integer.valueOf(split[2]);
        after = Integer.valueOf(split[3]);
        pair = Integer.valueOf(split[4]);
        original = Integer.valueOf(split[5]);
      } catch (final NumberFormatException e) {
        throw new InvalidStructureException(
            "Invalid CT format. Failed to parse column values: " + line, e);
      }

      entries.add(new Entry(index, pair, before, after, original, seq));
    }

    return new Ct(entries);
  }

  public static Ct fromBpSeq(final BpSeq bpSeq) throws InvalidStructureException {
    final List<Entry> ctEntries = new ArrayList<>();
    final SortedSet<BpSeq.Entry> entries = bpSeq.getEntries();
    final int size = entries.size();

    for (final BpSeq.Entry entry : entries) {
      final int index = entry.getIndex();
      final int pair = entry.getPair();
      final char seq = entry.getSeq();
      final String comment = entry.getComment();
      ctEntries.add(
          new Entry(index, pair, index - 1, (index + 1) % (size + 1), index, seq, comment));
    }

    return new Ct(ctEntries);
  }

  public static Ct fromBpSeqAndPdbModel(final BpSeq bpSeq, final PdbModel model)
      throws InvalidStructureException {
    final PdbModel rna;
    try {
      rna = model.filteredNewInstance(MoleculeType.RNA);
    } catch (final PdbParsingException e) {
      throw new InvalidStructureException("Failed to filter RNA chains", e);
    }

    final List<Entry> ctEntries = new ArrayList<>();
    final List<PdbResidue> residues = rna.getResidues();
    final SortedSet<BpSeq.Entry> entries = bpSeq.getEntries();
    int i = 0;

    for (final BpSeq.Entry entry : entries) {
      final PdbResidue residue = residues.get(i);
      final PdbChain chain = rna.findChainContainingResidue(residue.getResidueIdentifier());
      final List<PdbResidue> chainResidues = chain.getResidues();

      final int index = entry.getIndex();
      final int pair = entry.getPair();
      final int before = chainResidues.indexOf(residue);
      final int after = (before + 2) % (chainResidues.size() + 1);
      final int original = residue.getResidueNumber();
      final char seq = entry.getSeq();
      final String comment = entry.getComment();
      ctEntries.add(new Entry(index, pair, before, after, original, seq, comment));

      i += 1;
    }

    return new Ct(ctEntries);
  }

  public static Ct fromDotBracket(final DotBracketInterface dotBracket)
      throws InvalidStructureException {
    final List<Entry> entries = new ArrayList<>();

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

        entries.add(new Entry(index, pairIndex, i, after, original, seq));
      }
    }

    return new Ct(entries);
  }

  public static void setPrintComments(final boolean printComments) {
    Ct.printComments = printComments;
  }

  public final int getStrandCount() {
    int count = 0;
    for (final Entry entry : entries) {
      if (entry.getAfter() == 0) {
        count += 1;
      }
    }
    return count;
  }

  public final Iterable<Entry> getEntries() {
    return Collections.unmodifiableSortedSet(entries);
  }

  public final void removeIsolatedPairs() throws InvalidStructureException {
    final BpSeq bpSeq = BpSeq.fromCt(this);
    final List<Region> regions = Region.createRegions(bpSeq);
    regions.forEach(
        region -> {
          if (region.getLength() == 1) {
            removePair(region.getEntries().get(0).getIndex());
          }
        });
  }

  private void removePair(final int index) {
    final Optional<Entry> entry = entries.stream().filter(e -> e.index == index).findFirst();
    final Optional<Entry> paired = entries.stream().filter(e -> e.pair == index).findFirst();

    if (entry.isPresent()) {
      final Entry o = entry.get();
      entries.remove(o);
      entries.add(new Entry(o.index, 0, o.before, o.after, o.original, o.seq, o.comment));
    }

    if (paired.isPresent()) {
      final Entry o = paired.get();
      entries.remove(o);
      entries.add(new Entry(o.index, 0, o.before, o.after, o.original, o.seq, o.comment));
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class Entry extends BpSeq.Entry {
    private final int before;
    private final int after;
    private final int original;

    public Entry(
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

    public Entry(
        final int index,
        final int pair,
        final int before,
        final int after,
        final int original,
        final char seq) {
      this(index, pair, before, after, original, seq, "");
    }

    public final int getBefore() {
      return before;
    }

    public final int getAfter() {
      return after;
    }

    public final int getOriginal() {
      return original;
    }

    @Override
    public final String toString() {
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
  public final String toString() {
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
