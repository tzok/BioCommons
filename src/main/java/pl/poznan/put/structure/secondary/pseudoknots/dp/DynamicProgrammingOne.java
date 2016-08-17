package pl.poznan.put.structure.secondary.pseudoknots.dp;

import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.InvalidStructureException;
import pl.poznan.put.structure.secondary.pseudoknots.PseudoknotFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A class optimizes inclusion of pairs in the nested structure directly without
 * regions nomenclature.
 */
public class DynamicProgrammingOne implements PseudoknotFinder {
    // Create idMap to access connections and Array of connections
    private static List<Integer> filterNotConnected(
            final List<Integer> originalConnections,
            final Map<Integer, Integer> idMap) {
        int size = originalConnections.size();

        Map<Integer, Integer> originalToFiltered = new HashMap<>(size);
        List<Integer> filtered = new ArrayList<>(size);
        int id = 0;

        for (int i = 0; i < size; i++) {
            if (originalConnections.get(i) != -1) {
                originalToFiltered.put(i, id);
                idMap.put(id, i);
                id++;
                filtered.add(originalConnections.get(i));
            }
        }

        for (int i = 0; i < filtered.size(); i++) {
            int pos = filtered.get(i);
            filtered.set(i, originalToFiltered.get(pos));
        }
        return filtered;
    }

    // Create one range that contains all of the other
    private static List<Integer> addOuterRange(
            final Collection<Integer> connections) {
        List<Integer> res = new ArrayList<>(connections.size() + 2);

        res.add(connections.size() + 1);
        for (Integer connection : connections) {
            res.add(connection + 1);
        }
        res.add(0);

        return res;
    }

    // Create Ranges from given connections
    private static List<Range> createRanges(final List<Integer> connections) {
        List<Range> res = new ArrayList<>(connections.size());

        for (int i = 0; i < connections.size(); i++) {
            if (i < connections.get(i)) {
                res.add(new Range(i, connections.get(i)));
            }
        }
        return res;
    }

    // If connections is correct add them to final connections array (make
    // final correct connections)
    private static void appendCorrectConnections(final int i, final int j,
                                                 final List<Integer>
                                                         connections,
                                                 final int[][] dpPosition,
                                                 final List<Integer> appendTo) {
        if (i >= j) {
            return;
        }
        if (dpPosition[i][j] != -1) {
            int right = dpPosition[i][j];
            int left = connections.get(right);

            appendTo.add(left);
            appendTo.add(right);

            DynamicProgrammingOne
                    .appendCorrectConnections(left + 1, right - 1, connections,
                                              dpPosition, appendTo);
            DynamicProgrammingOne
                    .appendCorrectConnections(i, left - 1, connections,
                                              dpPosition, appendTo);
        }
    }

    // Tell which connections should be as the final ones
    private static List<Integer> keepSelected(final List<Integer> connections,
                                              final Iterable<Integer> toKeep) {
        boolean[] shouldKeep = new boolean[connections.size()];
        for (int i : toKeep) {
            shouldKeep[i] = true;
        }
        List<Integer> res = new ArrayList<>(connections);
        for (int i = 0; i < res.size(); i++) {
            if (!shouldKeep[i]) {
                res.set(i, -1);
            }
        }
        return res;
    }

    // Remove helping range that contains everything
    private static List<Integer> removeOuterRange(
            final List<Integer> connections) {
        List<Integer> res = new ArrayList<>(connections.size());

        for (int i = 1; i < (connections.size() - 1); i++) {
            if (connections.get(i) == -1) {
                res.add(-1);
            } else {
                res.add(connections.get(i) - 1);
            }
        }

        return res;
    }

    // Build final connections using idMap and filtered connections
    private static List<Integer> mapBack(final List<Integer> original,
                                         final List<Integer> filtered,
                                         final Map<Integer, Integer> idMap) {
        List<Integer> result = new ArrayList<>(original);

        for (int i = 0; i < filtered.size(); i++) {
            if (filtered.get(i) == -1) {
                result.set(idMap.get(i), -1);
            }
        }

        return result;
    }

