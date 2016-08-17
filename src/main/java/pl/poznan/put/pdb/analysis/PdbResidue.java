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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PdbResidue
        implements Serializable, Comparable<PdbResidue>, ChainNumberICode {
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

    public PdbResidue(PdbResidueIdentifier identifier, String residueName,
                      List<PdbAtomLine> atoms, boolean isMissing) {
        this(identifier, residueName, residueName, atoms, false, isMissing);
    }

    public PdbResidue(PdbResidueIdentifier identifier, String residueName,
                      String modifiedResidueName, List<PdbAtomLine> atoms,
                      boolean isModified, boolean isMissing) {
        super();
        this.identifier = identifier;
        this.residueName = residueName;
        this.modifiedResidueName = modifiedResidueName;
        this.atoms = atoms;
        this.isMissing = isMissing;
        this.atomNames = detectAtomNames();

        if (isMissing) {
            this.residueInformationProvider = ResidueTypeDetector
                    .detectResidueTypeFromResidueName(residueName);
            this.isModified = false;
        } else {
            this.residueInformationProvider = ResidueTypeDetector
                    .detectResidueType(modifiedResidueName, atomNames);
            this.isModified =
                    isModified || (wasSuccessfullyDetected() && !hasAllAtoms());
        }
    }

    private List<AtomName> detectAtomNames() {
        List<AtomName> result = new ArrayList<>();
        for (PdbAtomLine atom : atoms) {
            result.add(atom.detectAtomName());
        }
        return result;
    }

    public final boolean wasSuccessfullyDetected() {
        return !(residueInformationProvider instanceof
                InvalidResidueInformationProvider);
    }

    public final boolean hasAllAtoms() {
        List<AtomName> actual = new ArrayList<>(atomNames);
        List<AtomName> expected = new ArrayList<>();
        List<AtomName> additional = new ArrayList<>();

        for (ResidueComponent component : residueInformationProvider
                .getAllMoleculeComponents()) {
            expected.addAll(component.getAtoms());
            additional.addAll(component.getAdditionalAtoms());
        }

        Predicate<AtomName> isHeavyAtomPredicate =
                PredicateUtils.invokerPredicate("isHeavy");
        CollectionUtils.filter(actual, isHeavyAtomPredicate);
        CollectionUtils.filter(expected, isHeavyAtomPredicate);
        boolean result = CollectionUtils.isEqualCollection(actual, expected);

        if (!result) {
            List<AtomName> intersection = new ArrayList<>(actual);
            intersection.retainAll(expected);
            actual.removeAll(intersection);
            actual.removeAll(additional);
            expected.removeAll(intersection);

            if (!actual.isEmpty()) {
                PdbResidue.LOGGER.debug("Residue " + this + " ("
                                        + getDetectedResidueName()
                                        + ") contains additional atoms: "
                                        + Arrays.toString(
                        actual.toArray(new AtomName[actual.size()])));
            }
            if (!expected.isEmpty()) {
                PdbResidue.LOGGER.debug("Residue " + this + " ("
                                        + getDetectedResidueName()
                                        + ") has missing atoms: " + Arrays
                                                .toString(expected.toArray(
                                                        new AtomName[expected
                                                                .size()])));
            }
        }

        return result;
    }

    public final String getDetectedResidueName() {
        return residueInformationProvider.getDefaultPdbName();
    }

    public static PdbResidue fromBioJavaGroup(Group group) {
        ResidueNumber residueNumberObject = group.getResidueNumber();
        String chainIdentifier = residueNumberObject.getChainId();
        int residueNumber = residueNumberObject.getSeqNum();
        String insertionCode = residueNumberObject.getInsCode() == null ? " "
                                                                        :
                               Character
                                       .toString(residueNumberObject
                                                         .getInsCode());
        PdbResidueIdentifier residueIdentifier =
                new PdbResidueIdentifier(chainIdentifier, residueNumber,
                                         insertionCode);
        String residueName = group.getPDBName();
        List<PdbAtomLine> atoms = new ArrayList<>();

        for (Atom atom : group.getAtoms()) {
            atoms.add(PdbAtomLine.fromBioJavaAtom(atom));
        }

        return new PdbResidue(residueIdentifier, residueName, atoms, false);
    }

    public List<PdbAtomLine> getAtoms() {
        return Collections.unmodifiableList(atoms);
    }

    @Override
    public String getChainIdentifier() {
        return identifier.getChainIdentifier();
    }

    @Override
    public int getResidueNumber() {
        return identifier.getResidueNumber();
    }

    @Override
    public String getInsertionCode() {
        return identifier.getInsertionCode();
    }

    @Override
    public PdbResidueIdentifier getResidueIdentifier() {
        return identifier;
    }

    public String getOriginalResidueName() {
        return residueName;
    }

    public String getModifiedResidueName() {
        return modifiedResidueName;
    }

    public List<TorsionAngleType> getTorsionAngleTypes() {
        return residueInformationProvider.getTorsionAngleTypes();
    }

    public char getOneLetterName() {
        char oneLetterName = residueInformationProvider.getOneLetterName();
        return isModified ? Character.toLowerCase(oneLetterName)
                          : oneLetterName;
    }

    public boolean isModified() {
        return isModified;
    }

    public boolean isMissing() {
        return isMissing;
    }

    public MoleculeType getMoleculeType() {
        return residueInformationProvider.getMoleculeType();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
                prime * result + (atomNames == null ? 0 : atomNames.hashCode());
        result = prime * result + (atoms == null ? 0 : atoms.hashCode());
        result = prime * result + (identifier == null ? 0
                                                      : identifier.hashCode());
        result = prime * result + (isMissing ? 1231 : 1237);
        result = prime * result + (isModified ? 1231 : 1237);
        result = prime * result + (modifiedResidueName == null ? 0
                                                               :
                                   modifiedResidueName
                                           .hashCode());
        result = prime * result + (residueName == null ? 0 : residueName
                .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PdbResidue other = (PdbResidue) obj;
        if (atomNames == null) {
            if (other.atomNames != null) {
                return false;
            }
        } else if (!atomNames.equals(other.atomNames)) {
            return false;
        }
        if (atoms == null) {
            if (other.atoms != null) {
                return false;
            }
        } else if (!atoms.equals(other.atoms)) {
            return false;
        }
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        if (isMissing != other.isMissing) {
            return false;
        }
        if (isModified != other.isModified) {
            return false;
        }
        if (modifiedResidueName == null) {
            if (other.modifiedResidueName != null) {
                return false;
            }
        } else if (!modifiedResidueName.equals(other.modifiedResidueName)) {
            return false;
        }
        if (residueName == null) {
            if (other.residueName != null) {
                return false;
            }
        } else if (!residueName.equals(other.residueName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String chainIdentifier = identifier.getChainIdentifier();
        int residueNumber = identifier.getResidueNumber();
        String insertionCode = identifier.getInsertionCode();
        return chainIdentifier + "." + modifiedResidueName + residueNumber + (
                " ".equals(insertionCode) ? "" : insertionCode);
    }

    @Override
    public int compareTo(PdbResidue o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return identifier.compareTo(o.identifier);
    }

    public boolean hasAtom(AtomName atomName) {
        return atomNames.contains(atomName);
    }

    public final boolean hasHydrogen() {
        Predicate<AtomName> notIsHeavyPredicate = PredicateUtils
                .notPredicate(PredicateUtils.invokerPredicate("isHeavy"));
        return IterableUtils.matchesAny(atomNames, notIsHeavyPredicate);
    }

    public PdbAtomLine findAtom(AtomName atomName) {
        for (PdbAtomLine atom : atoms) {
            if (atom.detectAtomName() == atomName) {
                return atom;
            }
        }

        throw new IllegalArgumentException("Failed to find: " + atomName);
    }

    public boolean isConnectedTo(PdbResidue other) {
        MoleculeType moleculeType =
                residueInformationProvider.getMoleculeType();
        return moleculeType.areConnected(this, other);
    }

    public int findConnectedResidueIndex(List<PdbResidue> candidates) {
        MoleculeType moleculeType =
                residueInformationProvider.getMoleculeType();
        for (int i = 0; i < candidates.size(); i++) {
            PdbResidue candidate = candidates.get(i);
            if (moleculeType.areConnected(this, candidate)) {
                return i;
            }
        }
        return -1;
    }

    public String toPdb() {
        StringBuilder builder = new StringBuilder();
        for (PdbAtomLine atom : atoms) {
            builder.append(atom.toString());
            builder.append('\n');
        }
        return builder.toString();
    }
}
