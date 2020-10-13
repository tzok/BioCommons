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

/** A structure parsed from a PDB file. */
public interface PdbModel extends ResidueCollection {
  /** @return The structure header. */
  PdbHeaderLine header();

  /** @return Details about experiment used to solve the structure. */
  PdbExpdtaLine experimentalData();

  /** @return Information about the experimental resolution. */
  PdbRemark2Line resolution();

  /** @return Model number as stated in the PDB or mmCIF file. */
  int modelNumber();

  /** @return The list of atoms present in the structure. */
  List<PdbAtomLine> atoms();

  /** @return The list of modified residues as parsed from the PDB or mmCIF file. */
  List<PdbModresLine> modifiedResidues();

  /** @return The list of missing residues as parsed from the PDB or mmCIF file. */
  List<PdbRemark465Line> missingResidues();

  /** @return Structure title. */
  String title();

  /** @return The set of residues, after which the chain was terminated. */
  Set<PdbResidueIdentifier> chainTerminatedAfter();

  /** @return The list of chains in the structure. */
  List<PdbChain> chains();

  /**
   * Filters out residues of a given molecule type (RNA or protein) and creates a new instance of
   * this class.
   *
   * @param moleculeType Type of molecule.
   * @return An instance of this class with residues only of a desired type.
   */
  PdbModel filteredNewInstance(MoleculeType moleculeType);

  /** @return PDB id of the structure. */
  default String idCode() {
    return header().idCode();
  }

  /**
   * Checks if any chain is of a given type.
   *
   * @param moleculeType The type of molecule to check.
   * @return True if at least one chain is of given type.
   */
  default boolean containsAny(final MoleculeType moleculeType) {
    return chains().stream().anyMatch(chain -> chain.moleculeType() == moleculeType);
  }

  /**
   * Finds a chain which has a given residue.
   *
   * @param query A triplet of (chain, number, icode) to look for.
   * @return A chain with desired residue.
   */
  default SingleTypedResidueCollection findChainContainingResidue(final ChainNumberICode query) {
    return chains().stream()
        .filter(pdbChain -> pdbChain.hasResidue(query))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException("Failed to find chain containing residue: " + query));
  }

  /**
   * Finds all missing residues of the given type.
   *
   * @param moleculeType The type of molecule to look for.
   * @return A list of missing residues.
   */
  default List<PdbRemark465Line> filteredMissing(final MoleculeType moleculeType) {
    return residues().stream()
        .filter(
            pdbResidue -> pdbResidue.residueInformationProvider().moleculeType() == moleculeType)
        .filter(PdbResidue::isMissing)
        .map(
            pdbResidue ->
                ImmutablePdbRemark465Line.of(
                    modelNumber(),
                    pdbResidue.standardResidueName(),
                    pdbResidue.chainIdentifier(),
                    pdbResidue.residueNumber(),
                    pdbResidue.insertionCode()))
        .collect(Collectors.toList());
  }

  /**
   * Checks if a given residue is modified (as stated in the PDB or mmCIF headers).
   *
   * @param query An identifier of a residue.
   * @return True if the residue is modified.
   */
  default boolean isModified(final PdbResidueIdentifier query) {
    return modifiedResidues().stream()
        .anyMatch(modifiedResidue -> PdbResidueIdentifier.from(modifiedResidue).equals(query));
  }

  /**
   * Provides details about modification of the residue.
   *
   * @param query An identifier of a residue.
   * @return An object containing details about residue modification.
   */
  default PdbModresLine modificationDetails(final PdbResidueIdentifier query) {
    return modifiedResidues().stream()
        .filter(modifiedResidue -> PdbResidueIdentifier.from(modifiedResidue).equals(query))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Failed to find information about modification of: " + query));
  }
}
