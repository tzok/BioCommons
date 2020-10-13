package pl.poznan.put.pdb.analysis;

import org.immutables.value.Value;

import java.util.List;

/** A container for a list of residues. */
@Value.Immutable
public abstract class DefaultResidueCollection implements ResidueCollection {
  @Override
  @Value.Parameter(order = 1)
  public abstract List<PdbResidue> residues();
}
