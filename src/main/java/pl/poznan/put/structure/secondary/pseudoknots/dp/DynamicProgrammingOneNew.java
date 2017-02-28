package pl.poznan.put.structure.secondary.pseudoknots.dp;

import org.apache.commons.lang3.tuple.Pair;
import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.InvalidStructureException;
import pl.poznan.put.structure.secondary.pseudoknots.PseudoknotFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A new implementation of dynamic programming solution.
 */
public final class DynamicProgrammingOneNew implements PseudoknotFinder {
    @Override
    public List<BpSeq> findPseudoknots(final BpSeq bpSeq)
            throws InvalidStructureException {
        int size = bpSeq.size();
        Collection<Pair<Integer, Integer>> connections = new ArrayList<>(size);

        for (final BpSeq.Entry e : bpSeq.getEntries()) {
            int index = e.getIndex();
            int pair = e.getPair();

            if (index < pair) {
                connections.add(Pair.of(index, pair));
            }
        }

        Collection<List<Pair<Integer, Integer>>> solutions =
                DynamicProgrammingOneNew.dpNewAll(size, connections);
        List<BpSeq> results = new ArrayList<>(solutions.size());

        for (final List<Pair<Integer, Integer>> pairs : solutions) {
            List<BpSeq.Entry> entries = new ArrayList<>(bpSeq.getEntries());
            for (final Pair<Integer, Integer> pair : pairs) {
                BpSeq.Entry left = null;
                BpSeq.Entry right = null;
                for (int i = 0; i < entries.size(); i++) {
                    BpSeq.Entry entry = entries.get(i);
                    if (entry.getIndex() == pair.getLeft()) {
                        left = entry;
                        continue;
                    }
                    if (entry.getIndex() == pair.getRight()) {
                        right = entry;
                        break;
                    }
                }
                assert (left != null) && (right != null);
                entries.remove(left);
                entries.remove(right);
                entries.add(new BpSeq.Entry(left.getIndex(), 0, left.getSeq()));
                entries.add(
                        new BpSeq.Entry(right.getIndex(), 0, right.getSeq()));
            }

            results.add(new BpSeq(entries));
        }

        return results;
    }

    // New Dynamic Programmig.
    private static void getResults(
            final int p, final int k, final int[] beginnings,
            final int[] domeScore, final int[] prefixScore,
            final boolean makeResults) {
        prefixScore[p] = 0;
        for (int i = p + 1; i < k; i++) {
            prefixScore[i] = ((beginnings[i] > p) &&
                              // <- It checks if 'i' is ending of the dome
                              // which is in (p, k).
                              ((domeScore[i] + prefixScore[beginnings[i]])
                               > prefixScore[i - 1])) ? (domeScore[i]
                                                         +
                                                         prefixScore[beginnings[i]])
                                                      : prefixScore[i - 1];
        }
        if (makeResults) {
            domeScore[k] = prefixScore[k - 1] + 1;
        }
    }

    private static List<List<Pair<Integer, Integer>>> multiplyVectors(
            final List<List<Pair<Integer, Integer>>> left,
            final List<List<Pair<Integer, Integer>>> right) {
        // Optimize for empty vectors.
        if (left.get(0).isEmpty()) {
            return right;
        }
        if (right.get(0).isEmpty()) {
            return left;
        }

        // Optimize for single domes.
        if (left.get(0).size() == 1) {
            Pair<Integer, Integer> toAdd = left.get(0).get(0);
            for (final List<Pair<Integer, Integer>> aB : right) {
                aB.add(toAdd);
            }
            return right;
        }
        if (right.get(0).size() == 1) {
            Pair<Integer, Integer> toAdd = right.get(0).get(0);
            for (final List<Pair<Integer, Integer>> anA : left) {
                anA.add(toAdd);
            }
            return left;
        }
        List<List<Pair<Integer, Integer>>> merged = new ArrayList<>();
        for (final List<Pair<Integer, Integer>> anA : left) {
            for (final List<Pair<Integer, Integer>> aB : right) {
                List<Pair<Integer, Integer>> mergedV = new ArrayList<>();
                mergedV.addAll(anA);
                mergedV.addAll(aB);
                merged.add(mergedV);
            }
        }
        return merged;
    }

