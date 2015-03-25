package pl.poznan.put.pdb.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.common.NucleobaseType;
import pl.poznan.put.common.ResidueComponent;
import pl.poznan.put.common.ResidueInformationProvider;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.PdbAtomLine;

public class PdbResidue implements Comparable<PdbResidue>, ChainNumberICode {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdbResidue.class);
    private static final List<ResidueInformationProvider> RESIDUE_INFORMATION_PROVIDERS = new ArrayList<ResidueInformationProvider>();

    static {
        for (NucleobaseType nucleobase : NucleobaseType.values()) {
            RESIDUE_INFORMATION_PROVIDERS.add(nucleobase.getResidueInformationProvider());
        }
        for (AminoAcidType aminoAcid : AminoAcidType.values()) {
            RESIDUE_INFORMATION_PROVIDERS.add(aminoAcid.getResidueInformationProvider());
        }
    }

    private final List<AtomName> atomNames;
    private final ResidueInformationProvider nameProvider;

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
        this.isMissing = isMissing;

        this.atomNames = detectAtomNames();

        if (isMissing) {
            this.nameProvider = detectResidueTypeFromResidueName();
            this.isModified = false;
        } else {
            this.nameProvider = detectResidueType();
            this.isModified = isModified | (wasSuccessfullyDetected() ? !hasAllAtoms() : false);
        }
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

    private ResidueInformationProvider detectResidueType() {
        double bestScore = Double.POSITIVE_INFINITY;
        ResidueInformationProvider bestProvider = null;

        for (ResidueInformationProvider provider : RESIDUE_INFORMATION_PROVIDERS) {
            List<ResidueComponent> components = provider.getAllMoleculeComponents();
            Set<AtomName> nucleobaseAtomNames = new HashSet<AtomName>();

            for (ResidueComponent component : components) {
                nucleobaseAtomNames.addAll(component.getAtoms());
            }

            Collection<AtomName> disjunction = CollectionUtils.disjunction(nucleobaseAtomNames, atomNames);
            Collection<AtomName> union = CollectionUtils.union(nucleobaseAtomNames, atomNames);
            double score = (double) disjunction.size() / (double) union.size();

            if (score < bestScore) {
                bestScore = score;
                bestProvider = provider;
            }
        }

        // value 0.6 found empirically
        // for A.YYG37 in 1EHZ, the score is 0.54
        if (bestScore < 0.6) {
            return bestProvider;
        }
        return detectResidueTypeFromResidueName();
    }

    private ResidueInformationProvider detectResidueTypeFromResidueName() {
        for (ResidueInformationProvider provider : RESIDUE_INFORMATION_PROVIDERS) {
            if (provider.getPdbNames().contains(residueName)) {
                return provider;
            }
        }
        return new InvalidResidueInformationSupplier(MoleculeType.UNKNOWN, residueName);
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
        return nameProvider.getDefaultPdbName();
    }

    public char getOneLetterName() {
        char oneLetterName = nameProvider.getOneLetterName();
        return isModified ? Character.toLowerCase(oneLetterName) : oneLetterName;
    }

    public boolean isModified() {
        return isModified;
    }

    public boolean isMissing() {
        return isMissing;
    }

    public MoleculeType getMoleculeType() {
        return nameProvider.getMoleculeType();
    }

    public boolean wasSuccessfullyDetected() {
        return !(nameProvider instanceof InvalidResidueInformationSupplier);
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

        for (ResidueComponent component : nameProvider.getAllMoleculeComponents()) {
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
                PdbResidue.LOGGER.debug("Residue " + this + " (" + getDetectedResidueName() + ") contains additional atoms: " + Arrays.toString(actual.toArray(new AtomName[actual.size()])));
            }
            if (!expected.isEmpty()) {
                PdbResidue.LOGGER.debug("Residue " + this + " (" + getDetectedResidueName() + ") was expected to have more: " + Arrays.toString(expected.toArray(new AtomName[expected.size()])));
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
