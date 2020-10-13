package pl.poznan.put.structure.pseudoknots;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.immutables.value.Value;
import pl.poznan.put.structure.pseudoknots.dp.ConflictClique;
import pl.poznan.put.structure.pseudoknots.dp.ImmutableConflictClique;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A graph of conflicts between regions. A conflict is when one region starts/end in the middle of
 * another region.d
 */
@Value.Immutable
public abstract class ConflictGraph {
  /**
   * Checks if given regions are conflicting. A conflict is when one region starts/ends in the
   * middle of another region.
   *
   * @param first The first region.
   * @param second The second region.
   * @return True if regions are conflicting.
   */
  public static boolean isConflicting(final Region first, final Region second) {
    final int firstBegin = first.begin();
    final int firstEnd = first.end();
    final int secondBegin = second.begin();
    final int secondEnd = second.end();

    if (firstBegin < secondBegin) {
      if (firstEnd < secondEnd) {
        if (secondBegin < firstEnd) {
          return true;
        }
      }
    }
    if (secondBegin < firstBegin) {
      if (secondEnd < firstEnd) {
        return firstBegin < secondEnd;
      }
    }
    return false;
  }

  @Value.Parameter(order = 1)
  protected abstract List<Region> regions();

  /**
   * Removes region and all associated conflicts with it from the graph.
   *
   * @param region The region to remove.
   */
  public final void removeRegion(final Region region) {
    if (conflicts().containsKey(region)) {
      for (final Region conflicted : conflicts().get(region)) {
        conflicts().get(conflicted).remove(region);
        if (conflicts().get(conflicted).isEmpty()) {
          conflicts().remove(conflicted);
        }
      }
      conflicts().remove(region);
    }
  }

  /**
   * Returns all regions that conflict with a given one.
   *
   * @param region The region to search conflict for.
   * @return A collection of regions in conflict with the given one.
   */
  public final Set<Region> conflictsWith(final Region region) {
    return conflicts().containsKey(region)
        ? Collections.unmodifiableSet(conflicts().get(region))
        : Collections.emptySet();
  }

  /**
   * Returns all regions that are in conflict with at least one other region.
   *
   * @return A set of regions which are in at least one conflict.
   */
  public final Set<Region> regionsWithConflicts() {
    return Collections.unmodifiableSet(conflicts().keySet());
  }

  /** @return True if at least one region has at least one conflict. */
  public final boolean hasConflicts() {
    return !conflicts().isEmpty();
  }

  /**
   * Checks if a given region has any conflicts.
   *
   * @param region The region to check.
   * @return True if the given region has at least one conflict.
   */
  public final boolean hasConflicts(final Region region) {
    return conflicts().containsKey(region);
  }

  /**
   * Searches for connected components (named conflict cliques, although they are not real cliques).
   *
   * @return The list of conflict cliques.
   */
  public final List<ConflictClique> conflictCliques() {
    final List<ConflictClique> conflictCliques = new ArrayList<>();
    final Collection<Region> seen = new HashSet<>();

    for (final Region region : conflicts().keySet()) {
      if (seen.contains(region)) {
        continue;
      }

      final Collection<Region> todo = new HashSet<>();
      todo.add(region);
      final Set<Region> done = new HashSet<>();

      while (!CollectionUtils.isEqualCollection(todo, done)) {
        final Collection<Region> next = new ArrayList<>();
        for (final Region r : todo) {
          next.addAll(conflicts().get(r));
          done.add(r);
        }
        todo.addAll(next);
      }

      if (done.size() > 1) {
        conflictCliques.add(ImmutableConflictClique.of(done));
      }

      seen.addAll(done);
    }

    return conflictCliques;
  }

  /**
   * Creates a simplified copy of this instance. The simplification merges smaller region which are
   * fully embedded into the bigger ones, if both the small and big region have exactly the same
   * conflicts.
   *
   * @return A simplified instance of this conflict graph.
   */
  public final ConflictGraph simplified() {
    final Collection<Region> toRemove = new ArrayList<>();
    final Collection<Region> toAdd = new ArrayList<>();

    ConflictGraph result = ImmutableConflictGraph.copyOf(this);
    do {
      final List<Region> conflicted = new ArrayList<>(result.regionsWithConflicts());
      toRemove.clear();
      toAdd.clear();

      for (int i = 0; i < conflicted.size(); i++) {
        final Region ri = conflicted.get(i);
        final int riBegin = ri.begin();
        final int riEnd = ri.end();

        for (int j = i + 1; j < conflicted.size(); j++) {
          final Region rj = conflicted.get(j);
          final int rjBegin = rj.begin();
          final int rjEnd = rj.end();

          final boolean b1 = (riBegin <= rjBegin) && (riEnd >= rjEnd);
          final boolean b2 = (rjBegin <= riBegin) && (rjEnd >= riEnd);
          if ((b1 || b2)
              && CollectionUtils.isEqualCollection(
                  result.conflictsWith(ri), result.conflictsWith(rj))) {
            toRemove.add(ri);
            toRemove.add(rj);
            toAdd.add(Region.merge(ri, rj));
            break;
          }
        }

        if (!toRemove.isEmpty()) {
          break;
        }
      }

      if (!toRemove.isEmpty()) {
        final List<Region> regionsCopy = new ArrayList<>(regions());
        regionsCopy.removeAll(toRemove);
        regionsCopy.addAll(toAdd);
        result = ImmutableConflictGraph.of(regionsCopy);
      }
    } while (!toRemove.isEmpty());

    return result;
  }

  @Value.Lazy
  protected Map<Region, Set<Region>> conflicts() {
    if (regions().size() < 2) {
      return Collections.emptyMap();
    }

    final Map<Region, Set<Region>> map = new HashMap<>();

    final Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(regions().size(), 2);
    final Iterable<int[]> iterable = () -> iterator;
    StreamSupport.stream(iterable.spliterator(), false)
        .map(ints -> Pair.of(regions().get(ints[0]), regions().get(ints[1])))
        .filter(pair -> ConflictGraph.isConflicting(pair.getLeft(), pair.getRight()))
        .flatMap(pair -> Stream.of(pair, Pair.of(pair.getRight(), pair.getLeft())))
        .distinct()
        .forEach(
            pair -> {
              map.putIfAbsent(pair.getLeft(), new HashSet<>());
              map.get(pair.getLeft()).add(pair.getRight());
            });

    return map;
  }
}
