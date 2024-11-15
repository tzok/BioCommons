package pl.poznan.put.structure.pseudoknots;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;
import pl.poznan.put.structure.formats.BpSeq;

/** A collection of pairs (BPSEQ entries) which are consecutive in sequence. */
@Value.Immutable
@JsonSerialize(as = ImmutableRegion.class)
@JsonDeserialize(as = ImmutableRegion.class)
public abstract class Region implements Comparable<Region> {
  /**
   * Creates a list of regions from a secondary structure in BPSEQ format.
   *
   * @param bpSeq The input BPSEQ structure.
   * @return A list of regions in the structure.
   */
  public static List<Region> createRegions(final BpSeq bpSeq) {
    final List<Region> regions = new ArrayList<>();
    final List<BpSeq.Entry> regionEntries = new ArrayList<>();
    final Iterable<BpSeq.Entry> allEntries = new ArrayList<>(bpSeq.paired());

    for (final BpSeq.Entry entry : allEntries) {
      if (regionEntries.isEmpty()) {
        regionEntries.add(entry);
        continue;
      }

      final BpSeq.Entry last = regionEntries.get(regionEntries.size() - 1);
      if ((entry.index() == (last.index() + 1)) && (entry.pair() == (last.pair() - 1))) {
        regionEntries.add(entry);
        continue;
      }

      regions.add(ImmutableRegion.of(regionEntries));
      regionEntries.clear();
      regionEntries.add(entry);
    }

    if (!regionEntries.isEmpty()) {
      regions.add(ImmutableRegion.of(regionEntries));
    }

    return regions;
  }

  /**
   * Merges many regions into a new one.
   *
   * @param regions An array of regions to merge.
   * @return A new intance of this class created by merging the input regions.
   */
  public static Region merge(final Region... regions) {
    final List<BpSeq.Entry> entries =
        Arrays.stream(regions)
            .map(Region::entries)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    final int begin = entries.stream().map(BpSeq.Entry::index).min(Integer::compareTo).orElse(0);
    final int end = entries.stream().map(BpSeq.Entry::pair).max(Integer::compareTo).orElse(0);
    return ImmutableRegion.of(entries).withBegin(begin).withEnd(end);
  }

  /**
   * @return The list of BPSEQ entries in this region.
   */
  @Value.Parameter(order = 1)
  public abstract List<BpSeq.Entry> entries();

  /**
   * @return The number of BPSEQ entries in this region.
   */
  public final int length() {
    return entries().size();
  }

  /**
   * @return The first index of a region.
   */
  @Value.Default
  public int begin() {
    return entries().get(0).index();
  }

  /**
   * @return The last index of a region.
   */
  @Value.Default
  public int end() {
    return entries().get(0).pair();
  }

  /**
   * @return True if this region was removed.
   */
  @Value.Default
  @Value.Auxiliary
  public boolean isRemoved() {
    return false;
  }

  @Override
  public final int compareTo(final Region t) {
    return new CompareToBuilder().append(begin(), t.begin()).append(end(), t.end()).build();
  }

  @Override
  public final String toString() {
    return new ToStringBuilder(this)
        .append("begin", begin())
        .append("end", end())
        .append("length", length())
        .toString();
  }
}
