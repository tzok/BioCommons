package pl.poznan.put.structure.secondary.pseudoknots.elimination;

import pl.poznan.put.structure.secondary.pseudoknots.ConflictMap;
import pl.poznan.put.structure.secondary.pseudoknots.Region;

import java.util.ArrayList;
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
public class MinGain extends AbstractRegionRemover {
  @Override
  public final Region selectRegionToRemove(final ConflictMap conflictMap) {
    final Set<Region> regions = conflictMap.getRegionsWithConflicts();

    final List<Region> minGainRegions = MinGain.minGainRegions(conflictMap, regions);
    if (minGainRegions.size() == 1) {
      return minGainRegions.get(0);
    }

    final List<Region> maxConflictsRegions =
        MaxConflicts.maxConflictRegions(conflictMap, minGainRegions);
    if (maxConflictsRegions.size() == 1) {
      return maxConflictsRegions.get(0);
    }

    maxConflictsRegions.sort(Comparator.comparingInt(Region::getBegin));
    return maxConflictsRegions.get(maxConflictsRegions.size() - 1);
  }

  public static List<Region> minGainRegions(
      final ConflictMap conflictMap, final Iterable<Region> regions) {
    final SortedMap<Integer, List<Region>> mapGainRegions = new TreeMap<>();

    for (final Region region : regions) {
      final int conflictLength =
          conflictMap.conflictsWith(region).stream().mapToInt(Region::getLength).sum();

      final int gain = region.getLength() - conflictLength;
      if (!mapGainRegions.containsKey(gain)) {
        mapGainRegions.put(gain, new ArrayList<>());
      }

      mapGainRegions.get(gain).add(region);
    }

    return mapGainRegions.get(mapGainRegions.firstKey());
  }
}
