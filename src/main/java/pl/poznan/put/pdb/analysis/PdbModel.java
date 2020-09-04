package pl.poznan.put.pdb.analysis;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.CifConstants;
import pl.poznan.put.pdb.ImmutablePdbExpdtaLine;
import pl.poznan.put.pdb.ImmutablePdbHeaderLine;
import pl.poznan.put.pdb.ImmutablePdbRemark2Line;
import pl.poznan.put.pdb.ImmutablePdbRemark465Line;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbResidueIdentifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PdbModel implements Serializable, ResidueCollection {
  @Getter protected final String title;
  final PdbHeaderLine headerLine;
  final PdbExpdtaLine experimentalDataLine;
  final PdbRemark2Line resolutionLine;
  final int modelNumber;
  final List<PdbModresLine> modifiedResidues;
  final List<PdbAtomLine> chainTerminatedAfter;

  private final List<PdbChain> chains = new ArrayList<>();
  private final List<PdbResidue> residues = new ArrayList<>();
  private final Collection<PdbResidueIdentifier> missingResiduesIdentifiers = new HashSet<>();
  private final Map<ChainNumberICode, PdbModresLine> identifierToModification = new HashMap<>();
  private final Map<ChainNumberICode, PdbResidue> identifierToResidue = new HashMap<>();
  private final Map<ChainNumberICode, PdbChain> identifierToChain = new HashMap<>();
  @EqualsAndHashCode.Include private final List<PdbAtomLine> atoms;
  private final List<PdbRemark465Line> missingResidues;

  public PdbModel(final List<PdbAtomLine> atoms) {
    this(
        ImmutablePdbHeaderLine.of("", new Date(0L), ""),
        ImmutablePdbExpdtaLine.of(Collections.emptyList()),
        ImmutablePdbRemark2Line.of(Double.NaN),
        1,
        atoms,
        Collections.emptyList(),
        Collections.emptyList(),
        "",
        Collections.emptyList());
  }

  public PdbModel(
      final PdbHeaderLine headerLine,
      final PdbExpdtaLine experimentalDataLine,
      final PdbRemark2Line resolutionLine,
      final int modelNumber,
      final List<PdbAtomLine> atoms,
      final List<PdbModresLine> modifiedResidues,
      final List<PdbRemark465Line> missingResidues,
      final String title,
      final List<PdbAtomLine> chainTerminatedAfter) {
    super();
    this.title = title;
    this.headerLine = headerLine;
    this.experimentalDataLine = experimentalDataLine;
    this.resolutionLine = resolutionLine;
    this.modelNumber = modelNumber;
    this.atoms = new ArrayList<>(atoms);
    this.modifiedResidues = new ArrayList<>(modifiedResidues);
    this.missingResidues = new ArrayList<>(missingResidues);
    this.chainTerminatedAfter = new ArrayList<>(chainTerminatedAfter);

    for (final PdbRemark465Line missing : missingResidues) {
      missingResiduesIdentifiers.add(missing.toResidueIdentifer());
    }
    for (final PdbModresLine modified : modifiedResidues) {
      identifierToModification.put(modified.toResidueIdentifer(), modified);
    }

    analyzeResidues();
    analyzeChains();

    for (final PdbResidue residue : residues) {
      identifierToResidue.put(residue.toResidueIdentifer(), residue);
    }
  }

  private void analyzeResidues() {
    assert !atoms.isEmpty();

    List<PdbAtomLine> residueAtoms = new ArrayList<>();
    PdbResidueIdentifier lastResidueIdentifier = atoms.get(0).toResidueIdentifer();

    for (final PdbAtomLine atom : atoms) {
      final PdbResidueIdentifier residueIdentifier = atom.toResidueIdentifer();

      if (!Objects.equals(residueIdentifier, lastResidueIdentifier)) {
        saveExistingResidueIfValid(residueAtoms, lastResidueIdentifier);
        residueAtoms = new ArrayList<>();
        lastResidueIdentifier = residueIdentifier;
      }

      residueAtoms.add(atom);
    }

    saveExistingResidueIfValid(residueAtoms, lastResidueIdentifier);

    for (final PdbRemark465Line missingResidue : missingResidues) {
      final List<PdbAtomLine> emptyAtomList = Collections.emptyList();
      final PdbResidue residue =
          ImmutablePdbResidue.of(
              missingResidue.toResidueIdentifer(),
              missingResidue.residueName(),
              missingResidue.residueName(),
              emptyAtomList,
              false,
              true);

      final String chain = residue.chainIdentifier();
      boolean isChainFound = false;
      int i = 0;

      while (i < residues.size()) {
        final PdbResidue existing = residues.get(i);
        final String existingChain = existing.chainIdentifier();

        if (!isChainFound && Objects.equals(chain, existingChain)) {
          isChainFound = true;
        }

        if (isChainFound
            && ((existing.compareTo(residue) > 0) || !Objects.equals(chain, existingChain))) {
          residues.add(i, residue);
          break;
        }

        i += 1;
      }
    }

    if (residues.isEmpty()) {
      throw new PdbParsingException("Invalid PDB file. Failed to analyze any residue");
    }
  }

  private void analyzeChains() {
    assert !residues.isEmpty();

    final Predicate<PdbResidue> isMissing = PdbResidue::isMissing;
    final List<PdbResidue> missingResidues =
        residues.stream().filter(isMissing).collect(Collectors.toList());
    final List<PdbResidue> notMissingResidues =
        residues.stream().filter(isMissing.negate()).collect(Collectors.toList());

    List<PdbResidue> chainResidues = new ArrayList<>();
    String lastChainIdentifier = residues.get(0).chainIdentifier();
    boolean chainTerminated = false;

    for (final PdbResidue residue : notMissingResidues) {
      final String chainIdentifier = residue.chainIdentifier();

      if (!chainIdentifier.equals(lastChainIdentifier) || chainTerminated) {
        addMissingResiduesAndSaveChain(chainResidues, lastChainIdentifier, missingResidues);
        chainResidues = new ArrayList<>();
        lastChainIdentifier = chainIdentifier;
      }

      chainResidues.add(residue);
      chainTerminated = residue.atoms().stream().anyMatch(chainTerminatedAfter::contains);
    }

    addMissingResiduesAndSaveChain(chainResidues, lastChainIdentifier, missingResidues);

    for (final PdbChain chain : chains) {
      for (final PdbResidue residue : chain.residues()) {
        identifierToChain.put(residue.toResidueIdentifer(), chain);
      }
    }
  }

  private void addMissingResiduesAndSaveChain(
      final List<PdbResidue> chainResidues,
      final String chainIdentifier,
      final List<PdbResidue> missingResidues) {
    missingResidues.stream()
        .filter(pdbResidue -> chainIdentifier.equals(pdbResidue.chainIdentifier()))
        .forEach(pdbResidue -> putMissingResidueInRightPlace(chainResidues, pdbResidue));
    missingResidues.removeIf(pdbResidue -> chainIdentifier.equals(pdbResidue.chainIdentifier()));
    chains.add(ImmutablePdbChain.of(chainIdentifier, chainResidues));
  }

  private void putMissingResidueInRightPlace(
      final List<PdbResidue> chainResidues, final PdbResidue missingResidue) {
    for (int i = 0; i < chainResidues.size(); i++) {
      final PdbResidue chainResidue = chainResidues.get(i);
      if (chainResidue.compareTo(missingResidue) > 0) {
        chainResidues.add(i, missingResidue);
        return;
      }
    }
    chainResidues.add(missingResidue);
  }

  private void saveExistingResidueIfValid(
      final List<PdbAtomLine> residueAtoms, final PdbResidueIdentifier residueIdentifier) {
    assert !isMissing(residueIdentifier);
    assert !residueAtoms.isEmpty();

    final String residueName = residueAtoms.get(0).residueName();
    String modifiedResidueName = residueName;
    final boolean isModified = isModified(residueIdentifier);

    if (isModified) {
      modifiedResidueName = getModifiedResidueName(residueIdentifier);
    }

    final PdbResidue residue =
        ImmutablePdbResidue.of(
            residueIdentifier, residueName, modifiedResidueName, residueAtoms, isModified, false);

    if (residue.wasSuccessfullyDetected()) {
      residues.add(residue);
    }
  }

  public boolean isMissing(final PdbResidueIdentifier residueIdentifier) {
    return missingResiduesIdentifiers.contains(residueIdentifier);
  }

  public boolean isModified(final PdbResidueIdentifier residueIdentifier) {
    return identifierToModification.containsKey(residueIdentifier);
  }

  public String getModifiedResidueName(final PdbResidueIdentifier residueIdentifier) {
    if (!identifierToModification.containsKey(residueIdentifier)) {
      throw new IllegalArgumentException(
          "Failed to find information about modification of: " + residueIdentifier);
    }

    return identifierToModification.get(residueIdentifier).standardResidueName();
  }

  public final PdbHeaderLine getHeaderLine() {
    return headerLine;
  }

  public final PdbExpdtaLine getExperimentalDataLine() {
    return experimentalDataLine;
  }

  public final PdbRemark2Line getResolutionLine() {
    return resolutionLine;
  }

  public final int getModelNumber() {
    return modelNumber;
  }

  public final Iterable<PdbModresLine> getModifiedResidues() {
    return Collections.unmodifiableList(modifiedResidues);
  }

  public final List<PdbRemark465Line> getMissingResidues() {
    return Collections.unmodifiableList(missingResidues);
  }

  public final List<PdbAtomLine> getAtoms() {
    return Collections.unmodifiableList(atoms);
  }

  @Override
  public final List<PdbResidue> residues() {
    return Collections.unmodifiableList(residues);
  }

  /**
   * Return an index of a residue in this structure. Useful to translate residue identifiers to
   * unique integer indices.
   *
   * @param query A {@link PdbResidueIdentifier} identifying a single residue.
   * @return An index from 0 to the total number of residues in the structure.
   */
  public final int indexOfResidue(final ChainNumberICode query) {
    if (!identifierToResidue.containsKey(query)) {
      throw new IllegalArgumentException("Failed to find residue: " + query);
    }
    return residues.indexOf(identifierToResidue.get(query));
  }

  public final List<PdbChain> getChains() {
    return Collections.unmodifiableList(chains);
  }

  public final String getIdCode() {
    return headerLine.idCode();
  }

  public final PdbChain findChainContainingResidue(final ChainNumberICode residueIdentifier) {
    return identifierToChain.get(residueIdentifier);
  }

  public final String getSequence() {
    return residues.stream()
        .map(residue -> String.valueOf(residue.oneLetterName()))
        .collect(Collectors.joining());
  }

  public final String toPdbString() {
    final Collection<Pair<PdbResidueIdentifier, AtomName>> resolved = new HashSet<>();
    final StringBuilder builder = new StringBuilder();

    for (final PdbResidue residue : residues) {
      for (final PdbAtomLine atom : residue.atoms()) {
        final Pair<PdbResidueIdentifier, AtomName> pair =
            Pair.of(residue.toResidueIdentifer(), atom.detectAtomName());

        if (!resolved.contains(pair)) {

          builder.append(atom.withAlternateLocation(" ")).append('\n');
          resolved.add(pair);
        }
      }
    }

    return builder.toString();
  }

  public final String toCif() {
    final StringBuilder builder = new StringBuilder();
    builder.append("data_").append(getIdCode()).append('\n');
    builder.append(CifConstants.CIF_LOOP).append('\n');

    for (final PdbResidue residue : residues) {
      builder.append(residue.toCif());
      builder.append('\n');
    }

    return builder.toString();
  }

  public final boolean containsAny(final MoleculeType moleculeType) {
    return chains.stream().anyMatch(chain -> chain.moleculeType() == moleculeType);
  }

  public PdbModel filteredNewInstance(final MoleculeType moleculeType) {
    return new PdbModel(
        headerLine,
        experimentalDataLine,
        resolutionLine,
        modelNumber,
        filterAtoms(moleculeType),
        modifiedResidues,
        filterMissing(moleculeType),
        title,
        chainTerminatedAfter);
  }

  final List<PdbAtomLine> filterAtoms(final MoleculeType moleculeType) {
    return residues.stream()
        .filter(pdbResidue -> pdbResidue.getMoleculeType() == moleculeType)
        .filter(pdbResidue -> !pdbResidue.isMissing())
        .flatMap(pdbResidue -> pdbResidue.atoms().stream())
        .collect(Collectors.toList());
  }

  final List<PdbRemark465Line> filterMissing(final MoleculeType moleculeType) {
    return residues.stream()
        .filter(pdbResidue -> pdbResidue.getMoleculeType() == moleculeType)
        .filter(PdbResidue::isMissing)
        .map(
            pdbResidue ->
                ImmutablePdbRemark465Line.of(
                    modelNumber,
                    pdbResidue.residueName(),
                    pdbResidue.chainIdentifier(),
                    pdbResidue.residueNumber(),
                    pdbResidue.insertionCode()))
        .collect(Collectors.toList());
  }

  @Value.Lazy
  public final List<PdbResidueIdentifier> residueIdentifiers() {
    return residues.stream().map(PdbResidue::toResidueIdentifer).collect(Collectors.toList());
  }

  @Value.Lazy
  public final List<PdbNamedResidueIdentifier> namedResidueIdentifiers() {
    return residues.stream().map(PdbResidue::namedResidueIdentifer).collect(Collectors.toList());
  }
}
