package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.SetUtils;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.AminoAcid;
import pl.poznan.put.protein.ImmutableBackbone;
import pl.poznan.put.rna.ImmutableRibose;
import pl.poznan.put.rna.Nucleotide;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** A detector of residue type based on its name and atom content. */
public final class ResidueTypeDetector {
  private static final Set<AtomName> RIBOSE_HEAVY_ATOMS =
      ImmutableRibose.of().requiredAtoms().stream()
          .filter(AtomName::isHeavy)
          .collect(Collectors.toSet());
  private static final Set<AtomName> BACKBONE_HEAVY_ATOMS =
      ImmutableBackbone.of().requiredAtoms().stream()
          .filter(AtomName::isHeavy)
          .collect(Collectors.toSet());

  private ResidueTypeDetector() {
    super();
  }

  /**
   * Detects the type of residue by its name or atom content. Works by checking if there is a ribose
   * or protein backbone among the atoms. Then it finds the most similar nucleobase or protein
   * sidechain respectively.
   *
   * @param residueName The name of the residue.
   * @param atomNames The names of atoms in the residue.
   * @return An instance of class with all details about the residue type.
   */
  public static ResidueInformationProvider detectResidueType(
      final String residueName, final Set<AtomName> atomNames) {
    final ResidueInformationProvider provider =
        ResidueTypeDetector.detectResidueTypeFromResidueName(residueName);
    if (provider.moleculeType() != MoleculeType.UNKNOWN) {
      return provider;
    }
    return ResidueTypeDetector.detectResidueTypeFromAtoms(atomNames, residueName);
  }

  private static ResidueInformationProvider detectResidueTypeFromResidueName(
      final String residueName) {
    final Stream<ResidueInformationProvider> stream =
        Stream.concat(Arrays.stream(Nucleotide.values()), Arrays.stream(AminoAcid.values()));
    return stream
        .filter(provider -> provider.aliases().contains(residueName))
        .findFirst()
        .orElse(ImmutableInvalidResidueInformationProvider.of(residueName));
  }

  private static ResidueInformationProvider detectResidueTypeFromAtoms(
      final Set<AtomName> actual, final String residueName) {
    if (actual.size() > 1) {
      if (ResidueTypeDetector.isNucleotide(actual)) {
        return Arrays.stream(Nucleotide.values())
            .max(
                Comparator.comparingDouble(
                    nucleotide ->
                        ResidueTypeDetector.intersectionRatio(
                            actual, nucleotide.nucleobase().requiredAtoms())))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Failed to match any nucleobase to provided atom names"));
      }

      if (ResidueTypeDetector.isAminoAcid(actual)) {
        return Arrays.stream(AminoAcid.values())
            .max(
                Comparator.comparingDouble(
                    aminoAcid ->
                        ResidueTypeDetector.intersectionRatio(
                            actual, aminoAcid.sidechain().requiredAtoms())))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Failed to match any sidechain to provided atom names"));
      }
    }
    return ImmutableInvalidResidueInformationProvider.of(residueName);
  }

  private static boolean isNucleotide(final Set<AtomName> actual) {
    return ResidueTypeDetector.intersectionRatio(actual, ResidueTypeDetector.RIBOSE_HEAVY_ATOMS)
        >= 0.5;
  }

  private static double intersectionRatio(
      final Set<AtomName> actual, final Set<AtomName> expected) {
    return (double) SetUtils.intersection(actual, expected).size() / expected.size();
  }

  private static boolean isAminoAcid(final Set<AtomName> actual) {
    return ResidueTypeDetector.intersectionRatio(actual, ResidueTypeDetector.BACKBONE_HEAVY_ATOMS)
        >= 0.5;
  }
}
