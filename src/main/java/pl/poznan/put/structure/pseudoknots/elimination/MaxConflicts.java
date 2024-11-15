package pl.poznan.put.structure.pseudoknots.elimination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import pl.poznan.put.structure.pseudoknots.ConflictGraph;
import pl.poznan.put.structure.pseudoknots.Region;

/**
 * Java implementation of Elimination Conflicts algorithm as presented in: Smit, S. et al., 2008.
 * From knotted to nested RNA structures: A variety of computational methods for pseudoknot removal.
 * RNA, 14, pp.410–416.
 */
@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableMaxConflicts.class)
@JsonDeserialize(as = ImmutableMaxConflicts.class)
public abstract class MaxConflicts extends AbstractRegionRemover {
  static List<Region> maxConflictRegions(
      final ConflictGraph conflictGraph, final Collection<Region> regions) {
    final SortedMap<Integer, List<Region>> map = new TreeMap<>();
    regions.forEach(
        region -> {
          final int size = conflictGraph.conflictsWith(region).size();
          map.putIfAbsent(size, new ArrayList<>());
          map.get(size).add(region);
        });
    return map.get(map.lastKey());
  }

  /**
   * Selects a region which has the most conflicts with other regions. In case of a tie, selects one
   * with has the least gain. In case of another tie, select one which is closer to 5' end.
   *
   * @param conflictGraph The graph of conflicts between regions.
   * @return A region to be removed.
   */
  @Override
  public final Region selectRegionToRemove(final ConflictGraph conflictGraph) {
    final Set<Region> regions = conflictGraph.regionsWithConflicts();

    final List<Region> maxConflictsRegions =
        MaxConflicts.maxConflictRegions(conflictGraph, regions);
    if (maxConflictsRegions.size() == 1) {
      return maxConflictsRegions.get(0);
    }

    final List<Region> minGainRegions = MinGain.minGainRegions(conflictGraph, maxConflictsRegions);
    if (minGainRegions.size() == 1) {
      return minGainRegions.get(0);
    }

    maxConflictsRegions.sort(Comparator.comparingInt(Region::begin));
    return maxConflictsRegions.get(maxConflictsRegions.size() - 1);
  }
}
