package pl.poznan.put.pdb.analysis;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;
import pl.poznan.put.pdb.ImmutablePdbExpdtaLine;
import pl.poznan.put.pdb.ImmutablePdbHeaderLine;
import pl.poznan.put.pdb.ImmutablePdbRemark2Line;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbResidueIdentifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class PdbModel implements Serializable, StructureModel {
  public static PdbModel of(final List<PdbAtomLine> atoms) {
    return ImmutablePdbModel.of(
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

  @Value.Check
  protected void check() {
    Validate.notEmpty(atoms());
  }

  @Value.Lazy
  public List<PdbResidue> residues() {
    final List<PdbResidue> residues = new ArrayList<>();
    List<PdbAtomLine> residueAtoms = new ArrayList<>();
    PdbResidueIdentifier lastResidueIdentifier = atoms().get(0).toResidueIdentifer();

    for (final PdbAtomLine atom : atoms()) {
      final PdbResidueIdentifier residueIdentifier = atom.toResidueIdentifer();

      if (!Objects.equals(residueIdentifier, lastResidueIdentifier)) {
        addResidueIfValid(residues, residueAtoms);
        residueAtoms = new ArrayList<>();
        lastResidueIdentifier = residueIdentifier;
      }

      residueAtoms.add(atom);
    }

    addResidueIfValid(residues, residueAtoms);

    for (final PdbRemark465Line missingResidue : missingResidues()) {
      final PdbResidue residue =
          ImmutablePdbResidue.of(
              missingResidue.toResidueIdentifer(),
              missingResidue.residueName(),
              missingResidue.residueName(),
              Collections.emptyList(),
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

    return residues;
  }

  private void addResidueIfValid(
      final Collection<? super PdbResidue> residues,
      final List<? extends PdbAtomLine> residueAtoms) {
    final PdbResidueIdentifier residueIdentifier = residueAtoms.get(0).toResidueIdentifer();
    final boolean isModified = isModified(residueIdentifier);
    final String residueName = residueAtoms.get(0).residueName();
    final String modifiedResidueName =
        isModified ? modificationDetails(residueIdentifier).standardResidueName() : residueName;

    final PdbResidue residue =
        ImmutablePdbResidue.of(
            residueIdentifier, residueName, modifiedResidueName, residueAtoms, isModified, false);

    if (residue.wasSuccessfullyDetected()) {
      residues.add(residue);
    }
  }

  protected final boolean isMissing(final PdbResidueIdentifier residueIdentifier) {
    return missingResidues().stream()
        .anyMatch(missingResidue -> missingResidue.toResidueIdentifer().equals(residueIdentifier));
  }

  protected final boolean isModified(final PdbResidueIdentifier residueIdentifier) {
    return modifiedResidues().stream()
        .anyMatch(
            modifiedResidue -> modifiedResidue.toResidueIdentifer().equals(residueIdentifier));
  }

  protected final PdbModresLine modificationDetails(final PdbResidueIdentifier residueIdentifier) {
    return modifiedResidues().stream()
        .filter(modifiedResidue -> modifiedResidue.toResidueIdentifer().equals(residueIdentifier))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Failed to find information about modification of: " + residueIdentifier));
  }

  @Value.Lazy
  public List<PdbChain> chains() {
    final Predicate<PdbResidue> isMissing = PdbResidue::isMissing;
    final List<PdbResidue> missingResidues =
        residues().stream().filter(isMissing).collect(Collectors.toList());
    final List<PdbResidue> notMissingResidues =
        residues().stream().filter(isMissing.negate()).collect(Collectors.toList());

    List<PdbResidue> chainResidues = new ArrayList<>();
    String lastChainIdentifier = residues().get(0).chainIdentifier();
    boolean chainTerminated = false;

    final List<PdbChain> chains = new ArrayList<>();

    for (final PdbResidue residue : notMissingResidues) {
      final String chainIdentifier = residue.chainIdentifier();

      if (!chainIdentifier.equals(lastChainIdentifier) || chainTerminated) {
        addMissingResiduesAndSaveChain(chains, chainResidues, lastChainIdentifier, missingResidues);
        chainResidues = new ArrayList<>();
        lastChainIdentifier = chainIdentifier;
      }

      chainResidues.add(residue);
      chainTerminated = residue.atoms().stream().anyMatch(chainTerminatedAfter()::contains);
    }

    addMissingResiduesAndSaveChain(chains, chainResidues, lastChainIdentifier, missingResidues);

    return chains;
  }

  private void addMissingResiduesAndSaveChain(
      final Collection<? super PdbChain> chains,
      final List<PdbResidue> chainResidues,
      final String chainIdentifier,
      final Collection<? extends PdbResidue> missingResidues) {
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

  @Override
  @Value.Parameter(order = 1)
  public abstract PdbHeaderLine header();

  @Override
  @Value.Parameter(order = 2)
  public abstract PdbExpdtaLine experimentalData();

  @Override
  @Value.Parameter(order = 3)
  public abstract PdbRemark2Line resolution();

  @Override
  @Value.Parameter(order = 4)
  public abstract int modelNumber();

  @Override
  @Value.Parameter(order = 5)
  public abstract List<PdbAtomLine> atoms();

  @Override
  @Value.Parameter(order = 6)
  public abstract List<PdbModresLine> modifiedResidues();

  @Override
  @Value.Parameter(order = 7)
  public abstract List<PdbRemark465Line> missingResidues();

  @Override
  @Value.Parameter(order = 8)
  public abstract String title();

  @Override
  @Value.Parameter(order = 9)
  public abstract List<PdbAtomLine> chainTerminatedAfter();

  @Override
  public PdbModel filteredNewInstance(final MoleculeType moleculeType) {
    return ImmutablePdbModel.of(
        header(),
        experimentalData(),
        resolution(),
        modelNumber(),
        filteredAtoms(moleculeType),
        modifiedResidues(),
        filteredMissing(moleculeType),
        title(),
        chainTerminatedAfter());
  }
}
