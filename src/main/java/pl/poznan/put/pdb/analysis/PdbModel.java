package pl.poznan.put.pdb.analysis;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.*;

import java.io.Serializable;
import java.util.*;
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
  private final Map<PdbResidueIdentifier, PdbModresLine> identifierToModification = new HashMap<>();
  private final Map<PdbResidueIdentifier, PdbResidue> identifierToResidue = new HashMap<>();
  private final Map<PdbResidueIdentifier, PdbChain> identifierToChain = new HashMap<>();
  @EqualsAndHashCode.Include private final List<PdbAtomLine> atoms;
  private final List<PdbRemark465Line> missingResidues;

  public PdbModel(final List<PdbAtomLine> atoms) {
    this(
        PdbHeaderLine.emptyInstance(),
        PdbExpdtaLine.emptyInstance(),
        PdbRemark2Line.emptyInstance(),
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
      missingResiduesIdentifiers.add(missing.getResidueIdentifier());
    }
    for (final PdbModresLine modified : modifiedResidues) {
      identifierToModification.put(modified.getResidueIdentifier(), modified);
    }

    analyzeResidues();
    analyzeChains();

    for (final PdbResidue residue : residues) {
      identifierToResidue.put(residue.getResidueIdentifier(), residue);
    }
  }

  private void analyzeResidues() {
    assert !atoms.isEmpty();

    List<PdbAtomLine> residueAtoms = new ArrayList<>();
    PdbResidueIdentifier lastResidueIdentifier =
        PdbResidueIdentifier.fromChainNumberICode(atoms.get(0));

    for (final PdbAtomLine atom : atoms) {
      final PdbResidueIdentifier residueIdentifier =
          PdbResidueIdentifier.fromChainNumberICode(atom);

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
          new PdbResidue(
              PdbResidueIdentifier.fromChainNumberICode(missingResidue),
              missingResidue.getResidueName(),
              emptyAtomList,
              true);

      final String chain = residue.getChainIdentifier();
      boolean isChainFound = false;
      int i = 0;

      while (i < residues.size()) {
        final PdbResidue existing = residues.get(i);
        final String existingChain = existing.getChainIdentifier();

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
    String lastChainIdentifier = residues.get(0).getChainIdentifier();
    boolean chainTerminated = false;

    for (final PdbResidue residue : notMissingResidues) {
      final String chainIdentifier = residue.getChainIdentifier();

      if (!chainIdentifier.equals(lastChainIdentifier) || chainTerminated) {
        addMissingResiduesAndSaveChain(chainResidues, lastChainIdentifier, missingResidues);
        chainResidues = new ArrayList<>();
        lastChainIdentifier = chainIdentifier;
      }

      chainResidues.add(residue);
      chainTerminated = residue.getAtoms().stream().anyMatch(chainTerminatedAfter::contains);
    }

    addMissingResiduesAndSaveChain(chainResidues, lastChainIdentifier, missingResidues);

    for (final PdbChain chain : chains) {
      for (final PdbResidue residue : chain.getResidues()) {
        identifierToChain.put(residue.getResidueIdentifier(), chain);
      }
    }
  }

  private void addMissingResiduesAndSaveChain(
      List<PdbResidue> chainResidues, String chainIdentifier, List<PdbResidue> missingResidues) {
    missingResidues.stream()
        .filter(pdbResidue -> chainIdentifier.equals(pdbResidue.getChainIdentifier()))
        .forEach(pdbResidue -> putMissingResidueInRightPlace(chainResidues, pdbResidue));
    missingResidues.removeIf(pdbResidue -> chainIdentifier.equals(pdbResidue.getChainIdentifier()));
    chains.add(new PdbChain(chainIdentifier, chainResidues));
  }

  private void putMissingResidueInRightPlace(
      List<PdbResidue> chainResidues, PdbResidue missingResidue) {
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

    final String residueName = residueAtoms.get(0).getResidueName();
    String modifiedResidueName = residueName;
    final boolean isModified = isModified(residueIdentifier);

    if (isModified) {
      modifiedResidueName = getModifiedResidueName(residueIdentifier);
    }

    final PdbResidue residue =
        new PdbResidue(
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

    return identifierToModification.get(residueIdentifier).getStandardResidueName();
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
  public final List<PdbResidue> getResidues() {
    return Collections.unmodifiableList(residues);
  }

  @Override
  public final PdbResidue findResidue(
      final String chainIdentifier, final int residueNumber, final String insertionCode) {
    return findResidue(new PdbResidueIdentifier(chainIdentifier, residueNumber, insertionCode));
  }

  @Override
  public final PdbResidue findResidue(final PdbResidueIdentifier query) {
    if (!identifierToResidue.containsKey(query)) {
      throw new IllegalArgumentException("Failed to find residue: " + query);
    }

    return identifierToResidue.get(query);
  }

  /**
   * Return an index of a residue in this structure. Useful to translate residue identifiers to
   * unique integer indices.
   *
   * @param query A {@link PdbResidueIdentifier} identifying a single residue.
   * @return An index from 0 to the total number of residues in the structure.
   */
  public final int indexOfResidue(final PdbResidueIdentifier query) {
    if (!identifierToResidue.containsKey(query)) {
      throw new IllegalArgumentException("Failed to find residue: " + query);
    }
    return residues.indexOf(identifierToResidue.get(query));
  }

  public final List<PdbChain> getChains() {
    return Collections.unmodifiableList(chains);
  }

  public final String getIdCode() {
    return headerLine.getIdCode();
  }

  public final PdbChain findChainContainingResidue(final PdbResidueIdentifier residueIdentifier) {
    return identifierToChain.get(residueIdentifier);
  }

  public final String getSequence() {
    return residues.stream()
        .map(residue -> String.valueOf(residue.getOneLetterName()))
        .collect(Collectors.joining());
  }

  public final String toPdbString() {
    final Collection<Pair<PdbResidueIdentifier, AtomName>> resolved = new HashSet<>();
    final StringBuilder builder = new StringBuilder();

    for (final PdbResidue residue : residues) {
      for (final PdbAtomLine atom : residue.getAtoms()) {
        final Pair<PdbResidueIdentifier, AtomName> pair =
            Pair.of(residue.getResidueIdentifier(), atom.detectAtomName());

        if (!resolved.contains(pair)) {
          builder.append(atom.replaceAlternateLocation(" ")).append('\n');
          resolved.add(pair);
        }
      }
    }

    return builder.toString();
  }

  public final String toCif() {
    final StringBuilder builder = new StringBuilder();
    builder.append("data_").append(getIdCode()).append('\n');
    builder.append(PdbAtomLine.CIF_LOOP).append('\n');

    for (final PdbResidue residue : residues) {
      builder.append(residue.toCif());
    }

    return builder.toString();
  }

  public final boolean containsAny(final MoleculeType moleculeType) {
    return chains.stream().anyMatch(chain -> chain.getMoleculeType() == moleculeType);
  }

  public PdbModel filteredNewInstance(final MoleculeType moleculeType) {
    final List<PdbAtomLine> filteredAtoms = filterAtoms(moleculeType);
    final List<PdbRemark465Line> filteredMissing = filterMissing(moleculeType);
    return new PdbModel(
        headerLine,
        experimentalDataLine,
        resolutionLine,
        modelNumber,
        filteredAtoms,
        modifiedResidues,
        filteredMissing,
        title,
        chainTerminatedAfter);
  }

  final List<PdbAtomLine> filterAtoms(final MoleculeType moleculeType) {
    final List<PdbAtomLine> filteredAtoms = new ArrayList<>();

    for (final PdbResidue residue : residues) {
      if ((residue.getMoleculeType() == moleculeType) && !residue.isMissing()) {
        filteredAtoms.addAll(residue.getAtoms());
      }
    }

    return filteredAtoms;
  }

  final List<PdbRemark465Line> filterMissing(final MoleculeType moleculeType) {
    final List<PdbRemark465Line> filteredMissing = new ArrayList<>();

    for (final PdbResidue residue : residues) {
      if ((residue.getMoleculeType() == moleculeType) && residue.isMissing()) {
        final String residueName = residue.getOriginalResidueName();
        final String chainIdentifier = residue.getChainIdentifier();
        final int residueNumber = residue.getResidueNumber();
        final String insertionCode = residue.getInsertionCode();
        filteredMissing.add(
            new PdbRemark465Line(
                modelNumber, residueName, chainIdentifier, residueNumber, insertionCode));
      }
    }

    return filteredMissing;
  }

  public final List<PdbResidueIdentifier> residueIdentifiers() {
    return residues.stream().map(PdbResidue::getResidueIdentifier).collect(Collectors.toList());
  }
}