    private static List<List<Pair<Integer, Integer>>> mergeVectors(
            final List<List<Pair<Integer, Integer>>> left,
            final List<List<Pair<Integer, Integer>>> right) {
        if (left.get(0).isEmpty()) {
            return right;
        }
        if (right.get(0).isEmpty()) {
            return left;
        }
        List<List<Pair<Integer, Integer>>> merged = new ArrayList<>();
        merged.addAll(left);
        merged.addAll(right);
        return merged;
    }

    private static List<List<Pair<Integer, Integer>>> traceBack(
            final int p, final int k, final int[] beginnings,
            final int[] domeScore, final int[] prefixScore) {
        DynamicProgrammingOneNew
                .getResults(p, k, beginnings, domeScore, prefixScore, false);
        for (int i = k - 1; i > p; i--) {
            boolean firstIf = prefixScore[i] > prefixScore[i - 1];
            boolean secondIf = (prefixScore[i] == prefixScore[i - 1]) && (
                    prefixScore[i - 1] == (prefixScore[beginnings[i]]
                                           + domeScore[i])) && (beginnings[i]
                                                                != 0);

            if (firstIf) {
                List<Pair<Integer, Integer>> mypair = new ArrayList<>();
                mypair.add(Pair.of(beginnings[i], i));
                List<List<Pair<Integer, Integer>>> myself = new ArrayList<>();
                myself.add(mypair);

                return DynamicProgrammingOneNew.multiplyVectors(
                        DynamicProgrammingOneNew.multiplyVectors(myself,
                                                                 DynamicProgrammingOneNew
                                                                         .traceBack(
                                                                                 beginnings[i],
                                                                                 i,
                                                                                 beginnings,
                                                                                 domeScore,
                                                                                 prefixScore)),
                        DynamicProgrammingOneNew
                                .traceBack(p, beginnings[i], beginnings,
                                           domeScore, prefixScore));
            } else if (secondIf) {
                List<Pair<Integer, Integer>> mypair = new ArrayList<>();
                mypair.add(Pair.of(beginnings[i], i));
                List<List<Pair<Integer, Integer>>> myself = new ArrayList<>();
                myself.add(mypair);

                List<List<Pair<Integer, Integer>>> take =
                        DynamicProgrammingOneNew.multiplyVectors(
                                DynamicProgrammingOneNew.multiplyVectors(myself,
                                                                         DynamicProgrammingOneNew
                                                                                 .traceBack(
                                                                                         beginnings[i],
                                                                                         i,
                                                                                         beginnings,
                                                                                         domeScore,
                                                                                         prefixScore)),
                                DynamicProgrammingOneNew
                                        .traceBack(p, beginnings[i], beginnings,
                                                   domeScore, prefixScore));
                List<List<Pair<Integer, Integer>>> skip =
                        DynamicProgrammingOneNew
                                .traceBack(p, i, beginnings, domeScore,
                                           prefixScore);

                return DynamicProgrammingOneNew.mergeVectors(take, skip);
            }
        }
        List<List<Pair<Integer, Integer>>> results = new ArrayList<>();
        if (results.isEmpty()) {
            results.add(new ArrayList<>());
        }
        return results;
    }

    public static Collection<List<Pair<Integer, Integer>>> dpNewAll(
            final int size,
            final Iterable<Pair<Integer, Integer>> connections) {
        int[] beginnings = new int[size + 2];

        for (final Pair<Integer, Integer> p : connections) {
            int lower = Math.min(p.getLeft(), p.getRight());
            int upper = Math.max(p.getLeft(), p.getRight());
            beginnings[upper] = lower;
        }

        // Calculate dome scores.
        int[] prefixScore = new int[size + 2];
        int[] domeScore = new int[size + 2];
        for (int i = 1; i <= size; ++i) {
            if (beginnings[i] > 0) {
                DynamicProgrammingOneNew
                        .getResults(beginnings[i], i, beginnings, domeScore,
                                    prefixScore, true);
            }
        }
        DynamicProgrammingOneNew
                .getResults(0, size + 1, beginnings, domeScore, prefixScore,
                            true);

        // Track back pairs.
        return DynamicProgrammingOneNew
                .traceBack(0, size + 1, beginnings, domeScore, prefixScore);
    }

}
