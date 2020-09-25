package pl.poznan.put.structure.pseudoknots.elimination;

import pl.poznan.put.structure.pseudoknots.ConflictMap;
import pl.poznan.put.structure.pseudoknots.Region;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Java implementation of Elimination Conflicts algorithm as presented in: Smit, S. et al., 2008.
 * From knotted to nested RNA structures: A variety of computational methods for pseudoknot removal.
 * RNA, 14, pp.410–416.
 */
public class MaxConflicts extends AbstractRegionRemover {
  public static List<Region> maxConflictRegions(
      final ConflictMap conflictMap, final Iterable<Region> regions) {
    final SortedMap<Integer, List<Region>> mapConflicsRegions = new TreeMap<>();

    for (final Region region : regions) {
      final int conflictCount = conflictMap.conflictsWith(region).size();
      if (!mapConflicsRegions.containsKey(conflictCount)) {
        mapConflicsRegions.put(conflictCount, new ArrayList<>());
      }
      mapConflicsRegions.get(conflictCount).add(region);
    }

    return mapConflicsRegions.get(mapConflicsRegions.lastKey());
  }

  @Override
  public final Region selectRegionToRemove(final ConflictMap conflictMap) {
    final Set<Region> regions = conflictMap.getRegionsWithConflicts();

    final List<Region> maxConflictsRegions = MaxConflicts.maxConflictRegions(conflictMap, regions);
    if (maxConflictsRegions.size() == 1) {
      return maxConflictsRegions.get(0);
    }

    final List<Region> minGainRegions = MinGain.minGainRegions(conflictMap, maxConflictsRegions);
    if (minGainRegions.size() == 1) {
      return minGainRegions.get(0);
    }

    maxConflictsRegions.sort(Comparator.comparingInt(Region::getBegin));
    return maxConflictsRegions.get(maxConflictsRegions.size() - 1);
  }
}
