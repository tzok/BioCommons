package pl.poznan.put.structure.pseudoknots.elimination;

import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.ImmutableBpSeq;
import pl.poznan.put.structure.pseudoknots.ConflictGraph;
import pl.poznan.put.structure.pseudoknots.ImmutableConflictGraph;
import pl.poznan.put.structure.pseudoknots.ModifiableRegion;
import pl.poznan.put.structure.pseudoknots.Region;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Java implementation of region removal algorithm as presented in: Smit, S. et al., 2008. From
 * knotted to nested RNA structures: A variety of computational methods for pseudoknot removal. RNA,
 * 14, pp.410–416.
 */
public abstract class AbstractRegionRemover implements RegionRemover {
  // Unremove all Regions that were removed but are no longer in conflict
  private static void restoreNonConflicting(final Iterable<Region> regions) {
    for (final Region ri : regions) {
      if (!ri.isRemoved()) {
        continue;
      }

      boolean nonConflicting = true;
      for (final Region rj : regions) {
        if (!rj.isRemoved() && ConflictGraph.isConflicting(ri, rj)) {
          nonConflicting = false;
          break;
        }
      }

      if (nonConflicting) {
        ((ModifiableRegion) ri).setIsRemoved(false);
      }
    }
  }

  /**
   * Finds pseudoknots by removing one region at a time until there are any conflicts. The region to
   * remove is selected according to a heuristic (see {@link MinGain} and {@link MaxConflicts}).
   *
   * @param bpSeq An input BPSEQ structure with all pairs.
   * @return A list of BPSEQ structures where each contains only pairs considered to be pseudoknots.
   *     Each BPSEQ is a full copy of original one, but contains zeroed 'pair' columns for entries
   *     which are non-pseudoknots.
   */
  @Override
  public final List<BpSeq> findPseudoknots(final BpSeq bpSeq) {
    final List<Region> regions = Region.createRegions(bpSeq);
    final ConflictGraph conflictGraph = ImmutableConflictGraph.of(regions);

    while (conflictGraph.hasConflicts()) {
      final Region region = selectRegionToRemove(conflictGraph);
      ((ModifiableRegion) region).setIsRemoved(true);
      conflictGraph.removeRegion(region);
    }

    AbstractRegionRemover.restoreNonConflicting(regions);

    final List<BpSeq.Entry> nonPseudoknotted =
        regions.stream()
            .filter(region -> !region.isRemoved())
            .map(Region::entries)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    BpSeq result = ImmutableBpSeq.copyOf(bpSeq);
    for (final BpSeq.Entry entry : nonPseudoknotted) {
      result = result.withoutPair(entry);
    }
    return Collections.singletonList(result);
  }
}
