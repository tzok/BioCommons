package pl.poznan.put.pdb.analysis;

import org.apache.commons.lang3.StringUtils;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.ImmutablePdbAtomLine;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.rna.NucleotideTorsionAngle;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.torsion.AtomPair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** A collection of residues. */
@FunctionalInterface
public interface ResidueCollection extends Serializable {
  /** @return The list of residues. */
  List<PdbResidue> residues();

  /**
   * Creates a new instance of this class in which atoms with alternate locations are present only
   * once.
   *
   * @return A copy of the current instance, but without alternate locations in atoms.
   */
  default ResidueCollection withoutAlternateLocations() {
    final List<PdbResidue> residues = new ArrayList<>();

    for (final PdbResidue residue : residues()) {
      final Set<AtomName> resolved = EnumSet.noneOf(AtomName.class);
      final Collection<PdbAtomLine> atoms = new ArrayList<>();

      for (final PdbAtomLine atom : residue.atoms()) {
        if (!resolved.contains(atom.detectAtomName())) {
          atoms.add(ImmutablePdbAtomLine.copyOf(atom).withAlternateLocation(" "));
          resolved.add(atom.detectAtomName());
        }
      }

      residues.add(
          ImmutableDefaultPdbResidue.of(
              residue.identifier(),
              residue.standardResidueName(),
              residue.modifiedResidueName(),
              atoms));
    }

    return ImmutableDefaultResidueCollection.of(residues);
  }

  /**
   * Analyzes atomic bond lenths to find violations (too long or too short) and generates a report
   * in a form of a list of validation messages.
   *
   * @return A list of error messages.
   */
  default List<String> findBondLengthViolations() {
    final Set<AtomBasedTorsionAngleType> angleTypes =
        residues().stream()
            .map(PdbResidue::residueInformationProvider)
            .map(ResidueInformationProvider::torsionAngleTypes)
            .flatMap(Collection::stream)
            .filter(torsionAngleType -> torsionAngleType instanceof AtomBasedTorsionAngleType)
            .map(torsionAngleType -> (AtomBasedTorsionAngleType) torsionAngleType)
            .filter(torsionAngleType -> !torsionAngleType.isPseudoTorsion())
            .filter(
                torsionAngleType ->
                    !NucleotideTorsionAngle.CHI.angleTypes().contains(torsionAngleType))
            .collect(Collectors.toSet());

    final Set<AtomPair> atomPairs =
        IntStream.range(0, residues().size())
            .boxed()
            .flatMap(
                i ->
                    angleTypes.stream()
                        .map(angleType -> angleType.findAtomPairs(residues(), i))
                        .flatMap(Collection::stream))
            .collect(Collectors.toCollection(TreeSet::new));

    return atomPairs.stream()
        .map(AtomPair::generateValidationMessage)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toList());
  }

  /**
   * Checks if a given (chain, number, icode) is present in this collection of residues.
   *
   * @param query A residue identifier.
   * @return True if a given residue is part of this collection.
   */
  default boolean hasResidue(final ChainNumberICode query) {
    final PdbResidueIdentifier queryIdentifier = PdbResidueIdentifier.from(query);
    return residues().stream().map(PdbResidueIdentifier::from).anyMatch(queryIdentifier::equals);
  }

  /**
   * Finds a residue by a triplet (chain, number, icode).
   *
   * @param query A residue identifier.
   * @return The residue found in this collection of residues.
   */
  default PdbResidue findResidue(final ChainNumberICode query) {
    final PdbResidueIdentifier queryIdentifier = PdbResidueIdentifier.from(query);
    return residues().stream()
        .filter(residue -> Objects.equals(PdbResidueIdentifier.from(residue), queryIdentifier))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Failed to find residue: " + query));
  }

  /**
   * Finds a residue by a triplet (chain, number, icode).
   *
   * @param query A residue identifier.
   * @return The index of a residue found in this collection of residues.
   */
  default int indexOf(final ChainNumberICode query) {
    final PdbResidueIdentifier identifier = PdbResidueIdentifier.from(query);
    return IntStream.range(0, residues().size())
        .filter(i -> PdbResidueIdentifier.from(residues().get(i)).equals(identifier))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Failed to find residue: " + identifier));
  }

  /**
   * Generates a sequence out of this residue collection.
   *
   * @return A sequence of one-letter-codes e.g. ACGGGG.
   */
  default String sequence() {
    return residues().stream()
        .map(PdbResidue::oneLetterName)
        .map(String::valueOf)
        .collect(Collectors.joining());
  }

  /**
   * Filters atoms in this residue collection.
   *
   * @param moleculeType Type of molecule to leave in the result.
   * @return A list of atoms of a given type.
   */
  default List<PdbAtomLine> filteredAtoms(final MoleculeType moleculeType) {
    return residues().stream()
        .filter(
            pdbResidue -> pdbResidue.residueInformationProvider().moleculeType() == moleculeType)
        .filter(pdbResidue -> !pdbResidue.isMissing())
        .flatMap(pdbResidue -> pdbResidue.atoms().stream())
        .collect(Collectors.toList());
  }

  /** @return A list of residue identifiers. */
  default List<PdbResidueIdentifier> residueIdentifiers() {
    return residues().stream().map(PdbResidueIdentifier::from).collect(Collectors.toList());
  }

  /** @return A list of named residue identifiers. */
  default List<PdbNamedResidueIdentifier> namedResidueIdentifiers() {
    return residues().stream().map(PdbResidue::namedResidueIdentifer).collect(Collectors.toList());
  }

  /**
   * Generates a list of ATOM lines in PDB format from this instance.
   *
   * @return A representation of this residue collection in PDB format.
   */
  default String toPdb() {
    final StringBuilder builder = new StringBuilder();

    for (final PdbResidue residue : residues()) {
      builder.append(residue.toPdb());
      builder.append('\n');
    }

    return builder.toString();
  }

  /**
   * Generates a list of ATOM lines in mmCIF format from this instance.
   *
   * @return A representation of this residue collection in mmCIF format.
   */
  default String toCif() {
    final StringBuilder builder = new StringBuilder();
    builder.append("data_").append('\n');
    builder.append(PdbAtomLine.CIF_LOOP).append('\n');

    for (final PdbResidue residue : residues()) {
      builder.append(residue.toCif());
      builder.append('\n');
    }

    return builder.toString();
  }
}
