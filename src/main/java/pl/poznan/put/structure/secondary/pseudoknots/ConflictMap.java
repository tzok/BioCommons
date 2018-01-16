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
 * A map of conflicts between regions. A conflict is when one region starts/end in the middle of
 * another region.
 */
public class ConflictMap {
  private final Map<Region, Set<Region>> conflicts;
  private final int conflictsCount;

  // Create a basic 2DMap from which tells about conflicts. It can return
  // if given Region have conflicts and give these conflicts
  public ConflictMap(final List<Region> regions) {
    super();
    final int size = regions.size();
    conflicts = new HashMap<>(size);
    int count = 0;

    for (int i = 0; i < size; i++) {
      final Region ri = regions.get(i);

      for (int j = i + 1; j < size; j++) {
        final Region rj = regions.get(j);
        if (ConflictMap.isConflicting(ri, rj)) {
          if (!conflicts.containsKey(ri)) {
            conflicts.put(ri, new HashSet<>(size));
          }
          if (!conflicts.containsKey(rj)) {
            conflicts.put(rj, new HashSet<>(size));
          }
          conflicts.get(ri).add(rj);
          conflicts.get(rj).add(ri);
          count += 1;
        }
      }
    }

    conflictsCount = count;
  }

  // Check if given Regions are conflicting
  public static boolean isConflicting(final Region first, final Region second) {
    final int firstBegin = first.getBegin();
    final int firstEnd = first.getEnd();
    final int secondBegin = second.getBegin();
    final int secondEnd = second.getEnd();

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
      for (final Region conflicted : conflicts.get(region)) {
        conflicts.get(conflicted).remove(region);
        if (conflicts.get(conflicted).isEmpty()) {
          conflicts.remove(conflicted);
        }
      }
      conflicts.remove(region);
    }
  }

  // Return all Regions that conflicts with given Region
  public final Collection<Region> conflictsWith(final Region region) {
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

  public final List<Clique> getConflictCliques(final boolean isSingleton) {
    if (conflicts.isEmpty()) {
      return Collections.emptyList();
    }

    if (isSingleton) {
      return Collections.singletonList(new Clique(conflicts.keySet()));
    }

    final List<Clique> cliques = new ArrayList<>();
    final Collection<Region> seen = new HashSet<>();

    for (final Region region : conflicts.keySet()) {
      if (seen.contains(region)) {
        continue;
      }

      final Collection<Region> todo = new HashSet<>();
      todo.add(region);
      final Set<Region> done = new HashSet<>();

      while (!CollectionUtils.isEqualCollection(todo, done)) {
        final Collection<Region> next = new ArrayList<>();
        for (final Region r : todo) {
          next.addAll(conflicts.get(r));
          done.add(r);
        }
        todo.addAll(next);
      }

      if (done.size() > 1) {
        cliques.add(new Clique(done));
      }

      seen.addAll(done);
    }

    return cliques;
  }
}
