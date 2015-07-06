package pl.poznan.put.pdb.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.AtomType;
import pl.poznan.put.protein.aminoacid.AminoAcidType;
import pl.poznan.put.rna.base.NucleobaseType;

public class ResidueTypeDetector {
    private static final Set<ResidueInformationProvider> PROVIDERS = new LinkedHashSet<ResidueInformationProvider>();

    static {
        for (NucleobaseType nucleobase : NucleobaseType.values()) {
            ResidueTypeDetector.PROVIDERS.add(nucleobase);
        }
        for (AminoAcidType aminoAcid : AminoAcidType.values()) {
            ResidueTypeDetector.PROVIDERS.add(aminoAcid);
        }
    }

    public static ResidueInformationProvider detectResidueType(
            String residueName, Collection<AtomName> atomNames) {
        ResidueInformationProvider provider = ResidueTypeDetector.detectResidueTypeFromResidueName(residueName);
        if (provider.getMoleculeType() != MoleculeType.UNKNOWN) {
            return provider;
        }
        return ResidueTypeDetector.detectResidueTypeFromAtoms(atomNames, residueName);
    }

    public static ResidueInformationProvider detectResidueTypeFromResidueName(
            String residueName) {
        for (ResidueInformationProvider provider : ResidueTypeDetector.PROVIDERS) {
            if (provider.getPdbNames().contains(residueName)) {
                return provider;
            }
        }
        return new InvalidResidueInformationProvider(MoleculeType.UNKNOWN, residueName);
    }

    public static ResidueInformationProvider detectResidueTypeFromAtoms(
            Collection<AtomName> atomNames, String residueName) {
        boolean hasHydrogen = ResidueTypeDetector.hasHydrogen(atomNames);
        Predicate<AtomName> isHeavyAtomPredicate = PredicateUtils.invokerPredicate("isHeavy");
        Set<AtomName> actual = new HashSet<AtomName>(atomNames);

        if (!hasHydrogen) {
            CollectionUtils.filter(actual, isHeavyAtomPredicate);
        }

        double bestScore = Double.POSITIVE_INFINITY;
        ResidueInformationProvider bestProvider = null;

        for (ResidueInformationProvider provider : ResidueTypeDetector.PROVIDERS) {
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
        return new InvalidResidueInformationProvider(MoleculeType.UNKNOWN, residueName);
    }

    private static boolean hasHydrogen(Collection<AtomName> atomNames) {
        Predicate<AtomName> notIsHeavyPredicate = PredicateUtils.notPredicate(PredicateUtils.invokerPredicate("isHeavy"));
        return CollectionUtils.exists(atomNames, notIsHeavyPredicate);
    }

    private ResidueTypeDetector() {
        // empty constructor
    }
}
