package pl.poznan.put.pdb.analysis;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.rcsb.cif.CifIO;
import org.rcsb.cif.schema.mm.MmCifFileBuilder;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.ImmutablePdbAtomLine;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.rna.NucleotideTorsionAngle;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.torsion.AtomPair;

/** A collection of residues. */
@FunctionalInterface
public interface ResidueCollection extends Serializable {
  /**
   * @return The list of residues.
   */
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

  /**
   * @return A list of residue identifiers.
   */
  default List<PdbResidueIdentifier> residueIdentifiers() {
    return residues().stream().map(PdbResidueIdentifier::from).collect(Collectors.toList());
  }

  /**
   * @return A list of named residue identifiers.
   */
  default List<PdbNamedResidueIdentifier> namedResidueIdentifiers() {
    return residues().stream().map(PdbResidue::namedResidueIdentifier).collect(Collectors.toList());
  }

  /**
   * Generates a list of ATOM lines in PDB format from this instance.
   *
   * @return A representation of this residue collection in PDB format.
   */
  default String toPdb() {
    return new PdbBuilder().add(this, "").build();
  }

  /**
   * Generates a list of ATOM lines in mmCIF format from this instance.
   *
   * @return A representation of this residue collection in mmCIF format.
   */
  default String toCif() throws IOException {
    return toCif("");
  }

  /**
   * Generates a list of ATOM lines in mmCIF format from this instance.
   *
   * @param name A name of the data block in the mmCIF file.
   * @return A representation of this residue collection in mmCIF format.
   */
  default String toCif(final String name) throws IOException {
    return new CifBuilder().add(this, name).build();
  }

  final class PdbBuilder {
    final StringBuilder stringBuilder = new StringBuilder();

    public PdbBuilder add(final ResidueCollection residueCollection, final String name) {
      if (StringUtils.isNotBlank(name)) {
        stringBuilder.append(name).append('\n');
      }
      for (final PdbResidue residue : residueCollection.residues()) {
        stringBuilder.append(residue.toPdb()).append('\n');
      }
      return this;
    }

    public String build() {
      return stringBuilder.toString();
    }
  }

  final class CifBuilder {
    final MmCifFileBuilder fileBuilder = new MmCifFileBuilder();

    public CifBuilder add(final ResidueCollection residueCollection, final String name) {
      final var atomSiteBuilder = fileBuilder.enterBlock(name).enterAtomSite();
      final var groupPDB = atomSiteBuilder.enterGroupPDB();
      final var id = atomSiteBuilder.enterId();
      final var typeSymbol = atomSiteBuilder.enterTypeSymbol();
      final var labelAtomId = atomSiteBuilder.enterLabelAtomId();
      final var labelAltId = atomSiteBuilder.enterLabelAltId();
      final var labelCompId = atomSiteBuilder.enterLabelCompId();
      final var labelAsymId = atomSiteBuilder.enterLabelAsymId();
      final var labelEntityId = atomSiteBuilder.enterLabelEntityId();
      final var labelSeqId = atomSiteBuilder.enterLabelSeqId();
      final var pdbxPDBInsCode = atomSiteBuilder.enterPdbxPDBInsCode();
      final var cartnX = atomSiteBuilder.enterCartnX();
      final var cartnY = atomSiteBuilder.enterCartnY();
      final var cartnZ = atomSiteBuilder.enterCartnZ();
      final var occupancy = atomSiteBuilder.enterOccupancy();
      final var bIsoOrEquiv = atomSiteBuilder.enterBIsoOrEquiv();
      final var pdbxFormalCharge = atomSiteBuilder.enterPdbxFormalCharge();
      final var authSeqId = atomSiteBuilder.enterAuthSeqId();
      final var authCompId = atomSiteBuilder.enterAuthCompId();
      final var authAsymId = atomSiteBuilder.enterAuthAsymId();
      final var authAtomId = atomSiteBuilder.enterAuthAtomId();
      final var pdbxPDBModelNum = atomSiteBuilder.enterPdbxPDBModelNum();

      for (int i = 0; i < residueCollection.residues().size(); i++) {
        final PdbResidue residue = residueCollection.residues().get(i);

        for (final PdbAtomLine atom : residue.atoms()) {
          groupPDB.add(residue.isModified() ? "HETATM" : "ATOM");
          id.add(atom.serialNumber()).leaveColumn();
          typeSymbol.add(atom.elementSymbol());
          labelAtomId.add(atom.atomName());
          labelAltId.markNextNotPresent();
          labelCompId.add(atom.residueName());
          labelAsymId.add(atom.chainIdentifier());
          labelEntityId.markNextUnknown();
          labelSeqId.add(i + 1);

          if (atom.insertionCode().isPresent()) {
            pdbxPDBInsCode.add(atom.insertionCode().get());
          } else {
            pdbxPDBInsCode.markNextNotPresent();
          }

          cartnX.add(atom.x());
          cartnY.add(atom.y());
          cartnZ.add(atom.z());
          occupancy.add(atom.occupancy());
          bIsoOrEquiv.add(atom.temperatureFactor());

          if (StringUtils.isBlank(atom.charge())) {
            pdbxFormalCharge.markNextUnknown();
          } else {
            try {
              pdbxFormalCharge.add(Integer.parseInt(atom.charge()));
            } catch (NumberFormatException e) {
              pdbxFormalCharge.markNextUnknown();
            }
          }

          authSeqId.add(atom.residueNumber());
          authCompId.add(atom.residueName());
          authAsymId.add(atom.chainIdentifier());
          authAtomId.add(atom.atomName());
          pdbxPDBModelNum.markNextUnknown();
        }
      }

      groupPDB.leaveColumn();
      id.leaveColumn();
      typeSymbol.leaveColumn();
      labelAtomId.leaveColumn();
      labelAltId.leaveColumn();
      labelCompId.leaveColumn();
      labelAsymId.leaveColumn();
      labelEntityId.leaveColumn();
      labelSeqId.leaveColumn();
      pdbxPDBInsCode.leaveColumn();
      cartnX.leaveColumn();
      cartnY.leaveColumn();
      cartnZ.leaveColumn();
      occupancy.leaveColumn();
      bIsoOrEquiv.leaveColumn();
      pdbxFormalCharge.leaveColumn();
      authSeqId.leaveColumn();
      authCompId.leaveColumn();
      authAsymId.leaveColumn();
      authAtomId.leaveColumn();
      pdbxPDBModelNum.leaveColumn();
      atomSiteBuilder.leaveCategory().leaveBlock();
      return this;
    }

    public String build() throws IOException {
      final var mmCifFile = fileBuilder.leaveFile();
      final var bytes = CifIO.writeText(mmCifFile);
      return new String(bytes, StandardCharsets.UTF_8);
    }
  }
}
