package pl.poznan.put.structure.secondary.pseudoknots;

import pl.poznan.put.structure.secondary.formats.BpSeq;

/**
 * Interface for classes which remove pseudoknots from secondary structures.
 */
public interface PseudoknotRemover {
    void removePseudoknots(BpSeq bpSeq);
}
