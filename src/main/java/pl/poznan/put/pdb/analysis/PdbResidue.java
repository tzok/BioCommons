package pl.poznan.put.pdb.analysis;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.ResidueNumber;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.rna.Base;
import pl.poznan.put.rna.NucleicAcidResidueComponent;
import pl.poznan.put.rna.Purine;
import pl.poznan.put.rna.Pyrimidine;
import pl.poznan.put.rna.RNAResidueComponentType;
import pl.poznan.put.rna.base.NucleobaseType;
import pl.poznan.put.torsion.TorsionAngleType;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// atomNames, identifier, residueName, modifiedResidueName, atoms, isModified, isMissing);
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Slf4j
public class PdbResidue implements Serializable, Comparable<PdbResidue>, ChainNumberICode {
  @EqualsAndHashCode.Include private final List<AtomName> atomNames;
  @EqualsAndHashCode.Include private final PdbResidueIdentifier identifier;
  @EqualsAndHashCode.Include private final String residueName;
  @EqualsAndHashCode.Include private final String modifiedResidueName;
  @EqualsAndHashCode.Include private final List<PdbAtomLine> atoms;
  @EqualsAndHashCode.Include private final boolean isModified;
  @EqualsAndHashCode.Include private final boolean isMissing;

  private final ResidueInformationProvider residueInformationProvider;

  public PdbResidue(
      final PdbResidueIdentifier identifier,
      final String residueName,
      final List<PdbAtomLine> atoms,
      final boolean isMissing) {
    this(identifier, residueName, residueName, atoms, false, isMissing);
  }

  public PdbResidue(
      final PdbResidueIdentifier identifier,
      final String residueName,
      final String modifiedResidueName,
      final List<PdbAtomLine> atoms,
      final boolean isModified,
      final boolean isMissing) {
    super();
    this.identifier = identifier;
    this.residueName = residueName;
    this.modifiedResidueName = modifiedResidueName;
    this.atoms = new ArrayList<>(atoms);
    this.isMissing = isMissing;
    atomNames = detectAtomNames();

    if (isMissing) {
      residueInformationProvider =
          ResidueTypeDetector.detectResidueTypeFromResidueName(residueName);
      this.isModified = false;
    } else {
      residueInformationProvider =
          ResidueTypeDetector.detectResidueType(modifiedResidueName, atomNames);
      this.isModified = isModified || (wasSuccessfullyDetected() && !hasAllAtoms());
    }

    final char oneLetterName = residueInformationProvider.getOneLetterName();
    this.identifier.setResidueOneLetterName(
        this.isModified ? Character.toLowerCase(oneLetterName) : oneLetterName);
  }

  public static PdbResidue fromBioJavaGroup(final Group group) {
    final ResidueNumber residueNumberObject = group.getResidueNumber();
    final String chainIdentifier = residueNumberObject.getChainName();
    final int residueNumber = residueNumberObject.getSeqNum();
    final String insertionCode =
        (residueNumberObject.getInsCode() == null)
            ? " "
            : Character.toString(residueNumberObject.getInsCode());
    final PdbResidueIdentifier residueIdentifier =
        new PdbResidueIdentifier(chainIdentifier, residueNumber, insertionCode);
    final List<PdbAtomLine> atoms =
        group.getAtoms().stream().map(PdbAtomLine::fromBioJavaAtom).collect(Collectors.toList());

    final String residueName = group.getPDBName();
    return new PdbResidue(residueIdentifier, residueName, atoms, false);
  }

  private List<AtomName> detectAtomNames() {
    return atoms.stream().map(PdbAtomLine::detectAtomName).collect(Collectors.toList());
  }

  public final boolean wasSuccessfullyDetected() {
    return !(residueInformationProvider instanceof InvalidResidueInformationProvider);
  }

  public final boolean hasAllAtoms() {
    final Collection<AtomName> expected = new ArrayList<>();
    final Collection<AtomName> additional = new ArrayList<>();

    for (final ResidueComponent component : residueInformationProvider.getAllMoleculeComponents()) {
      expected.addAll(component.getAtoms());
      additional.addAll(component.getAdditionalAtoms());
    }

    final Predicate<AtomName> isHeavyAtomPredicate = PredicateUtils.invokerPredicate("isHeavy");
    final List<AtomName> actual = new ArrayList<>(atomNames);
    CollectionUtils.filter(actual, isHeavyAtomPredicate);
    CollectionUtils.filter(expected, isHeavyAtomPredicate);
    final boolean result = CollectionUtils.isEqualCollection(actual, expected);

    if (!result) {
      final Collection<AtomName> intersection = new ArrayList<>(actual);
      intersection.retainAll(expected);
      actual.removeAll(intersection);
      actual.removeAll(additional);
      expected.removeAll(intersection);

      if (!actual.isEmpty()) {
        PdbResidue.log.debug(
            "Residue {} ({}) contains additional atoms: {}",
            this,
            getDetectedResidueName(),
            Arrays.toString(actual.toArray(new AtomName[0])));
      }
      if (!expected.isEmpty()) {
        PdbResidue.log.debug(
            "Residue {} ({}) has missing atoms: {}",
            this,
            getDetectedResidueName(),
            Arrays.toString(expected.toArray(new AtomName[0])));
      }
    }

    return result;
  }

