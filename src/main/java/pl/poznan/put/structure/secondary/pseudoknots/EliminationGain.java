package pl.poznan.put.structure.secondary.pseudoknots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Java implementation of Elimination Gain algorithm as presented in: Smit, S.
 * et al., 2008. From knotted to nested RNA structures: A variety of
 * computational methods for pseudoknot removal. RNA, 14, pp.410–416.
 */
public class EliminationGain extends RegionBasedPseudoknotRemover {
    // Rewritten find_min_gain function from PyCogent-1.5.3
    @Override
    protected final Iterable<Region> selectRegionsToRemove(
            final ConflictMap conflictMap) {
        SortedMap<Integer, List<Region>> mapGainRegions = new TreeMap<>();

        for (Region region : conflictMap.getRegionsWithConflicts()) {
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

        List<Region> minGainRegions = mapGainRegions.get(
                mapGainRegions.firstKey());
        if (minGainRegions.size() == 1) {
            return Collections.singleton(minGainRegions.get(0));
        }

        int maxConflictCount = Integer.MIN_VALUE;
        Region maxConflictCountRegion = null;

        for (Region region : minGainRegions) {
            int conflictCount = conflictMap.conflictsWith(region).size();
            if (conflictCount > maxConflictCount) {
                maxConflictCount = conflictCount;
                maxConflictCountRegion = region;
            }
        }

        return Collections.singleton(maxConflictCountRegion);
    }
}
