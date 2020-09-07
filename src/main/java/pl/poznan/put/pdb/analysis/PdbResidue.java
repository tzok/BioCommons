package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.ResidueNumber;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.ImmutablePdbResidueIdentifier;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
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
import java.util.Set;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class PdbResidue implements Serializable, Comparable<PdbResidue>, ChainNumberICode {
  private static final Logger LOGGER = LoggerFactory.getLogger(PdbResidue.class);

  public static PdbResidue fromBioJavaGroup(final Group group) {
    final ResidueNumber residueNumberObject = group.getResidueNumber();
    final String chainIdentifier = residueNumberObject.getChainName();
    final int residueNumber = residueNumberObject.getSeqNum();
    final String insertionCode =
        (residueNumberObject.getInsCode() == null)
            ? " "
            : Character.toString(residueNumberObject.getInsCode());
    final PdbResidueIdentifier residueIdentifier =
        ImmutablePdbResidueIdentifier.of(chainIdentifier, residueNumber, insertionCode);
    final List<PdbAtomLine> atoms =
        group.getAtoms().stream().map(PdbAtomLine::fromBioJavaAtom).collect(Collectors.toList());

    final String residueName = group.getPDBName();
    return ImmutablePdbResidue.of(residueIdentifier, residueName, residueName, atoms, false, false);
  }

  @Value.Parameter(order = 1)
  public abstract PdbResidueIdentifier identifier();

  @Value.Parameter(order = 2)
  public abstract String residueName();

  @Value.Parameter(order = 3)
  public abstract String modifiedResidueName();

  @Value.Parameter(order = 4)
  public abstract List<PdbAtomLine> atoms();

  @Value.Parameter(order = 5)
  public abstract boolean isModifiedInPDB();

  @Value.Parameter(order = 6)
  public abstract boolean isMissing();

  @Value.Lazy
  public Set<AtomName> atomNames() {
    return atoms().stream().map(PdbAtomLine::detectAtomName).collect(Collectors.toSet());
  }

  public final boolean wasSuccessfullyDetected() {
    return residueInformationProvider().moleculeType() != MoleculeType.UNKNOWN;
  }

  @Value.Lazy
  public boolean hasAllAtoms() {
    final Collection<AtomName> expected = new ArrayList<>();
    final Collection<AtomName> additional = new ArrayList<>();

    for (final ResidueComponent component : residueInformationProvider().moleculeComponents()) {
      expected.addAll(component.getAtoms());
      additional.addAll(component.getAdditionalAtoms());
    }

    final Predicate<AtomName> isHeavyAtomPredicate = PredicateUtils.invokerPredicate("isHeavy");
    final List<AtomName> actual = new ArrayList<>(atomNames());
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
        PdbResidue.LOGGER.debug(
            "Residue {} ({}) contains additional atoms: {}",
            this,
            getDetectedResidueName(),
            Arrays.toString(actual.toArray(new AtomName[0])));
      }
      if (!expected.isEmpty()) {
        PdbResidue.LOGGER.debug(
            "Residue {} ({}) has missing atoms: {}",
            this,
            getDetectedResidueName(),
            Arrays.toString(expected.toArray(new AtomName[0])));
      }
    }

    return result;
  }

  public final String getDetectedResidueName() {
    return residueInformationProvider().defaultPdbName();
  }

  @Override
  public final String chainIdentifier() {
    return identifier().chainIdentifier();
  }

  @Override
  public final int residueNumber() {
    return identifier().residueNumber();
  }

  @Override
  public final String insertionCode() {
    return identifier().insertionCode();
  }

  @Value.Lazy
  public PdbNamedResidueIdentifier namedResidueIdentifer() {
    final char oneLetterName = residueInformationProvider().oneLetterName();
    return ImmutablePdbNamedResidueIdentifier.of(
        identifier().chainIdentifier(),
        identifier().residueNumber(),
        identifier().insertionCode(),
        isModified() ? Character.toLowerCase(oneLetterName) : oneLetterName);
  }

  public final List<TorsionAngleType> torsionAngleTypes() {
    return residueInformationProvider().torsionAngleTypes();
  }

  public final char oneLetterName() {
    return namedResidueIdentifer().oneLetterName();
  }

  public final boolean isModified() {
    return !isMissing() && (isModifiedInPDB() || isModifiedByAtomContent());
  }

  private boolean isModifiedByAtomContent() {
    return wasSuccessfullyDetected() && !hasAllAtoms();
  }

  public final MoleculeType getMoleculeType() {
    return residueInformationProvider().moleculeType();
  }

  @Override
  public final String toString() {
    final String chainIdentifier = identifier().chainIdentifier();
    final int residueNumber = identifier().residueNumber();
    final String insertionCode = identifier().insertionCode();
    return chainIdentifier
        + '.'
        + modifiedResidueName()
        + residueNumber
        + (Objects.equals(" ", insertionCode) ? "" : insertionCode);
  }

  @Override
  public final int compareTo(@Nonnull final PdbResidue t) {
    return identifier().compareTo(t.identifier());
  }

  public final boolean hasAtom(final AtomName atomName) {
    return atomNames().contains(atomName);
  }

  @Value.Lazy
  public boolean hasHydrogen() {
    return atomNames().stream().anyMatch(atomName -> !atomName.isHeavy());
  }

  public final PdbAtomLine findAtom(final AtomName atomName) {
    for (final PdbAtomLine atom : atoms()) {
      if (atom.detectAtomName() == atomName) {
        return atom;
      }
    }

    throw new IllegalArgumentException("Failed to find: " + atomName);
  }

  public final boolean isConnectedTo(final PdbResidue other) {
    final MoleculeType moleculeType = residueInformationProvider().moleculeType();
    return moleculeType.areConnected(this, other);
  }

  public final int findConnectedResidueIndex(final List<? extends PdbResidue> candidates) {
    final MoleculeType moleculeType = residueInformationProvider().moleculeType();
    for (int i = 0; i < candidates.size(); i++) {
      final PdbResidue candidate = candidates.get(i);
      if (moleculeType.areConnected(this, candidate)) {
        return i;
      }
    }
    return -1;
  }

  public final String toPdb() {
    return atoms().stream().map(String::valueOf).collect(Collectors.joining("\n"));
  }

  public final String toCif() {
    return atoms().stream().map(PdbAtomLine::toCif).collect(Collectors.joining("\n"));
  }

  @Value.Lazy
  public ResidueInformationProvider residueInformationProvider() {
    if (isMissing()) {
      return ResidueTypeDetector.detectResidueTypeFromResidueName(residueName());
    }
    return ResidueTypeDetector.detectResidueType(modifiedResidueName(), atomNames());
  }

  public final List<PdbAtomLine> getComponentAtoms(final RNAResidueComponentType type) {
    return residueInformationProvider().moleculeComponents().stream()
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

  @Value.Lazy
  public Plane getNucleobasePlane() {
    if (residueInformationProvider() instanceof NucleobaseType) {
      final Base base = ((NucleobaseType) residueInformationProvider()).getBaseInstance();

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
        "Cannot compute base plane for not a nucleotide: " + identifier());
  }
}
