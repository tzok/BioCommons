package pl.poznan.put.pdb.analysis;

import java.util.List;
import org.immutables.value.Value;

/** A container for a list of residues. */
@Value.Immutable
public abstract class DefaultResidueCollection implements ResidueCollection {
  @Override
  @Value.Parameter(order = 1)
  public abstract List<PdbResidue> residues();
}