    @Override
    public final List<BpSeq> findPseudoknots(final BpSeq bpSeq)
            throws InvalidStructureException {
        List<Character> sequence = new ArrayList<>(bpSeq.size());
        List<Integer> originalConnections = new ArrayList<>(bpSeq.size());

        for (BpSeq.Entry e : bpSeq.getEntries()) {
            int pair = e.getPair();
            char seq = e.getSeq();

            sequence.add(seq);
            originalConnections.add(pair - 1);
        }

        Map<Integer, Integer> idMap = new HashMap<>(bpSeq.size());
        List<Integer> connections = DynamicProgrammingOne
                .filterNotConnected(originalConnections, idMap);

        connections = DynamicProgrammingOne.addOuterRange(connections);
        int size = connections.size();

        // Create array of ranges and sort them
        List<Range> ranges = DynamicProgrammingOne.createRanges(connections);
        Collections.sort(ranges);

        // Initialize arrays needed for dynamic programming (it takes
        // approximately 2/3 of program execution time)
        int[][] dpPosition = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                dpPosition[i][j] = -1;
            }
        }

        // Fill dp array for results
        int[][] dpScore = new int[size][size];
        for (Range range : ranges) {
            int left = range.getLeft();
            int right = range.getRight();
            int[] prefixesScore = dpScore[left + 1];
            int[] prefixesPosition = dpPosition[left + 1];

            for (int pos = left + 2; pos < right; pos++) {
                int paired = connections.get(pos);

                if (!range.contains(paired) || (paired > pos)) {
                    if (prefixesScore[pos] < prefixesScore[pos - 1]) {
                        prefixesScore[pos] = prefixesScore[pos - 1];
                        prefixesPosition[pos] = prefixesPosition[pos - 1];
                    }
                } else if (paired < pos) {
                    if ((prefixesScore[pos - 1] < (prefixesScore[paired - 1]
                                                   + dpScore[paired][pos])) && (
                                prefixesScore[pos] < (prefixesScore[paired - 1]
                                                      + dpScore[paired][pos])
                        )) {
                        prefixesScore[pos] = prefixesScore[paired - 1]
                                             + dpScore[paired][pos];
                        prefixesPosition[pos] = pos;
                    } else if (prefixesScore[pos] < prefixesScore[pos - 1]) {
                        prefixesScore[pos] = prefixesScore[pos - 1];
                        prefixesPosition[pos] = prefixesPosition[pos - 1];
                    }
                }
            }
            dpScore[left][right] = 1 + prefixesScore[right - 1];
            dpPosition[left][right] = right;
        }


        List<Integer> correctConnections = new ArrayList<>(connections.size());
        DynamicProgrammingOne
                .appendCorrectConnections(1, size - 2, connections, dpPosition,
                                          correctConnections);

        List<Integer> nonConflicting = DynamicProgrammingOne
                .keepSelected(connections, correctConnections);
        nonConflicting = DynamicProgrammingOne.removeOuterRange(nonConflicting);

        List<Integer> structureCorrectConnections = DynamicProgrammingOne
                .mapBack(originalConnections, nonConflicting, idMap);

        for (int i = 0; i < structureCorrectConnections.size(); i++) {
            int originalConnection = originalConnections.get(i);
            if (originalConnection == structureCorrectConnections.get(i)) {
                originalConnections.set(i, -1);
                structureCorrectConnections.set(i, -1);
            }
        }

        List<BpSeq.Entry> entries = new ArrayList<>(bpSeq.size());

        for (int i = 0; i < sequence.size(); i++) {
            int index = i + 1;
            int pair = originalConnections.get(i) + 1;
            char seq = sequence.get(i);

            entries.add(new BpSeq.Entry(index, pair, seq));
        }

        return Collections.singletonList(new BpSeq(entries));
    }

    private static final class Range implements Comparable<Range> {
        private final int left;
        private final int right;

        private Range(final int left, final int right) {
            super();
            this.left = left;
            this.right = right;
        }

        private boolean contains(final int node) {
            return (node <= right) && (node >= left);
        }

        @Override
        public int compareTo(final Range t) {
            if (size() < t.size()) {
                return -1;
            }
            if (size() > t.size()) {
                return 1;
            }
            if (left < t.left) {
                return -1;
            }
            return 1;
        }

        private int size() {
            return Math.abs(left - right);
        }

        private int getLeft() {
            return left;
        }

        private int getRight() {
            return right;
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || (getClass() != obj.getClass())) {
                return false;
            }
            Range range = (Range) obj;
            return (left == range.left) && (right == range.right);
        }

        @Override
        public String toString() {
            return String.format("Range{left=%d, right=%d}", left, right);
        }
    }
}