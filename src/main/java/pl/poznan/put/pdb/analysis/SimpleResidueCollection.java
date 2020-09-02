package pl.poznan.put.pdb.analysis;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class SimpleResidueCollection implements ResidueCollection {
  private final List<PdbResidue> residues;

  @Override
  public final List<PdbResidue> residues() {
    return Collections.unmodifiableList(residues);
  }
}
