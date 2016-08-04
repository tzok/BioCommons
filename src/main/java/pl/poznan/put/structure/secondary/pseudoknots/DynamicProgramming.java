package pl.poznan.put.structure.secondary.pseudoknots;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Java implementation of OPT ALL algorithm as presented in: Smit, S.
 * et al., 2008. From knotted to nested RNA structures: A variety of
 * computational methods for pseudoknot removal. RNA, 14, pp.410–416.
 */
public class DynamicProgramming extends RegionBasedPseudoknotRemover {
    public static class SubSolution {
        public static DynamicProgramming.SubSolution merge(
                final DynamicProgramming.SubSolution left,
                final DynamicProgramming.SubSolution below) {
            List<Region> regions = new ArrayList<>();
            regions.addAll(left.regions);
            regions.addAll(below.regions);
            return new DynamicProgramming.SubSolution(regions);
        }

        final List<Region> regions;
        final int lowestEndpoint;
        final int highestEndpoint;
        final int score;

        public SubSolution(final Region region) {
            this(Collections.singletonList(region));
        }

        public SubSolution(final List<Region> regions) {
            super();
            this.regions = new ArrayList<>(regions);

            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            int sum = 0;

            for (Region region : regions) {
                min = Math.min(min, region.getBegin());
                max = Math.max(max, region.getEnd());
                sum += region.getLength();
            }

            lowestEndpoint = min;
            highestEndpoint = max;
            score = sum;
        }

        @Override
        public final boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || (getClass() != obj.getClass())) {
                return false;
            }
            DynamicProgramming.SubSolution other =
                    (DynamicProgramming.SubSolution) obj;
            return (lowestEndpoint == other.lowestEndpoint) &&
                   (highestEndpoint == other.highestEndpoint) &&
                   (score == other.score);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(lowestEndpoint, highestEndpoint, score);
        }
    }

    @Override
    public final Iterable<Region> selectRegionsToRemove(
            final ConflictMap conflictMap) {
        assert !conflictMap.getRegionsWithConflicts().isEmpty();

        Set<Region> clique = new HashSet<>();
        Region starting =
                conflictMap.getRegionsWithConflicts().iterator().next();
        DynamicProgramming.fillConflictClique(clique, starting, conflictMap);

        List<Integer> endpoints = new ArrayList<>();
        for (Region region : clique) {
            endpoints.add(region.getBegin());
            endpoints.add(region.getEnd());
        }
        Collections.sort(endpoints);

        int size = endpoints.size();
        Collection<DynamicProgramming.SubSolution> candidates = new HashSet<>(
                size);
        DynamicProgramming.SubSolution[][][] matrix =
                new DynamicProgramming.SubSolution[size][size][0];

        for (int j = 1; j < size; j++) {
            for (int i = j - 1; i >= 0; i--) {
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
                Region region = DynamicProgramming.findRegion(clique,
                                                              endpoints.get(i),
                                                              endpoints.get(j));
                if (region != null) {
                    DynamicProgramming.SubSolution current =
                            new DynamicProgramming.SubSolution(region);

                    if (matrix[i + 1][j - 1].length > 0) {
                        for (DynamicProgramming.SubSolution subSolution :
                                matrix[
                                i + 1][j - 1]) {
                            candidates.add(DynamicProgramming.SubSolution
                                                   .merge(subSolution,
                                                          current));
                        }
                    } else {
                        candidates.add(current);
                    }
                }

                candidates.addAll(DynamicProgramming
                                          .merge(matrix, i, j, endpoints,
                                                 matrix[i][j - 1],
                                                 matrix[i + 1][j]));

                if (!candidates.isEmpty()) {
                    SortedMap<Integer, List<DynamicProgramming.SubSolution>>
                            map = new TreeMap<>();
                    for (DynamicProgramming.SubSolution candidate :
                            candidates) {

                        int score = candidate.score;
                        if (!map.containsKey(score)) {
                            map.put(score,
                                    new ArrayList<DynamicProgramming
                                            .SubSolution>());
                        }
                        map.get(score).add(candidate);
                    }

                    List<DynamicProgramming.SubSolution> best = map.get(
                            map.lastKey());
                    matrix[i][j] = best.toArray(
                            new DynamicProgramming.SubSolution[best.size()]);
                    candidates.clear();
                }
            }
        }

        return CollectionUtils.subtract(clique, matrix[0][size - 1][0].regions);
    }

    private static Collection<DynamicProgramming.SubSolution> merge(
            final DynamicProgramming.SubSolution[][][] matrix, final int row,
            final int column, final List<Integer> endpoints,
            final DynamicProgramming.SubSolution[] left,
            final DynamicProgramming.SubSolution[] below) {
        if ((left.length == 0) || (below.length == 0)) {
            return Collections.emptyList();
        }

        Collection<DynamicProgramming.SubSolution> result = new ArrayList<>();

        for (DynamicProgramming.SubSolution leftSub : left) {
            for (DynamicProgramming.SubSolution belowSub : below) {
                if (leftSub.highestEndpoint < belowSub.lowestEndpoint) {
                    result.add(DynamicProgramming.SubSolution
                                       .merge(leftSub, belowSub));
                    continue;
                }

                int begin = endpoints.indexOf(belowSub.lowestEndpoint) - 1;
                int end = endpoints.indexOf(leftSub.highestEndpoint) + 1;

                for (int k = begin; k < end; k++) {
                    result.addAll(DynamicProgramming
                                          .merge(matrix, row, column, endpoints,
                                                 matrix[row][k],
                                                 matrix[k + 1][column]));
                }
            }
        }

        return result;
    }

    private static Region findRegion(final Iterable<Region> clique,
                                     final int begin, final int end) {
        for (Region region : clique) {
            if ((region.getBegin() == begin) && (region.getEnd() == end)) {
                return region;
            }
        }
        return null;
    }

    private static void fillConflictClique(final Set<Region> clique,
                                           final Region region,
                                           final ConflictMap conflictMap) {
        clique.add(region);

        for (Region other : conflictMap.conflictsWith(region)) {
            if (!clique.contains(other)) {
                DynamicProgramming.fillConflictClique(clique, other,
                                                      conflictMap);
            }
        }
    }
}
