package pl.poznan.put.pdb.analysis;

import lombok.Data;
import pl.poznan.put.pdb.PdbResidueIdentifier;

import java.util.Collections;
import java.util.List;

@Data
public class SimpleResidueCollection implements ResidueCollection {
  private final List<PdbResidue> residues;

  @Override
  public final List<PdbResidue> getResidues() {
    return Collections.unmodifiableList(residues);
  }

  @Override
  public final PdbResidue findResidue(
      final String chainIdentifier, final int residueNumber, final String insertionCode) {
    final PdbResidueIdentifier identifier =
        new PdbResidueIdentifier(chainIdentifier, residueNumber, insertionCode);
    return findResidue(identifier);
  }

  @Override
  public final PdbResidue findResidue(final PdbResidueIdentifier query) {
    for (final PdbResidue residue : residues) {
      if (query.equals(residue.toResidueIdentifer())) {
        return residue;
      }
    }
    throw new IllegalArgumentException("Failed to find residue: " + query);
  }
}
