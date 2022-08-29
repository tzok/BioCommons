package pl.poznan.put.structure.pseudoknots.dp;

import java.util.List;
import pl.poznan.put.structure.pseudoknots.PseudoknotFinder;

/** A pseudoknot finder which works on a dynamic programming basis. */
interface DynamicProgramming extends PseudoknotFinder {
  /**
   * Solves a single conflict clique in an optimal way.
   *
   * @param conflictClique The conflict clique to solve.
   * @return A list of subsolutions, each with an optimal score.
   */
  List<SubSolution> findOptimalSolutions(final ConflictClique conflictClique);
}
