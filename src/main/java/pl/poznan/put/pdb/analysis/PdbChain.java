package pl.poznan.put.pdb.analysis;

import org.biojava.nbio.structure.Chain;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/** A chain in a structure. */
@Value.Immutable
public abstract class PdbChain implements Comparable<PdbChain>, SingleTypedResidueCollection {
  /**
   * Creates an instance of this class from a chain instance from BioJava.
   *
   * @param chain An instance of Chain from BioJava.
   * @return An instance of this class with data converted from BioJava chain.
   */
  public static PdbChain fromBioJavaChain(final Chain chain) {
    final List<PdbResidue> residues =
        chain.getAtomGroups().stream()
            .map(PdbResidue::fromBioJavaGroup)
            .collect(Collectors.toList());
    return ImmutablePdbChain.of(chain.getId(), residues);
  }

  /** @return The chain identifier. */
  @Value.Parameter(order = 1)
  public abstract String identifier();

  @Value.Parameter(order = 2)
  public abstract List<PdbResidue> residues();

  @Override
  public final int compareTo(@Nonnull final PdbChain t) {
    return identifier().compareTo(t.identifier());
  }
}
