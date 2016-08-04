package pl.poznan.put.structure.secondary.pseudoknots;

import pl.poznan.put.structure.secondary.formats.BpSeq;

import java.util.List;

/**
 * Pseudknot removal algorithm based on concept of regions as described in:
 * Smit, S. et al., 2008. From knotted to nested RNA structures: A variety of
 * computational methods for pseudoknot removal. RNA, 14, pp.410–416.
 */
public abstract class RegionBasedPseudoknotRemover
        implements PseudoknotRemover {
    @Override
    public final void removePseudoknots(final BpSeq bpSeq) {
        List<Region> regions = Region.createRegions(bpSeq);
        ConflictMap conflictMap = new ConflictMap(regions);

        while (conflictMap.hasConflicts()) {
            for (Region region : selectRegionsToRemove(conflictMap)) {
                region.setRemoved(true);
                conflictMap.remove(region);
            }
        }

        RegionBasedPseudoknotRemover.restoreNonConflicting(regions);

        for (Region region : regions) {
            if (region.isRemoved()) {
                for (BpSeq.Entry entry : region.getEntries()) {
                    bpSeq.removePair(entry);
                }
            }
        }
    }

    // Unremove all Regions that were removed but are no longer in conflict
    private static void restoreNonConflicting(final Iterable<Region> regions) {
        for (Region ri : regions) {
            if (!ri.isRemoved()) {
                continue;
            }

            boolean nonConflicting = true;
            for (Region rj : regions) {
                if (!rj.isRemoved() && ConflictMap.isConflicting(ri, rj)) {
                    nonConflicting = false;
                    break;
                }
            }

            if (nonConflicting) {
                ri.setRemoved(false);
            }
        }
    }

    protected abstract Iterable<Region> selectRegionsToRemove(
            final ConflictMap conflictMap);
}
