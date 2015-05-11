package pl.poznan.put.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.AtomType;
import pl.poznan.put.protein.aminoacid.AminoAcidType;
import pl.poznan.put.rna.base.NucleobaseType;

public class ResidueTypeDetector {
    private static final ResidueTypeDetector INSTANCE = new ResidueTypeDetector();

    public static ResidueTypeDetector getInstance() {
        return ResidueTypeDetector.INSTANCE;
    }

    private final List<ResidueInformationProvider> residueInformationProviders = new ArrayList<ResidueInformationProvider>();

    private ResidueTypeDetector() {
        super();
        for (NucleobaseType nucleobase : NucleobaseType.values()) {
            residueInformationProviders.add(nucleobase.getResidueInformationProvider());
        }
        for (AminoAcidType aminoAcid : AminoAcidType.values()) {
            residueInformationProviders.add(aminoAcid.getResidueInformationProvider());
        }
    }

    public ResidueInformationProvider detectResidueType(String residueName,
            Collection<AtomName> atomNames) {
        ResidueInformationProvider provider = detectResidueTypeFromResidueName(residueName);
        if (provider.getMoleculeType() != MoleculeType.UNKNOWN) {
            return provider;
        }
        return detectResidueTypeFromAtoms(atomNames, residueName);
    }

    public ResidueInformationProvider detectResidueTypeFromResidueName(
            String residueName) {
        for (ResidueInformationProvider provider : residueInformationProviders) {
            if (provider.getPdbNames().contains(residueName)) {
                return provider;
            }
        }
        return new InvalidResidueInformationSupplier(MoleculeType.UNKNOWN, residueName);
    }

    public ResidueInformationProvider detectResidueTypeFromAtoms(
            Collection<AtomName> atomNames, String residueName) {
        boolean hasHydrogen = hasHydrogen(atomNames);
        Predicate<AtomName> isHeavyAtomPredicate = PredicateUtils.invokerPredicate("isHeavy");
        Set<AtomName> actual = new HashSet<AtomName>(atomNames);

        if (!hasHydrogen) {
            CollectionUtils.filter(actual, isHeavyAtomPredicate);
        }

        double bestScore = Double.POSITIVE_INFINITY;
        ResidueInformationProvider bestProvider = null;

        for (ResidueInformationProvider provider : residueInformationProviders) {
            Set<AtomName> expected = new HashSet<AtomName>();

            for (ResidueComponent component : provider.getAllMoleculeComponents()) {
                for (AtomName atomName : component.getAtoms()) {
                    if (!hasHydrogen && atomName.getType() == AtomType.H) {
                        continue;
                    }
                    expected.add(atomName);
                }
            }

            Collection<AtomName> disjunction = CollectionUtils.disjunction(expected, atomNames);
            Collection<AtomName> union = CollectionUtils.union(expected, atomNames);
            double score = (double) disjunction.size() / (double) union.size();

            if (score < bestScore) {
                bestScore = score;
                bestProvider = provider;
            }
        }

        // value 0.5 found empirically
        if (bestScore < 0.5) {
            return bestProvider;
        }
        return new InvalidResidueInformationSupplier(MoleculeType.UNKNOWN, residueName);
    }

    private static boolean hasHydrogen(Collection<AtomName> atomNames) {
        Predicate<AtomName> notIsHeavyPredicate = PredicateUtils.notPredicate(PredicateUtils.invokerPredicate("isHeavy"));
        return CollectionUtils.exists(atomNames, notIsHeavyPredicate);
    }
}
