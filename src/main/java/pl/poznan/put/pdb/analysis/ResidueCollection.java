package pl.poznan.put.pdb.analysis;

import org.apache.commons.lang3.StringUtils;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.rna.torsion.Chi;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.torsion.PseudoTorsionAngleType;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface ResidueCollection {
  default List<String> findBondLengthViolations() {
    final Set<AtomBasedTorsionAngleType> angleTypes =
        getResidues().stream()
            .map(PdbResidue::getTorsionAngleTypes)
            .flatMap(Collection::stream)
            .filter(torsionAngleType -> torsionAngleType instanceof AtomBasedTorsionAngleType)
            .filter(torsionAngleType -> !(torsionAngleType instanceof PseudoTorsionAngleType))
            .filter(torsionAngleType -> !(torsionAngleType instanceof Chi))
            .map(torsionAngleType -> (AtomBasedTorsionAngleType) torsionAngleType)
            .collect(Collectors.toSet());

    final Set<AtomBasedTorsionAngleType.AtomPair> atomPairs =
        IntStream.range(0, getResidues().size())
            .boxed()
            .flatMap(
                i ->
                    angleTypes.stream()
                        .map(angleType -> angleType.findAtomPairs(getResidues(), i))
                        .flatMap(Collection::stream))
            .collect(Collectors.toCollection(TreeSet::new));

    return atomPairs.stream()
        .map(AtomBasedTorsionAngleType.AtomPair::generateValidationMessage)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toList());
  }

  List<PdbResidue> getResidues();

  PdbResidue findResidue(String chainIdentifier, int residueNumber, String insertionCode);

  PdbResidue findResidue(PdbResidueIdentifier query);
}
