package pl.poznan.put.structure.secondary.pseudoknots;

import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.InvalidStructureException;

import java.util.List;

/**
 * Interface for classes which find pseudoknots from secondary structures.
 */
public interface PseudoknotFinder {
    /**
     * Find pairs in 'flat' BPSEQ information which are pseudoknots i.e. their
     * removal will leave a nested RNA structure. Potentially from a single
     * BPSEQ, there can be many subsets of pairs considered to be pseudoknots.
     *
     * @param bpSeq An input BPSEQ structure with all pairs.
     * @return A list of BPSEQ structures where each contains only pairs
     * considered to be pseudoknots. Each BPSEQ is a full copy of original one,
     * but contains zeroed 'pair' columns for entries which are
     * non-pseudoknots.
     * @throws InvalidStructureException If recreation of BPSEQ fails.
     */
    List<BpSeq> findPseudoknots(BpSeq bpSeq) throws InvalidStructureException;
}
