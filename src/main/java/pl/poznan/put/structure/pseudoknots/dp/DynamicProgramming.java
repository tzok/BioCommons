package pl.poznan.put.structure.pseudoknots.dp;

import pl.poznan.put.structure.pseudoknots.PseudoknotFinder;

/** Interface for pseudoknot finders which work on a dynamic programming basis. */
interface DynamicProgramming extends PseudoknotFinder {
  SubSolution[] findOptimalSolutions(final Clique clique);
}
