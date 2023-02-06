package pl.poznan.put.pdb.analysis;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.rcsb.cif.CifIO;
import org.rcsb.cif.model.FloatColumnBuilder;
import org.rcsb.cif.model.IntColumnBuilder;
import org.rcsb.cif.model.StrColumnBuilder;
import org.rcsb.cif.schema.mm.MmCifBlockBuilder;
import org.rcsb.cif.schema.mm.MmCifCategoryBuilder;
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
    return new PdbBuilder().add(this).build();
  }

  /**
   * Generates a list of ATOM lines in mmCIF format from this instance.
   *
   * @return A representation of this residue collection in mmCIF format.
   */
  default String toCif() throws IOException {
    return new CifBuilder().add(this).build();
  }

  /** A builder that can merge many residue collections in one PDB file. */
  final class PdbBuilder {
    final StringBuilder stringBuilder = new StringBuilder();

    public PdbBuilder add(final ResidueCollection residueCollection) {
      return add(residueCollection, "");
    }

    /**
     * Add a new residue collection to the builder
     *
     * @param residueCollection The residue collection to build.
     * @param name Name of the collection, which will be prepended to ATOM lines if not empty.
     * @return This builder instance.
     */
    public PdbBuilder add(final ResidueCollection residueCollection, final String name) {
      if (StringUtils.isNotBlank(name)) {
        stringBuilder.append(name).append('\n');
      }
      for (final PdbResidue residue : residueCollection.residues()) {
        stringBuilder.append(residue.toPdb()).append('\n');
      }
      return this;
    }

    /**
     * @return The string content in PDB format.
     */
    public String build() {
      return stringBuilder.toString();
    }
  }

  /** A builder that can merge many residue collections in one mmCIF file. */
  final class CifBuilder {
    private MmCifBlockBuilder blockBuilder;
    private MmCifCategoryBuilder.EntityBuilder entityBuilder;
    private MmCifCategoryBuilder.AtomSiteBuilder atomSiteBuilder;
    private StrColumnBuilder<
            MmCifCategoryBuilder.EntityBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        entityId;
    private StrColumnBuilder<
            MmCifCategoryBuilder.EntityBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        details;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        groupPDB;
    private IntColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        id;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        typeSymbol;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        labelAtomId;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        labelAltId;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        labelCompId;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        labelAsymId;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        labelEntityId;
    private IntColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        labelSeqId;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        pdbxPDBInsCode;
    private FloatColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        cartnX;
    private FloatColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        cartnY;
    private FloatColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        cartnZ;
    private FloatColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        occupancy;
    private FloatColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        bIsoOrEquiv;
    private IntColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        pdbxFormalCharge;
    private IntColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        authSeqId;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        authCompId;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        authAsymId;
    private StrColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        authAtomId;
    private IntColumnBuilder<
            MmCifCategoryBuilder.AtomSiteBuilder, MmCifBlockBuilder, MmCifFileBuilder>
        pdbxPDBModelNum;

    public CifBuilder() {
      blockBuilder = new MmCifFileBuilder().enterBlock("");
      setupEntity();
      setupAtomSite();
      setupAudit();
      setupCitation();
      setupCitationAuthor();
    }

    private void setupAtomSite() {
      atomSiteBuilder = blockBuilder.enterAtomSite();
      groupPDB = atomSiteBuilder.enterGroupPDB();
      id = atomSiteBuilder.enterId();
      typeSymbol = atomSiteBuilder.enterTypeSymbol();
      labelAtomId = atomSiteBuilder.enterLabelAtomId();
      labelAltId = atomSiteBuilder.enterLabelAltId();
      labelCompId = atomSiteBuilder.enterLabelCompId();
      labelAsymId = atomSiteBuilder.enterLabelAsymId();
      labelEntityId = atomSiteBuilder.enterLabelEntityId();
      labelSeqId = atomSiteBuilder.enterLabelSeqId();
      pdbxPDBInsCode = atomSiteBuilder.enterPdbxPDBInsCode();
      cartnX = atomSiteBuilder.enterCartnX();
      cartnY = atomSiteBuilder.enterCartnY();
      cartnZ = atomSiteBuilder.enterCartnZ();
      occupancy = atomSiteBuilder.enterOccupancy();
      bIsoOrEquiv = atomSiteBuilder.enterBIsoOrEquiv();
      pdbxFormalCharge = atomSiteBuilder.enterPdbxFormalCharge();
      authSeqId = atomSiteBuilder.enterAuthSeqId();
      authCompId = atomSiteBuilder.enterAuthCompId();
      authAsymId = atomSiteBuilder.enterAuthAsymId();
      authAtomId = atomSiteBuilder.enterAuthAtomId();
      pdbxPDBModelNum = atomSiteBuilder.enterPdbxPDBModelNum();
    }

    private void setupEntity() {
      entityBuilder = blockBuilder.enterEntity();
      entityId = entityBuilder.enterId();
      details = entityBuilder.enterDetails();
    }

    public CifBuilder add(final ResidueCollection residueCollection) {
      return add(residueCollection, "", "", 1);
    }

    public CifBuilder add(final ResidueCollection residueCollection, final String name) {
      return add(residueCollection, name, "", 1);
    }

    public CifBuilder add(
        final ResidueCollection residueCollection, final String name, final String description) {
      return add(residueCollection, name, description, 1);
    }

    /**
     * Add a residue collection to be serialized into mmCIF format.
     *
     * @param residueCollection The residue collection to store.
     * @param name The name of the residue collection.
     * @param description An optional description (for example, the dot-bracket representation of
     *     the entity).
     * @return This instance of builder.
     */
    public CifBuilder add(
        final ResidueCollection residueCollection,
        final String name,
        final String description,
        final int modelNumber) {
      if (StringUtils.isNotBlank(name)) {
        entityId.add(name);

        if (StringUtils.isNotBlank(description)) {
          details.add(description);
        } else {
          details.markNextUnknown();
        }
      }

      for (int i = 0; i < residueCollection.residues().size(); i++) {
        final PdbResidue residue = residueCollection.residues().get(i);

        for (final PdbAtomLine atom : residue.atoms()) {
          groupPDB.add(residue.isModified() ? "HETATM" : "ATOM");
          id.add(atom.serialNumber());
          typeSymbol.add(atom.elementSymbol());
          labelAtomId.add(atom.atomName());
          labelAltId.markNextNotPresent();
          labelCompId.add(atom.residueName());
          labelAsymId.add(atom.chainIdentifier());
          if (StringUtils.isNotBlank(name)) {
            labelEntityId.add(name);
          } else {
            labelEntityId.markNextUnknown();
          }
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
          pdbxPDBModelNum.add(modelNumber);
        }
      }

      return this;
    }

    public String build() throws IOException {
      entityId.leaveColumn();
      details.leaveColumn();
      entityBuilder.leaveCategory();

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
      atomSiteBuilder.leaveCategory();

      final var mmCifFile = blockBuilder.leaveBlock().leaveFile();
      blockBuilder = new MmCifFileBuilder().enterBlock("");
      for (final var category : mmCifFile.getFirstBlock().getCategories().values()) {
        blockBuilder.addCategory(category);
      }

      final var bytes = CifIO.writeText(mmCifFile);
      return new String(bytes, StandardCharsets.UTF_8);
    }

    private void setupCitationAuthor() {
      final var citationAuthorBuilder = blockBuilder.enterCitationAuthor();
      citationAuthorBuilder.enterCitationId().add("1").leaveColumn();
      citationAuthorBuilder.enterOrdinal().add(1).leaveColumn();
      citationAuthorBuilder.enterName().add("Zok, T.").leaveColumn();
      citationAuthorBuilder.enterIdentifierORCID().add("0000-0003-4103-9238").leaveColumn();
      citationAuthorBuilder.leaveCategory();
    }

    private void setupCitation() {
      final var citationBuilder = blockBuilder.enterCitation();
      citationBuilder.enterId().add("1").leaveColumn();
      citationBuilder
          .enterTitle()
          .add("BioCommons: a robust Java library for RNA structural bioinformatics")
          .leaveColumn();
      citationBuilder.enterJournalAbbrev().add("Bioinformatics").leaveColumn();
      citationBuilder.enterYear().add(2021).leaveColumn();
      citationBuilder.enterJournalVolume().add("37").leaveColumn();
      citationBuilder.enterJournalIssue().add("17").leaveColumn();
      citationBuilder.enterPageFirst().add("2766").leaveColumn();
      citationBuilder.enterPageLast().add("2767").leaveColumn();
      citationBuilder.enterPdbxDatabaseIdDOI().add("10.1093/bioinformatics/btab069").leaveColumn();
      citationBuilder.enterPdbxDatabaseIdPubMed().add(33532837).leaveColumn();
      citationBuilder.leaveCategory();
    }

    private void setupAudit() {
      final var auditBuilder = blockBuilder.enterAudit();
      auditBuilder.enterRevisionId().add("1").leaveColumn();
      auditBuilder
          .enterCreationDate()
          .add(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
          .leaveColumn();
      auditBuilder.enterCreationMethod().add("BioCommons").leaveColumn();
      auditBuilder.leaveCategory();
    }
  }
}
