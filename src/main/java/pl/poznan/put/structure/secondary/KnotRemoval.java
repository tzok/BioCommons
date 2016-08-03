package pl.poznan.put.structure.secondary;

import pl.poznan.put.structure.secondary.formats.BpSeq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class KnotRemoval {
    private static final char[] openingBracket =
            {'(', '[', '{', '<', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
             'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
             'W', 'X', 'Y', 'Z'};
    private static final char[] closingBracket =
            {')', ']', '}', '>', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
             'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
             'w', 'x', 'y', 'z'};

    // Dynamic Programming
/*
    public static RNAStructure dynamicProgramming(RNAStructure structure) {
        // Create a deep copy of RNAStructure
        RNAStructure str = new RNAStructure(structure);
        str.DP = true;
        int bracket = -1;
        while (KnotRemoval.hasPairs(str)) {
            ++bracket;
            Map<Integer, Integer> idMap = new HashMap<>();
            ArrayList<Integer> connections = KnotRemoval.filterNotConnected(
                    str.originalConnections, idMap);

            connections = KnotRemoval.addOuterRange(connections);
            int size = connections.size();

            // Create array of ranges and sort them
            ArrayList<Range> ranges = KnotRemoval.createRanges(connections);
            Collections.sort(ranges);

            // Initialize arrays needed for dynamic programming (it takes
            // approximately 2/3 of program execution time)
            int[][] dpScore = new int[size][size];
            int[][] dpPosition = new int[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dpPosition[i][j] = -1;
                }
            }

            // Fill dp array for results
            for (Range range : ranges) {
                int[] prefixesScore = dpScore[range.left + 1];
                int[] prefixesPosition = dpPosition[range.left + 1];

                for (int pos = range.left + 1; pos < range.right; pos++) {
                    int paired = connections.get(pos);

                    if (pos == range.left + 1) {
                        continue;
                    }

                    if (!range.contains(paired) || (paired > pos)) {
                        if (prefixesScore[pos] < prefixesScore[pos - 1]) {
                            prefixesScore[pos] = prefixesScore[pos - 1];
                            prefixesPosition[pos] = prefixesPosition[pos - 1];
                        }
                    } else if (paired < pos) {
                        if ((prefixesScore[pos - 1] <
                             (prefixesScore[paired - 1] +
                              dpScore[paired][pos])) && (prefixesScore[pos] <
                                                         (prefixesScore[paired -
                                                                        1] +
                                                          dpScore[paired][pos]))) {
                            prefixesScore[pos] = prefixesScore[paired - 1] +
                                                 dpScore[paired][pos];
                            prefixesPosition[pos] = pos;
                        } else if (prefixesScore[pos] <
                                   prefixesScore[pos - 1]) {
                            prefixesScore[pos] = prefixesScore[pos - 1];
                            prefixesPosition[pos] = prefixesPosition[pos - 1];
                        }
                    }
                }
                dpScore[range.left][range.right] =
                        1 + prefixesScore[range.right - 1];
                dpPosition[range.left][range.right] = range.right;
            }


            ArrayList<Integer> correctConnections = new ArrayList<>();
            KnotRemoval.appendCorrectConnections(1, size - 2, connections,
                                                 dpPosition,
                                                 correctConnections);

            ArrayList<Integer> nonConflicting = KnotRemoval.keepSelected(
                    connections, correctConnections);
            nonConflicting = KnotRemoval.removeOuterRange(nonConflicting);

            str.correctConnections = KnotRemoval.mapBack(
                    str.originalConnections, nonConflicting, idMap);

            KnotRemoval.updateDotBracket(str, bracket);
            KnotRemoval.removeUsedConnections(str);
        }

        return str;
    }
*/

    // Elimination Gain
    public static void eliminationGain(final BpSeq bpSeq) {
        // Initialize all variables for Elimination Gain function
        List<Region> regions = KnotRemoval.createRegions(bpSeq);
        ConflictMap conflictMap = new ConflictMap(regions);

        // Main loop with function
        while (conflictMap.hasConflicts()) {
            Region region = KnotRemoval.findMinGain(conflictMap);
            region.setRemoved(true);
            conflictMap.remove(region);
        }

        // Check if removed regions are still conflicting, if not add
        // them back
        KnotRemoval.restoreNonConflicting(regions);

        for (Region region : regions) {
            if (region.isRemoved()) {
                for (BpSeq.Entry entry : region.getEntries()) {
                    bpSeq.removePair(entry);
                }
            }
        }
    }

    ///////////////////////////////////
    // Elimination Gain functions
    ///////////////////////////////////
    // Create Array of Regions from connections stored in RNAStructure
    private static List<Region> createRegions(final BpSeq bpSeq) {
        List<Region> regions = new ArrayList<>();
        List<BpSeq.Entry> regionEntries = new ArrayList<>();
        List<BpSeq.Entry> allEntries = new ArrayList<>(bpSeq.getPaired());

        for (BpSeq.Entry entry : allEntries) {
            if (regionEntries.isEmpty()) {
                regionEntries.add(entry);
                continue;
            }

            BpSeq.Entry last = regionEntries.get(regionEntries.size() - 1);
            if ((entry.getIndex() == (last.getIndex() + 1)) &&
                (entry.getPair() == (last.getPair() - 1))) {
                regionEntries.add(entry);
                continue;
            }

            regions.add(new Region(regionEntries));
            regionEntries.clear();
            regionEntries.add(entry);
        }

        if (!regionEntries.isEmpty()) {
            regions.add(new Region(regionEntries));
        }

        return regions;
    }

    // Rewritten find_min_gain function from PyCogent-1.5.3
    private static Region findMinGain(final ConflictMap conflictMap) {
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
            return minGainRegions.get(0);
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

        return maxConflictCountRegion;
    }

    // Unremove all Regions that were removed but are no longer in conflict
    private static void restoreNonConflicting(final List<Region> regions) {
        for (Region ri : regions) {
            if (!ri.isRemoved()) {
                continue;
            }

            boolean conflicting = false;
            for (Region rj : regions) {
                if (!rj.isRemoved() && ConflictMap.isConflicting(ri, rj)) {
                    conflicting = true;
                    break;
                }
            }

            if (!conflicting) {
                ri.setRemoved(false);
            }
        }
    }

    public static ArrayList<Pair> toPairs(List<Region> regions) {
        ArrayList<Pair> connections = new ArrayList<>();
        int start, end;


        for (int i = 0; i < regions.size(); i++) {
            if (!regions.get(i).isRemoved()) {
                start = regions.get(i).getBegin();
                end = regions.get(i).getEnd();
                for (int j = 0; j < regions.get(i).getLength(); j++) {
                    connections.add(new Pair(start + j, end - j));
                }
            }
        }

        return connections;
    }

    // Update correct connections from pairs
    private static void updateConnectionsFromPairs(RNAStructure str) {
        ArrayList<Integer> correctConnections = new ArrayList<>();
        int size = str.rnaSequence.size();
        int[] temp = new int[size + 2];

        for (int i = 0; i < str.connections.size(); i++) {
            temp[str.connections.get(i).getFirst()] = str.connections.get(i)
                                                                     .getSecond();
            temp[str.connections.get(i).getSecond()] = str.connections.get(i)
                                                                      .getFirst();
        }
        for (int i = 0; i < str.rnaSequence.size(); i++) {
            correctConnections.add(temp[i + 1]);
        }
        str.correctConnections = correctConnections;
    }

    private static void updateDotBracket(RNAStructure str, int bracket) {
        int isDP = 0;
        if (str.DP) {
            isDP = -1;
        }
        for (int i = 0; i < str.correctConnections.size(); i++) {
            if (str.correctConnections.get(i) > i && str.correctConnections.get(
                    i) != isDP) {
                str.DotBracket.set(i, KnotRemoval.openingBracket[bracket]);
            } else if (str.correctConnections.get(i) <= i &&
                       str.correctConnections.get(i) != isDP) {
                str.DotBracket.set(i, KnotRemoval.closingBracket[bracket]);
            }
        }
    }

    /////////////////////////////////////
    // Dynamic Programming functions
    /////////////////////////////////////

    private static void removeUsedConnections(RNAStructure str) {
        int isDP = 1;
        if (str.DP) {
            isDP = 0;
        }
        for (int i = 0; i < str.correctConnections.size(); i++) {
            if (str.originalConnections.get(i) + isDP ==
                str.correctConnections.get(i)) {
                str.originalConnections.set(i, isDP - 1);
                str.correctConnections.set(i, isDP - 1);
            }
        }
    }

    private static void updateConnectionPairs(RNAStructure str) {
        str.connections.clear();
        for (int i = 0; i < str.originalConnections.size(); i++) {
            if (i < str.originalConnections.get(i)) {
                str.connections.add(
                        new Pair(i + 1, str.originalConnections.get(i) + 1));
            }
        }
    }

    // Create idMap to access connections and Array of connections
    private static ArrayList<Integer> filterNotConnected(
            ArrayList<Integer> originalConnections,
            Map<Integer, Integer> idMap) {
        ArrayList<Integer> filtered = new ArrayList<>();
        int id = 0;
        Map<Integer, Integer> originalToFiltered = new HashMap<>();
        for (int i = 0; i < originalConnections.size(); i++) {
            if (originalConnections.get(i) != -1) {
                originalToFiltered.put(i, id);
                idMap.put(id++, i);
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
    private static ArrayList<Integer> addOuterRange(
            ArrayList<Integer> connections) {
        ArrayList<Integer> res = new ArrayList<>();

        res.add(connections.size() + 1);
        for (int i = 0; i < connections.size(); i++) {
            res.add(connections.get(i) + 1);
        }
        res.add(0);

        return res;
    }

    // Create Ranges from given connections
    private static ArrayList<Range> createRanges(
            ArrayList<Integer> connections) {
        ArrayList<Range> res = new ArrayList<>();

        for (int i = 0; i < connections.size(); i++) {
            if (i < connections.get(i)) {
                res.add(new Range(i, connections.get(i)));
            }
        }
        return res;
    }

    // If connections is correct add them to final connections array (make
    // final correct connections)
    private static void appendCorrectConnections(int a, int b,
                                                 ArrayList<Integer> connections,
                                                 int[][] dpPosition,
                                                 ArrayList<Integer> appendTo) {
        if (a >= b) {
            return;
        }
        if (dpPosition[a][b] != -1) {
            int right = dpPosition[a][b];
            int left = connections.get(right);

            appendTo.add(left);
            appendTo.add(right);

            KnotRemoval.appendCorrectConnections(left + 1, right - 1,
                                                 connections, dpPosition,
                                                 appendTo);
            KnotRemoval.appendCorrectConnections(a, left - 1, connections,
                                                 dpPosition, appendTo);
        }
    }

    /////////////////////////////////////
    // Misc functions
    /////////////////////////////////////

    // Tell which connections should be as the final ones
    private static ArrayList<Integer> keepSelected(
            ArrayList<Integer> connections, ArrayList<Integer> toKeep) {
        boolean[] shouldKeep = new boolean[connections.size()];
        Arrays.fill(shouldKeep, false);
        for (int c : toKeep) {
            shouldKeep[c] = true;
        }
        ArrayList<Integer> res = new ArrayList<>(connections);
        for (int i = 0; i < res.size(); i++) {
            if (!shouldKeep[i]) {
                res.set(i, -1);
            }
        }
        return res;
    }

    // Remove helping range that contains everything
    private static ArrayList<Integer> removeOuterRange(
            ArrayList<Integer> connections) {
        ArrayList<Integer> res = new ArrayList<>();

        for (int i = 1; i < connections.size() - 1; i++) {
            if (connections.get(i) == -1) {
                res.add(-1);
            } else {
                res.add(connections.get(i) - 1);
            }
        }

        return res;
    }

    // Build final connections using idMap and filtered connections
    private static ArrayList<Integer> mapBack(ArrayList<Integer> original,
                                              ArrayList<Integer> filtered,
                                              Map<Integer, Integer> idMap) {
        ArrayList<Integer> result = new ArrayList<>(original);

        for (int i = 0; i < filtered.size(); i++) {
            if (filtered.get(i) == -1) {
                result.set(idMap.get(i), -1);
            }
        }

        return result;
    }
}
