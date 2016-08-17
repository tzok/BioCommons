package pl.poznan.put.structure.secondary.pseudoknots.dp;

import pl.poznan.put.structure.secondary.pseudoknots.elimination.RegionRemover;

/**
 * A serial implementation (matrix is filled piece by piece).
 */
public class DynamicProgrammingAll extends AbstractDynamicProgramming {
    /**
     * Construct an instance of dynamic solver for all solutions. The
     * regionRemover will be used only until any clique has size bigger then
     * maxCliqueSize! If not, the conflicts are resolved optimally.
     *
     * @param regionRemover A region remover to be used.
     * @param maxCliqueSize Maximum number of conflicts allowed to be in the
     *                      clique. The algorithm slows down very much when the
     *                      cliques are getting bigger, so it is advisable to
     *                      use heuristic to remove single regions prior to a
     */
    public DynamicProgrammingAll(final RegionRemover regionRemover,
                                 final int maxCliqueSize) {
        super(regionRemover, maxCliqueSize);
    }

    public DynamicProgrammingAll() {
        super(null, Integer.MAX_VALUE);
    }

    @Override
    public final SubSolution[] findOptimalSolutions(final Clique clique) {
        int size = clique.endpointCount();
        SubSolution[][][] matrix = new SubSolution[size][size][0];

        for (int j = 1; j < size; j++) {
            for (int i = j - 1; i >= 0; i--) {
                matrix[i][j] = AbstractDynamicProgramming
                        .solveSingleCase(matrix, clique, i, j);
            }
        }

        return matrix[0][size - 1];
    }
}
