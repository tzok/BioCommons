package pl.poznan.put.pdb.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.AminoAcidType;
import pl.poznan.put.common.InvalidResidueInformationSupplier;
import pl.poznan.put.common.NucleobaseType;
import pl.poznan.put.common.ResidueComponent;
import pl.poznan.put.common.ResidueInformationProvider;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.PdbAtomLine;

public class PdbResidue implements Comparable<PdbResidue>, ChainNumberICode {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdbResidue.class);

    private final List<AtomName> atomNames;
    private final ResidueInformationProvider nameSupplier;

    private final PdbResidueIdentifier identifier;
    private final String residueName;
    private final String modifiedResidueName;
    private final List<PdbAtomLine> atoms;
    private final boolean isModified;
    private final boolean isMissing;

    public PdbResidue(PdbResidueIdentifier identifier, String residueName, String modifiedResidueName, List<PdbAtomLine> atoms, boolean isModified, boolean isMissing) {
        super();
        this.identifier = identifier;
        this.residueName = residueName;
        this.modifiedResidueName = modifiedResidueName;
        this.atoms = atoms;
        this.isModified = isModified;
        this.isMissing = isMissing;

        atomNames = detectAtomNames();
        nameSupplier = detectNameSupplier();
    }

    public PdbResidue(PdbResidueIdentifier identifier, String residueName, List<PdbAtomLine> atoms, boolean isModified, boolean isMissing) {
        this(identifier, residueName, residueName, atoms, isModified, isMissing);
        assert !isModified : "This is constructor for unmodified residues";
    }

    private List<AtomName> detectAtomNames() {
        List<AtomName> result = new ArrayList<AtomName>();
        for (PdbAtomLine atom : atoms) {
            result.add(atom.detectAtomName());
        }
        return result;
    }

    private ResidueInformationProvider detectNameSupplier() {
        List<ResidueInformationProvider> candidates = new ArrayList<ResidueInformationProvider>();

        for (NucleobaseType nucleobase : NucleobaseType.values()) {
            candidates.add(nucleobase.getResidueInformationProvider());
        }
        for (AminoAcidType aminoacid : AminoAcidType.values()) {
            candidates.add(aminoacid.getNameSupplier());
        }

        int bestScore = Integer.MIN_VALUE;
        ResidueInformationProvider bestSupplier = null;

        for (ResidueInformationProvider supplier : candidates) {
            Set<AtomName> set = new HashSet<AtomName>(atomNames);
            set.retainAll(supplier.getAtoms());
            int score = set.size();

            if (score > bestScore) {
                bestScore = score;
                bestSupplier = supplier;
            }
        }

        if (bestScore < 3) {
            return new InvalidResidueInformationSupplier(residueName);
        }

        assert bestSupplier != null;
        return bestSupplier;
    }

    public List<PdbAtomLine> getAtoms() {
        return Collections.unmodifiableList(atoms);
    }

    @Override
    public PdbResidueIdentifier getResidueIdentifier() {
        return identifier;
    }

    @Override
    public char getChainIdentifier() {
        return identifier.getChainIdentifier();
    }

    @Override
    public int getResidueNumber() {
        return identifier.getResidueNumber();
    }

    @Override
    public char getInsertionCode() {
        return identifier.getInsertionCode();
    }

    public String getOriginalResidueName() {
        return residueName;
    }

    public String getModifiedResidueName() {
        return modifiedResidueName;
    }

    public String getDetectedResidueName() {
        return nameSupplier.getDefaultPdbName();
    }

    public char getOneLetterName() {
        return nameSupplier.getOneLetterName();
    }

    public boolean isModified() {
        return isModified;
    }

    public boolean isMissing() {
        return isMissing;
    }

    public boolean wasSuccessfullyDetected() {
        return !(nameSupplier instanceof InvalidResidueInformationSupplier);
    }

    @Override
    public String toString() {
        char chainIdentifier = identifier.getChainIdentifier();
        int residueNumber = identifier.getResidueNumber();
        char insertionCode = identifier.getInsertionCode();
        return chainIdentifier + "." + modifiedResidueName + residueNumber + (insertionCode != ' ' ? insertionCode : "");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (atomNames == null ? 0 : atomNames.hashCode());
        result = prime * result + (atoms == null ? 0 : atoms.hashCode());
        result = prime * result + (identifier == null ? 0 : identifier.hashCode());
        result = prime * result + (isMissing ? 1231 : 1237);
        result = prime * result + (isModified ? 1231 : 1237);
        result = prime * result + (modifiedResidueName == null ? 0 : modifiedResidueName.hashCode());
        result = prime * result + (residueName == null ? 0 : residueName.hashCode());
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
    public int compareTo(PdbResidue o) {
        return identifier.compareTo(o.identifier);
    }

    public boolean hasAtom(AtomName atomName) {
        return atomNames.contains(atomName);
    }

    public boolean hasHydrogen() {
        Predicate<AtomName> notIsHeavyPredicate = PredicateUtils.notPredicate(PredicateUtils.invokerPredicate("isHeavy"));
        return CollectionUtils.exists(atomNames, notIsHeavyPredicate);
    }

    public boolean hasAllAtoms() {
        List<AtomName> actual = new ArrayList<AtomName>(atomNames);
        List<AtomName> expected = new ArrayList<AtomName>();

        for (ResidueComponent component : nameSupplier.getAllMoleculeComponents()) {
            expected.addAll(component.getAtoms());
        }

        if (!hasHydrogen()) {
            Predicate<AtomName> isHeavyAtomPredicate = PredicateUtils.invokerPredicate("isHeavy");
            CollectionUtils.filter(actual, isHeavyAtomPredicate);
            CollectionUtils.filter(expected, isHeavyAtomPredicate);
        }

        boolean result = CollectionUtils.isEqualCollection(actual, expected);

        if (!result) {
            ArrayList<AtomName> intersection = new ArrayList<AtomName>(actual);
            intersection.retainAll(expected);
            actual.removeAll(intersection);
            expected.removeAll(intersection);

            if (!actual.isEmpty()) {
                PdbResidue.LOGGER.debug("Residue " + this + " contains additional atoms: " + Arrays.toString(actual.toArray(new AtomName[actual.size()])));
            }
            if (!expected.isEmpty()) {
                PdbResidue.LOGGER.debug("Residue " + this + " was expected to have more: " + Arrays.toString(expected.toArray(new AtomName[expected.size()])));
            }
        }

        return result;
    }

    public PdbAtomLine findAtom(AtomName atomName) {
        for (PdbAtomLine atom : atoms) {
            if (atom.detectAtomName() == atomName) {
                return atom;
            }
        }

        throw new IllegalArgumentException("Failed to find: " + atomName);
    }
}
