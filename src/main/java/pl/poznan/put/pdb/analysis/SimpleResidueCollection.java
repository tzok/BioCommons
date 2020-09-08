package pl.poznan.put.pdb.analysis;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class SimpleResidueCollection implements ResidueCollection {
  @Value.Parameter(order = 1)
  public abstract List<PdbResidue> residues();
}
