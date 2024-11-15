package pl.poznan.put.pdb.analysis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

/** A container for a list of residues. */
@Value.Immutable
@JsonSerialize(as = ImmutableDefaultResidueCollection.class)
@JsonDeserialize(as = ImmutableDefaultResidueCollection.class)
public abstract class DefaultResidueCollection implements ResidueCollection {
  @Override
  @Value.Parameter(order = 1)
  public abstract List<PdbResidue> residues();
}
