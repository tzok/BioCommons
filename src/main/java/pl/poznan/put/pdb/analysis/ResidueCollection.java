package pl.poznan.put.pdb.analysis;

import org.apache.commons.lang3.StringUtils;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.CifConstants;
import pl.poznan.put.pdb.ImmutablePdbAtomLine;
import pl.poznan.put.pdb.ImmutablePdbResidueIdentifier;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.rna.torsion.Chi;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.torsion.AtomPair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface ResidueCollection {
  List<PdbResidue> residues();

  default List<String> findBondLengthViolations() {
    final Set<AtomBasedTorsionAngleType> angleTypes =
        residues().stream()
            .map(PdbResidue::torsionAngleTypes)
            .flatMap(Collection::stream)
            .filter(torsionAngleType -> torsionAngleType instanceof AtomBasedTorsionAngleType)
            .map(torsionAngleType -> (AtomBasedTorsionAngleType) torsionAngleType)
            .filter(torsionAngleType -> !torsionAngleType.isPseudoTorsion())
            .filter(torsionAngleType -> !Objects.equals(torsionAngleType, Chi.PURINE_CHI))
            .filter(torsionAngleType -> !Objects.equals(torsionAngleType, Chi.PYRIMIDINE_CHI))
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

  default PdbResidue findResidue(
      final String chainIdentifier, final int residueNumber, final String insertionCode) {
    return findResidue(
        ImmutablePdbResidueIdentifier.of(chainIdentifier, residueNumber, insertionCode));
  }

  default PdbResidue findResidue(final ChainNumberICode query) {
    return residues().stream()
        .filter(residue -> Objects.equals(residue.toResidueIdentifer(), query.toResidueIdentifer()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Failed to find residue: " + query));
  }

  default boolean hasResidue(
      final String chainIdentifier, final int residueNumber, final String insertionCode) {
    return hasResidue(
        ImmutablePdbResidueIdentifier.of(chainIdentifier, residueNumber, insertionCode));
  }

  default boolean hasResidue(final ChainNumberICode query) {
    return residues().stream()
        .anyMatch(
            residue -> Objects.equals(residue.toResidueIdentifer(), query.toResidueIdentifer()));
  }

  default List<PdbNamedResidueIdentifier> namedResidueIdentifiers() {
    return residues().stream().map(PdbResidue::namedResidueIdentifer).collect(Collectors.toList());
  }

  default String sequence() {
    return residues().stream()
        .map(residue -> String.valueOf(residue.oneLetterName()))
        .collect(Collectors.joining());
  }

  default List<PdbAtomLine> filteredAtoms(final MoleculeType moleculeType) {
    return residues().stream()
        .filter(pdbResidue -> pdbResidue.moleculeType() == moleculeType)
        .filter(pdbResidue -> !pdbResidue.isMissing())
        .flatMap(pdbResidue -> pdbResidue.atoms().stream())
        .collect(Collectors.toList());
  }

  default List<PdbResidueIdentifier> residueIdentifiers() {
    return residues().stream().map(PdbResidue::toResidueIdentifer).collect(Collectors.toList());
  }

  default String toPdb() {
    final StringBuilder builder = new StringBuilder();

    for (final PdbResidue residue : residues()) {
      builder.append(residue.toPdb());
      builder.append('\n');
    }

    return builder.toString();
  }

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

      residues.add(ImmutablePdbResidue.copyOf(residue).withAtoms(atoms));
    }

    return ImmutableSimpleResidueCollection.of(residues);
  }

  default String toCif() {
    final StringBuilder builder = new StringBuilder();
    builder.append("data_").append('\n');
    builder.append(CifConstants.CIF_LOOP).append('\n');

    for (final PdbResidue residue : residues()) {
      builder.append(residue.toCif());
      builder.append('\n');
    }

    return builder.toString();
  }

  default int indexOf(final ChainNumberICode query) {
    final PdbResidueIdentifier identifier = query.toResidueIdentifer();
    return IntStream.range(0, residues().size())
        .filter(i -> residues().get(i).toResidueIdentifer().equals(identifier))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Failed to find residue: " + identifier));
  }
}
