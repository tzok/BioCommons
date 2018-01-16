package pl.poznan.put.structure.secondary.pseudoknots.dp;

import pl.poznan.put.structure.secondary.pseudoknots.PseudoknotFinder;

/** Interface for pseudoknot finders which work on a dynamic programming basis. */
public interface DynamicProgramming extends PseudoknotFinder {
  SubSolution[] findOptimalSolutions(final Clique clique);
}
