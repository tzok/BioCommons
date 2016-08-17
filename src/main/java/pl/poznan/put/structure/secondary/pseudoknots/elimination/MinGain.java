package pl.poznan.put.structure.secondary.pseudoknots.elimination;

import pl.poznan.put.structure.secondary.pseudoknots.ConflictMap;
import pl.poznan.put.structure.secondary.pseudoknots.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Java implementation of Elimination Gain algorithm as presented in: Smit, S.
 * et al., 2008. From knotted to nested RNA structures: A variety of
 * computational methods for pseudoknot removal. RNA, 14, pp.410–416.
 */
public class MinGain extends AbstractRegionRemover {
    @Override
    public final Region selectRegionToRemove(final ConflictMap conflictMap) {
        Set<Region> regions = conflictMap.getRegionsWithConflicts();

        List<Region> minGainRegions =
                MinGain.minGainRegions(conflictMap, regions);
        if (minGainRegions.size() == 1) {
            return minGainRegions.get(0);
        }

        List<Region> maxConflictsRegions =
                MaxConflicts.maxConflictRegions(conflictMap, minGainRegions);
        if (maxConflictsRegions.size() == 1) {
            return maxConflictsRegions.get(0);
        }

        Collections.sort(maxConflictsRegions, new Comparator<Region>() {
            @Override
            public int compare(final Region t, final Region t1) {
                return Integer.compare(t.getBegin(), t1.getBegin());
            }
        });
        return maxConflictsRegions.get(maxConflictsRegions.size() - 1);
    }

    public static List<Region> minGainRegions(final ConflictMap conflictMap,
                                              final Iterable<Region> regions) {
        SortedMap<Integer, List<Region>> mapGainRegions = new TreeMap<>();

        for (Region region : regions) {
            int conflictLength = 0;
            for (Region conflicting : conflictMap.conflictsWith(region)) {
                conflictLength += conflicting.getLength();
            }

            int gain = region.getLength() - conflictLength;
            if (!mapGainRegions.containsKey(gain)) {
                mapGainRegions.put(gain, new ArrayList<Region>());
            }

            mapGainRegions.get(gain).add(region);
        }

        return mapGainRegions.get(mapGainRegions.firstKey());
    }
}