  public final String getDetectedResidueName() {
    return residueInformationProvider.getDefaultPdbName();
  }

  public final List<PdbAtomLine> getAtoms() {
    return Collections.unmodifiableList(atoms);
  }

  @Override
  public final String getChainIdentifier() {
    return identifier.getChainIdentifier();
  }

  @Override
  public final int getResidueNumber() {
    return identifier.getResidueNumber();
  }

  @Override
  public final String getInsertionCode() {
    return identifier.getInsertionCode();
  }

  @Override
  public final PdbResidueIdentifier getResidueIdentifier() {
    return identifier;
  }

  public final String getOriginalResidueName() {
    return residueName;
  }

  public final String getModifiedResidueName() {
    return modifiedResidueName;
  }

  public final List<TorsionAngleType> getTorsionAngleTypes() {
    return residueInformationProvider.getTorsionAngleTypes();
  }

  public final char getOneLetterName() {
    return identifier.getResidueOneLetterName();
  }

  public final boolean isModified() {
    return isModified;
  }

  public final boolean isMissing() {
    return isMissing;
  }

  public final MoleculeType getMoleculeType() {
    return residueInformationProvider.getMoleculeType();
  }

  @Override
  public final String toString() {
    final String chainIdentifier = identifier.getChainIdentifier();
    final int residueNumber = identifier.getResidueNumber();
    final String insertionCode = identifier.getInsertionCode();
    return chainIdentifier
        + '.'
        + modifiedResidueName
        + residueNumber
        + (Objects.equals(" ", insertionCode) ? "" : insertionCode);
  }

  @Override
  public final int compareTo(@Nonnull final PdbResidue t) {
    return identifier.compareTo(t.identifier);
  }

  public final boolean hasAtom(final AtomName atomName) {
    return atomNames.contains(atomName);
  }

  public final boolean hasHydrogen() {
    final Predicate<AtomName> notIsHeavyPredicate =
        PredicateUtils.notPredicate(PredicateUtils.invokerPredicate("isHeavy"));
    return IterableUtils.matchesAny(atomNames, notIsHeavyPredicate);
  }

  public final PdbAtomLine findAtom(final AtomName atomName) {
    for (final PdbAtomLine atom : atoms) {
      if (atom.detectAtomName() == atomName) {
        return atom;
      }
    }

    throw new IllegalArgumentException("Failed to find: " + atomName);
  }

  public final boolean isConnectedTo(final PdbResidue other) {
    final MoleculeType moleculeType = residueInformationProvider.getMoleculeType();
    return moleculeType.areConnected(this, other);
  }

  public final int findConnectedResidueIndex(final List<? extends PdbResidue> candidates) {
    final MoleculeType moleculeType = residueInformationProvider.getMoleculeType();
    for (int i = 0; i < candidates.size(); i++) {
      final PdbResidue candidate = candidates.get(i);
      if (moleculeType.areConnected(this, candidate)) {
        return i;
      }
    }
    return -1;
  }

  public final String toPdb() {
    return atoms.stream().map(atom -> String.valueOf(atom) + '\n').collect(Collectors.joining());
  }

  public final String toCif() {
    return atoms.stream()
        .map(atom -> atom.toCif() + '\n')
        .collect(Collectors.joining("", PdbAtomLine.CIF_LOOP + '\n', ""));
  }

  public final ResidueInformationProvider getResidueInformationProvider() {
    return residueInformationProvider;
  }

  public final List<PdbAtomLine> getComponentAtoms(final RNAResidueComponentType type) {
    return residueInformationProvider.getAllMoleculeComponents().stream()
        .filter(
            component ->
                (component instanceof NucleicAcidResidueComponent)
                    && (((NucleicAcidResidueComponent) component).getType() == type))
        .findFirst()
        .map(
            component ->
                component.getAtoms().stream()
                    .filter(this::hasAtom)
                    .map(this::findAtom)
                    .collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }

  public final Plane getNucleobasePlane() {
    if (residueInformationProvider instanceof NucleobaseType) {
      final Base base = ((NucleobaseType) residueInformationProvider).getBaseInstance();

      if (base instanceof Purine) {
        final Vector3D p1 = findAtom(AtomName.N9).toVector3D();
        final Vector3D p2 = findAtom(AtomName.C2).toVector3D();
        final Vector3D p3 = findAtom(AtomName.C6).toVector3D();
        return new Plane(p1, p2, p3, 1.0e-3);
      } else if (base instanceof Pyrimidine) {
        final Vector3D p1 = findAtom(AtomName.N1).toVector3D();
        final Vector3D p2 = findAtom(AtomName.N3).toVector3D();
        final Vector3D p3 = findAtom(AtomName.C5).toVector3D();
        return new Plane(p1, p2, p3, 1.0e-3);
      }
    }

    throw new IllegalArgumentException(
        "Cannot compute base plane for not a nucleotide: " + identifier);
  }
}
