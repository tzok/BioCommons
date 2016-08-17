package pl.poznan.put.structure.secondary.pseudoknots.elimination;

import pl.poznan.put.structure.secondary.pseudoknots.ConflictMap;
import pl.poznan.put.structure.secondary.pseudoknots.PseudoknotFinder;
import pl.poznan.put.structure.secondary.pseudoknots.Region;

/**
 * A pseudoknot finder algorithm which works by selecting a region to remove in
 * each iteration.
 */
public interface RegionRemover extends PseudoknotFinder {
    Region selectRegionToRemove(final ConflictMap conflictMap);
}
