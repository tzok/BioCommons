package pl.poznan.put.structure.secondary.pseudoknots;

import pl.poznan.put.structure.secondary.pseudoknots.dp.DynamicProgrammingAll;
import pl.poznan.put.structure.secondary.pseudoknots.dp.DynamicProgrammingOne;
import pl.poznan.put.structure.secondary.pseudoknots.elimination.MaxConflicts;
import pl.poznan.put.structure.secondary.pseudoknots.elimination.MinGain;

/**
 * Enum listing all names and implementations of PseudoknotFinder
 */
public enum PseudoknotFinderEnum {
    EG("Elimination Min-Gain", new MinGain()),
    EC("Elimination Max-Conflicts", new MaxConflicts()),
    DP_ONE("Dynamic Programming", new DynamicProgrammingOne()),
    DP_ALL("Dynamic Programming (All)", new DynamicProgrammingAll());

    private final String name;
    private final PseudoknotFinder instance;

    PseudoknotFinderEnum(final String name, final PseudoknotFinder instance) {
        this.name = name;
        this.instance = instance;
    }

    public String getName() {
        return name;
    }

    public PseudoknotFinder getInstance() {
        return instance;
    }
}
