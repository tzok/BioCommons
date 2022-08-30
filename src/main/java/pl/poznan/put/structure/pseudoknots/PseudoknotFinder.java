package pl.poznan.put.structure.pseudoknots;

import java.util.List;
import pl.poznan.put.structure.formats.BpSeq;

/** A finder of pseudoknots in RNA secondary structure. */
@FunctionalInterface
public interface PseudoknotFinder {
  /**
   * Finds pseudoknots in BPSEQ data. Pseudoknots are defined here as BPSEQ entries which if removed
   * will make the RNA secondary structure fully nested. There may be many subsets of pairs
   * considered pseudoknots for a single BPSEQ input.
   *
   * @param bpSeq An input BPSEQ structure with all pairs.
   * @return A list of BPSEQ structures where each contains only pairs considered to be pseudoknots.
   *     Each BPSEQ is a full copy of original one, but contains zeroed 'pair' columns for entries
   *     which are non-pseudoknots.
   */
  List<BpSeq> findPseudoknots(BpSeq bpSeq);
}
