package pl.poznan.put.structure.formats;

/** An interface for classes which convert from BPSEQ to dot-bracket. */
@FunctionalInterface
public interface Converter {
  DefaultDotBracket convert(final BpSeq bpSeq);
}
