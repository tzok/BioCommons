package pl.poznan.put.pdb.analysis;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/** A collection of residues with a common molecule type (RNA or protein). */
@FunctionalInterface
public interface SingleTypedResidueCollection extends ResidueCollection {
  /** @return A detected type of the chain (RNA or protein). */
  default MoleculeType moleculeType() {
    final Map<MoleculeType, Integer> typeCount =
        residues().stream()
            .map(PdbResidue::residueInformationProvider)
            .collect(
                Collectors.toMap(ResidueInformationProvider::moleculeType, o -> 1, Integer::sum));
    return typeCount.entrySet().stream()
        .max(Comparator.comparingInt(Map.Entry::getValue))
        .map(Map.Entry::getKey)
        .orElse(MoleculeType.UNKNOWN);
  }
}
