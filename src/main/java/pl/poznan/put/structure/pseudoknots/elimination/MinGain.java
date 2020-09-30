package pl.poznan.put.structure.pseudoknots.elimination;

import org.immutables.value.Value;
import pl.poznan.put.structure.pseudoknots.ConflictGraph;
import pl.poznan.put.structure.pseudoknots.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Java implementation of Elimination Gain algorithm as presented in: Smit, S. et al., 2008. From
 * knotted to nested RNA structures: A variety of computational methods for pseudoknot removal. RNA,
 * 14, pp.410–416.
 */
@Value.Immutable(singleton = true)
public abstract class MinGain extends AbstractRegionRemover {
  static List<Region> minGainRegions(
      final ConflictGraph conflictGraph, final Collection<Region> regions) {
    final SortedMap<Integer, List<Region>> map = new TreeMap<>();
    regions.forEach(
        region -> {
          final int gain =
              region.length()
                  - conflictGraph.conflictsWith(region).stream().mapToInt(Region::length).sum();
          map.putIfAbsent(gain, new ArrayList<>());
          map.get(gain).add(region);
        });
    return map.get(map.firstKey());
  }

  /**
   * Selects a region which has the least gain (sum of length of all regions in conflict). In case
   * of a tie, selects one with has the maximum conflict count. In case of another tie, select one
   * which is closer to 5' end.
   *
   * @param conflictGraph The graph of conflicts between regions.
   * @return A region to be removed.
   */
  @Override
  public final Region selectRegionToRemove(final ConflictGraph conflictGraph) {
    final Set<Region> regions = conflictGraph.regionsWithConflicts();

    final List<Region> minGainRegions = MinGain.minGainRegions(conflictGraph, regions);
    if (minGainRegions.size() == 1) {
      return minGainRegions.get(0);
    }

    final List<Region> maxConflictsRegions =
        MaxConflicts.maxConflictRegions(conflictGraph, minGainRegions);
    if (maxConflictsRegions.size() == 1) {
      return maxConflictsRegions.get(0);
    }

    maxConflictsRegions.sort(Comparator.comparingInt(Region::begin));
    return maxConflictsRegions.get(maxConflictsRegions.size() - 1);
  }
}
