package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.SetUtils;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.protein.ProteinBackbone;
import pl.poznan.put.protein.aminoacid.AminoAcidType;
import pl.poznan.put.rna.Ribose;
import pl.poznan.put.rna.base.NucleobaseType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ResidueTypeDetector {
  private static final Set<AtomName> RIBOSE_HEAVY_ATOMS =
      Ribose.getInstance().getAtoms().stream()
          .filter(AtomName::isHeavy)
          .collect(Collectors.toSet());
  private static final Set<AtomName> BACKBONE_HEAVY_ATOMS =
      ProteinBackbone.getInstance().getAtoms().stream()
          .filter(AtomName::isHeavy)
          .collect(Collectors.toSet());

  private ResidueTypeDetector() {
    super();
  }

  public static ResidueInformationProvider detectResidueType(
      final String residueName, final Set<AtomName> atomNames) {
    final ResidueInformationProvider provider =
        ResidueTypeDetector.detectResidueTypeFromResidueName(residueName);
    if (provider.moleculeType() != MoleculeType.UNKNOWN) {
      return provider;
    }
    return ResidueTypeDetector.detectResidueTypeFromAtoms(atomNames, residueName);
  }

  public static ResidueInformationProvider detectResidueTypeFromResidueName(
      final String residueName) {
    final Stream<ResidueInformationProvider> stream =
        Stream.concat(
            Arrays.stream(NucleobaseType.values()), Arrays.stream(AminoAcidType.values()));
    return stream
        .filter(provider -> provider.allPdbNames().contains(residueName))
        .findFirst()
        .orElse(new InvalidResidueInformationProvider(residueName));
  }

  private static ResidueInformationProvider detectResidueTypeFromAtoms(
      final Set<AtomName> actual, final String residueName) {
    if (actual.size() > 1) {
      if (ResidueTypeDetector.isNucleotide(actual)) {
        return Arrays.stream(NucleobaseType.values())
            .map(NucleobaseType::getBaseInstance)
            .max(
                Comparator.comparingDouble(
                    base -> ResidueTypeDetector.intersectionRatio(actual, base.getAtoms())))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Failed to match any nucleobase to provided atom names"));
      }

      if (ResidueTypeDetector.isAminoAcid(actual)) {
        return Arrays.stream(AminoAcidType.values())
            .map(AminoAcidType::getProteinSidechainInstance)
            .max(
                Comparator.comparingDouble(
                    sidechain ->
                        ResidueTypeDetector.intersectionRatio(actual, sidechain.getAtoms())))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Failed to match any sidechain to provided atom names"));
      }
    }
    return new InvalidResidueInformationProvider(residueName);
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
