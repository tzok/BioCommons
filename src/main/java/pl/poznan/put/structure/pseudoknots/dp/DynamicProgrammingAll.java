package pl.poznan.put.structure.pseudoknots.dp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.ImmutableBpSeq;
import pl.poznan.put.structure.pseudoknots.ConflictGraph;
import pl.poznan.put.structure.pseudoknots.ImmutableConflictGraph;
import pl.poznan.put.structure.pseudoknots.Region;
import pl.poznan.put.structure.pseudoknots.elimination.RegionRemover;

/**
 * Java implementation of OPT ALL algorithm as presented in: Smit, S. et al., 2008. From knotted to
 * nested RNA structures: A variety of computational methods for pseudoknot removal. RNA, 14,
 * pp.410–416.
 */
@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableDynamicProgrammingAll.class)
@JsonDeserialize(as = ImmutableDynamicProgrammingAll.class)
public abstract class DynamicProgrammingAll implements DynamicProgramming {
  private static SubSolution[] solveSingleCase(
      final SubSolution[][][] matrix,
      final ConflictClique conflictClique,
      final int i,
      final int j) {
    final int size = conflictClique.endpointCount();
    final Collection<SubSolution> candidates = new HashSet<>(size);

    // add solutions from the left
    if (matrix[i][j - 1].length > 0) {
      candidates.addAll(Arrays.asList(matrix[i][j - 1]));
    }

    // add solutions from the bottom
    if (matrix[i + 1][j].length > 0) {
      candidates.addAll(Arrays.asList(matrix[i + 1][j]));
    }

    // if there is a region from i to j, then add it and all from
    // the bottom left
    final Optional<Region> region =
        conflictClique.findRegion(conflictClique.endpoint(i), conflictClique.endpoint(j));
    if (region.isPresent()) {
      final SubSolution current = ImmutableSubSolution.of(Collections.singletonList(region.get()));

      if (matrix[i + 1][j - 1].length > 0) {
        for (final SubSolution subSolution : matrix[i + 1][j - 1]) {
          candidates.add(SubSolution.merge(subSolution, current));
        }
      } else {
        candidates.add(current);
      }
    }

    // merge solution from next-row, previous-column
    candidates.addAll(DynamicProgrammingAll.merge(matrix, conflictClique, i, j));

    // select all candidates with highest score
    final List<SubSolution> bestCandidates = DynamicProgrammingAll.selectBestCandidates(candidates);
    return bestCandidates.toArray(new SubSolution[0]);
  }

  private static Collection<SubSolution> merge(
      final SubSolution[][][] matrix,
      final ConflictClique conflictClique,
      final int i,
      final int j) {
    final SubSolution[] left = matrix[i][j - 1];
    final SubSolution[] below = matrix[i + 1][j];

    if ((left.length == 0) || (below.length == 0)) {
      return Collections.emptyList();
    }

    final Collection<SubSolution> result = new ArrayList<>();

    for (final SubSolution leftSub : left) {
      final int highestEndpoint = leftSub.highestEndpoint();

      for (final SubSolution belowSub : below) {
        final int lowestEndpoint = belowSub.lowestEndpoint();

        if (highestEndpoint < lowestEndpoint) {
          result.add(SubSolution.merge(leftSub, belowSub));
          continue;
        }

        final int begin = conflictClique.indexOfEndpoint(lowestEndpoint) - 1;
        final int end = conflictClique.indexOfEndpoint(highestEndpoint) + 1;

        for (int k = begin; k < end; k++) {
          for (final SubSolution s1 : matrix[i][k]) {
            for (final SubSolution s2 : matrix[k + 1][j]) {
              result.add(SubSolution.merge(s1, s2));
            }
          }
        }
      }
    }

    return result;
  }

  private static List<SubSolution> selectBestCandidates(final Collection<SubSolution> candidates) {
    if (candidates.isEmpty()) {
      return Collections.emptyList();
    }

    final SortedMap<Integer, List<SubSolution>> map = new TreeMap<>();
    candidates.forEach(
        subSolution -> {
          final int score = subSolution.score();
          map.putIfAbsent(score, new ArrayList<>());
          map.get(score).add(subSolution);
        });
    return map.get(map.lastKey());
  }

  /**
   * @return An optional region remover to be used if a clique size exceeds {@code maxCliqueSize()}.
   */
  protected abstract Optional<RegionRemover> regionRemover();

  @Override
  public final List<SubSolution> findOptimalSolutions(final ConflictClique conflictClique) {
    final int size = conflictClique.endpointCount();
    final SubSolution[][][] matrix = new SubSolution[size][size][0];

    for (int j = 1; j < size; j++) {
      for (int i = j - 1; i >= 0; i--) {
        matrix[i][j] = DynamicProgrammingAll.solveSingleCase(matrix, conflictClique, i, j);
      }
    }

    return Arrays.asList(matrix[0][size - 1]);
  }

  @Override
  public final List<BpSeq> findPseudoknots(final BpSeq bpSeq) {
    final List<Region> regions = Region.createRegions(bpSeq);
    final ConflictGraph conflictGraph = ImmutableConflictGraph.of(regions).simplified();

    final List<BpSeq.Entry> nonConflicting = new ArrayList<>();
    for (final Region region : regions) {
      if (!conflictGraph.hasConflicts(region)) {
        nonConflicting.addAll(region.entries());
      }
    }

    if (regionRemover().isPresent()) {
      int max;
      do {
        max = Integer.MIN_VALUE;
        for (final ConflictClique conflictClique : conflictGraph.conflictCliques()) {
          if (conflictClique.size() > max) {
            max = conflictClique.size();
          }
        }
        if (max > maxCliqueSize()) {
          conflictGraph.removeRegion(regionRemover().get().selectRegionToRemove(conflictGraph));
        }
      } while (max > maxCliqueSize());
    }

    List<List<BpSeq.Entry>> results = new ArrayList<>();
    results.add(nonConflicting);
    final List<ConflictClique> conflictCliques = conflictGraph.conflictCliques();

    for (final ConflictClique conflictClique : conflictCliques) {
      final List<List<BpSeq.Entry>> nextResults = new ArrayList<>();
      final List<SubSolution> solutions = findOptimalSolutions(conflictClique);

      for (final SubSolution solution : solutions) {
        for (final List<BpSeq.Entry> previousResult : results) {
          final List<BpSeq.Entry> nextResult = new ArrayList<>(previousResult);
          for (final Region region : solution.regions()) {
            nextResult.addAll(region.entries());
          }
          nextResults.add(nextResult);
        }
      }

      results = nextResults;
    }

    final List<BpSeq> bpSeqs = new ArrayList<>();

    for (final List<BpSeq.Entry> result : results) {
      BpSeq nextBpSeq = ImmutableBpSeq.copyOf(bpSeq);
      for (final BpSeq.Entry entry : result) {
        nextBpSeq = nextBpSeq.withoutPair(entry);
      }
      bpSeqs.add(nextBpSeq);
    }

    return bpSeqs;
  }

  /**
   * @return The maximum number of conflicts allowed to be in the clique. The algorithm slows down
   *     very much when the cliques are getting bigger, so it is advisable to use a heuristic to
   *     remove a single regions and decrease clique size significantly.
   */
  @Value.Default
  protected int maxCliqueSize() {
    return Integer.MAX_VALUE;
  }
}
