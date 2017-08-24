package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.ResidueNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.torsion.TorsionAngleType;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PdbResidue
        implements Serializable, Comparable<PdbResidue>, ChainNumberICode {
    private static final long serialVersionUID = 8274994774089305365L;
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PdbResidue.class);

    private final List<AtomName> atomNames;
    private final ResidueInformationProvider residueInformationProvider;
    private final PdbResidueIdentifier identifier;
    private final String residueName;
    private final String modifiedResidueName;
    private final List<PdbAtomLine> atoms;
    private final boolean isModified;
    private final boolean isMissing;

    public PdbResidue(final PdbResidueIdentifier identifier,
                      final String residueName, final List<PdbAtomLine> atoms,
                      final boolean isMissing) {
        this(identifier, residueName, residueName, atoms, false, isMissing);
    }

    public PdbResidue(final PdbResidueIdentifier identifier,
                      final String residueName,
                      final String modifiedResidueName,
                      final List<PdbAtomLine> atoms, final boolean isModified,
                      final boolean isMissing) {
        super();
        this.identifier = identifier;
        this.residueName = residueName;
        this.modifiedResidueName = modifiedResidueName;
        this.atoms = new ArrayList<>(atoms);
        this.isMissing = isMissing;
        atomNames = detectAtomNames();

        if (isMissing) {
            residueInformationProvider = ResidueTypeDetector
                    .detectResidueTypeFromResidueName(residueName);
            this.isModified = false;
        } else {
            residueInformationProvider = ResidueTypeDetector
                    .detectResidueType(modifiedResidueName, atomNames);
            this.isModified =
                    isModified || (wasSuccessfullyDetected() && !hasAllAtoms());
        }
    }

    private List<AtomName> detectAtomNames() {
        final List<AtomName> result = new ArrayList<>();
        for (final PdbAtomLine atom : atoms) {
            result.add(atom.detectAtomName());
        }
        return result;
    }

    public final boolean wasSuccessfullyDetected() {
        return !(residueInformationProvider instanceof
                InvalidResidueInformationProvider);
    }

    public final boolean hasAllAtoms() {
        final Collection<AtomName> expected = new ArrayList<>();
        final Collection<AtomName> additional = new ArrayList<>();

        for (final ResidueComponent component : residueInformationProvider
                .getAllMoleculeComponents()) {
            expected.addAll(component.getAtoms());
            additional.addAll(component.getAdditionalAtoms());
        }

        final Predicate<AtomName> isHeavyAtomPredicate =
                PredicateUtils.invokerPredicate("isHeavy");
        final List<AtomName> actual = new ArrayList<>(atomNames);
        CollectionUtils.filter(actual, isHeavyAtomPredicate);
        CollectionUtils.filter(expected, isHeavyAtomPredicate);
        final boolean result =
                CollectionUtils.isEqualCollection(actual, expected);

        if (!result) {
            final Collection<AtomName> intersection = new ArrayList<>(actual);
            intersection.retainAll(expected);
            actual.removeAll(intersection);
            actual.removeAll(additional);
            expected.removeAll(intersection);

            if (!actual.isEmpty()) {
                PdbResidue.LOGGER
                        .debug("Residue {} ({}) contains additional atoms: {}",
                               this, getDetectedResidueName(), Arrays.toString(
                                        actual.toArray(
                                                new AtomName[actual.size()])));
            }
            if (!expected.isEmpty()) {
                PdbResidue.LOGGER
                        .debug("Residue {} ({}) has missing atoms: {}", this,
                               getDetectedResidueName(), Arrays.toString(
                                        expected.toArray(new AtomName[expected
                                                .size()])));
            }
        }

        return result;
    }

    public final String getDetectedResidueName() {
        return residueInformationProvider.getDefaultPdbName();
    }

    public static PdbResidue fromBioJavaGroup(final Group group) {
        final ResidueNumber residueNumberObject = group.getResidueNumber();
        final String chainIdentifier = residueNumberObject.getChainName();
        final int residueNumber = residueNumberObject.getSeqNum();
        final String insertionCode =
                (residueNumberObject.getInsCode() == null) ? " " : Character
                        .toString(residueNumberObject.getInsCode());
        final PdbResidueIdentifier residueIdentifier =
                new PdbResidueIdentifier(chainIdentifier, residueNumber,
                                         insertionCode);
        final String residueName = group.getPDBName();
        final List<PdbAtomLine> atoms = new ArrayList<>();

        for (final Atom atom : group.getAtoms()) {
            atoms.add(PdbAtomLine.fromBioJavaAtom(atom));
        }

        return new PdbResidue(residueIdentifier, residueName, atoms, false);
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

    public final Iterable<TorsionAngleType> getTorsionAngleTypes() {
        return residueInformationProvider.getTorsionAngleTypes();
    }

    public final char getOneLetterName() {
        final char oneLetterName =
                residueInformationProvider.getOneLetterName();
        return isModified ? Character.toLowerCase(oneLetterName)
                          : oneLetterName;
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
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        final PdbResidue other = (PdbResidue) o;
        return (isModified == other.isModified) &&
               (isMissing == other.isMissing) &&
               Objects.equals(atomNames, other.atomNames) &&
               Objects.equals(identifier, other.identifier) &&
               Objects.equals(residueName, other.residueName) &&
               Objects.equals(modifiedResidueName, other.modifiedResidueName) &&
               Objects.equals(atoms, other.atoms);
    }

    @Override
    public final int hashCode() {
        return Objects
                .hash(atomNames, identifier, residueName, modifiedResidueName,
                      atoms, isModified, isMissing);
    }

    @Override
    public final String toString() {
        final String chainIdentifier = identifier.getChainIdentifier();
        final int residueNumber = identifier.getResidueNumber();
        final String insertionCode = identifier.getInsertionCode();
        return chainIdentifier + '.' + modifiedResidueName + residueNumber +
               (Objects.equals(" ", insertionCode) ? "" : insertionCode);
    }

    @Override
    public final int compareTo(@Nonnull final PdbResidue t) {
        return identifier.compareTo(t.identifier);
    }

    public final boolean hasAtom(final AtomName atomName) {
        return atomNames.contains(atomName);
    }

    public final boolean hasHydrogen() {
        final Predicate<AtomName> notIsHeavyPredicate = PredicateUtils
                .notPredicate(PredicateUtils.invokerPredicate("isHeavy"));
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
        final MoleculeType moleculeType =
                residueInformationProvider.getMoleculeType();
        return moleculeType.areConnected(this, other);
    }

    public final int findConnectedResidueIndex(
            final List<PdbResidue> candidates) {
        final MoleculeType moleculeType =
                residueInformationProvider.getMoleculeType();
        for (int i = 0; i < candidates.size(); i++) {
            final PdbResidue candidate = candidates.get(i);
            if (moleculeType.areConnected(this, candidate)) {
                return i;
            }
        }
        return -1;
    }

    public final String toPdb() {
        final StringBuilder builder = new StringBuilder();
        for (final PdbAtomLine atom : atoms) {
            builder.append(atom);
            builder.append('\n');
        }
        return builder.toString();
    }

    public final ResidueInformationProvider getResidueInformationProvider() {
        return residueInformationProvider;
    }
}
