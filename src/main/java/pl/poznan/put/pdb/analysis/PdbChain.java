package pl.poznan.put.pdb.analysis;

import java.util.List;
import javax.annotation.Nonnull;
import org.immutables.value.Value;

/** A chain in a structure. */
@Value.Immutable
public abstract class PdbChain implements Comparable<PdbChain>, SingleTypedResidueCollection {
  /**
   * @return The chain identifier.
   */
  @Value.Parameter(order = 1)
  public abstract String identifier();

  @Override
  @Value.Parameter(order = 2)
  public abstract List<PdbResidue> residues();

  @Override
  public final int compareTo(@Nonnull final PdbChain t) {
    return identifier().compareTo(t.identifier());
  }
}
