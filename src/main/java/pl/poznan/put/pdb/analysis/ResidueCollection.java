package pl.poznan.put.pdb.analysis;

import org.apache.commons.lang3.StringUtils;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.ImmutablePdbResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.rna.torsion.Chi;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.torsion.PseudoTorsionAngleType;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FunctionalInterface
public interface ResidueCollection {
  default List<String> findBondLengthViolations() {
    final Set<AtomBasedTorsionAngleType> angleTypes =
        residues().stream()
            .map(PdbResidue::torsionAngleTypes)
            .flatMap(Collection::stream)
            .filter(torsionAngleType -> torsionAngleType instanceof AtomBasedTorsionAngleType)
            .filter(torsionAngleType -> !(torsionAngleType instanceof PseudoTorsionAngleType))
            .filter(torsionAngleType -> !(torsionAngleType instanceof Chi))
            .map(torsionAngleType -> (AtomBasedTorsionAngleType) torsionAngleType)
            .collect(Collectors.toSet());

    final Set<AtomBasedTorsionAngleType.AtomPair> atomPairs =
        IntStream.range(0, residues().size())
            .boxed()
            .flatMap(
                i ->
                    angleTypes.stream()
                        .map(angleType -> angleType.findAtomPairs(residues(), i))
                        .flatMap(Collection::stream))
            .collect(Collectors.toCollection(TreeSet::new));

    return atomPairs.stream()
        .map(AtomBasedTorsionAngleType.AtomPair::generateValidationMessage)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toList());
  }

  List<PdbResidue> residues();

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

  default List<PdbNamedResidueIdentifier> namedResidueIdentifers() {
    return residues().stream().map(PdbResidue::namedResidueIdentifer).collect(Collectors.toList());
  }
}
