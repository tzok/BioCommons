package pl.poznan.put.structure.secondary.pseudoknots;

import org.apache.commons.collections4.CollectionUtils;
import pl.poznan.put.structure.secondary.pseudoknots.dp.Clique;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A map of conflicts between regions. A conflict is when one region starts/end
 * in the middle of another region.
 */
public class ConflictMap {
    private final Map<Region, Set<Region>> conflicts;

    // Create a basic 2DMap from which tells about conflicts. It can return
    // if given Region have conflicts and give these conflicts
    public ConflictMap(final List<Region> regions) {
        super();
        int size = regions.size();
        conflicts = new HashMap<>(size);

        for (int i = 0; i < size; i++) {
            Region ri = regions.get(i);

            for (int j = i + 1; j < size; j++) {
                Region rj = regions.get(j);
                if (ConflictMap.isConflicting(ri, rj)) {
                    if (!conflicts.containsKey(ri)) {
                        conflicts.put(ri, new HashSet<Region>(size));
                    }
                    if (!conflicts.containsKey(rj)) {
                        conflicts.put(rj, new HashSet<Region>(size));
                    }
                    conflicts.get(ri).add(rj);
                    conflicts.get(rj).add(ri);
                }
            }
        }
    }

    // Check if given Regions are conflicting
    public static boolean isConflicting(final Region first,
                                        final Region second) {
        int firstBegin = first.getBegin();
        int firstEnd = first.getEnd();
        int secondBegin = second.getBegin();
        int secondEnd = second.getEnd();

        if (firstBegin < secondBegin) {
            if (firstEnd < secondEnd) {
                if (secondBegin < firstEnd) {
                    return true;
                }
            }
        }
        if (secondBegin < firstBegin) {
            if (secondEnd < firstEnd) {
                if (firstBegin < secondEnd) {
                    return true;
                }
            }
        }
        return false;
    }

    // Remove Region and all associated conflicts with it
    public final void remove(final Region region) {
        if (conflicts.containsKey(region)) {
            for (Region conflicted : conflicts.get(region)) {
                conflicts.get(conflicted).remove(region);
                if (conflicts.get(conflicted).isEmpty()) {
                    conflicts.remove(conflicted);
                }
            }
            conflicts.remove(region);
        }
    }

    // Return all Regions that conflicts with given Region
    public final Set<Region> conflictsWith(final Region region) {
        return Collections.unmodifiableSet(conflicts.get(region));
    }

    // Return all Regions that have some conflict
    public final Set<Region> getRegionsWithConflicts() {
        return Collections.unmodifiableSet(conflicts.keySet());
    }

    public final boolean hasAnyConflicts() {
        return !conflicts.isEmpty();
    }

    public final boolean hasConflicts(final Region region) {
        return conflicts.containsKey(region);
    }

    public final List<Clique> getConflictCliques(boolean isSingleton) {
        if (conflicts.isEmpty()) {
            return Collections.emptyList();
        }

        if (isSingleton) {
            return Collections.singletonList(new Clique(conflicts.keySet()));
        }

        List<Clique> cliques = new ArrayList<>();
        Collection<Region> seen = new HashSet<>();

        for (Region region : conflicts.keySet()) {
            if (seen.contains(region)) {
                continue;
            }

            Collection<Region> todo = new HashSet<>();
            todo.add(region);
            Set<Region> done = new HashSet<>();

            while (!CollectionUtils.isEqualCollection(todo, done)) {
                Collection<Region> next = new ArrayList<>();
                for (Region r : todo) {
                    next.addAll(conflicts.get(r));
                    done.add(r);
                }
                todo.addAll(next);
            }

            if (done.size() > 1) {
                cliques.add(new Clique(done));
            }

            for (Region r : done) {
                seen.add(r);
            }
        }

        return cliques;
    }
}
