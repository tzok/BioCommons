package pl.poznan.put.structure.formats;

/** A converter from BPSEQ to dot-bracket. */
@FunctionalInterface
public interface Converter {
  /**
   * Converts the secondary structure from BPSEQ to dot-bracket format.
   *
   * @param bpSeq The data in BPSEQ format.
   * @return The resulting dot-bracket.
   */
  DotBracket convert(final BpSeq bpSeq);
}
