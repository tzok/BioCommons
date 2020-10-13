package pl.poznan.put.structure.pseudoknots.elimination;

import pl.poznan.put.structure.pseudoknots.ConflictGraph;
import pl.poznan.put.structure.pseudoknots.PseudoknotFinder;
import pl.poznan.put.structure.pseudoknots.Region;

/** A pseudoknot finder algorithm which works by selecting a region to remove in each iteration. */
public interface RegionRemover extends PseudoknotFinder {
  /**
   * Uses data in the conflict graph to select one region to be removed.
   *
   * @param conflictGraph The graph of conflicts between regions.
   * @return A region to be removed.
   */
  Region selectRegionToRemove(final ConflictGraph conflictGraph);
}
