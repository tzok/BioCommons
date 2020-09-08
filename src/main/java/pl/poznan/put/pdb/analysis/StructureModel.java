package pl.poznan.put.pdb.analysis;

import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.ImmutablePdbRemark465Line;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbResidueIdentifier;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface StructureModel extends ResidueCollection {
  PdbHeaderLine header();

  PdbExpdtaLine experimentalData();

  PdbRemark2Line resolution();

  int modelNumber();

  List<PdbModresLine> modifiedResidues();

  List<PdbRemark465Line> missingResidues();

  List<PdbAtomLine> atoms();

  List<PdbChain> chains();

  String title();

  Set<PdbResidueIdentifier> chainTerminatedAfter();

  StructureModel filteredNewInstance(MoleculeType moleculeType);

  default String idCode() {
    return header().idCode();
  }

  default boolean containsAny(final MoleculeType moleculeType) {
    return chains().stream().anyMatch(chain -> chain.moleculeType() == moleculeType);
  }

  default PdbChain findChainContainingResidue(final ChainNumberICode query) {
    return chains().stream()
        .filter(pdbChain -> pdbChain.hasResidue(query))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException("Failed to find chain containing residue: " + query));
  }

  default List<PdbRemark465Line> filteredMissing(final MoleculeType moleculeType) {
    return residues().stream()
        .filter(pdbResidue -> pdbResidue.getMoleculeType() == moleculeType)
        .filter(PdbResidue::isMissing)
        .map(
            pdbResidue ->
                ImmutablePdbRemark465Line.of(
                    modelNumber(),
                    pdbResidue.residueName(),
                    pdbResidue.chainIdentifier(),
                    pdbResidue.residueNumber(),
                    pdbResidue.insertionCode()))
        .collect(Collectors.toList());
  }
}
