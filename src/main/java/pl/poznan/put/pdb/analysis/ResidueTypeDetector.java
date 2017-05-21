package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.AtomType;
import pl.poznan.put.protein.aminoacid.AminoAcidType;
import pl.poznan.put.rna.base.NucleobaseType;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;

public final class ResidueTypeDetector {
    private static final Collection<ResidueInformationProvider> PROVIDERS =
            new LinkedHashSet<>();

    static {
        Collections
                .addAll(ResidueTypeDetector.PROVIDERS, NucleobaseType.values());
        Collections
                .addAll(ResidueTypeDetector.PROVIDERS, AminoAcidType.values());
    }

    private ResidueTypeDetector() {
        super();
    }

    public static ResidueInformationProvider detectResidueType(
            final String residueName, final Collection<AtomName> atomNames) {
        final ResidueInformationProvider provider = ResidueTypeDetector
                .detectResidueTypeFromResidueName(residueName);
        if (provider.getMoleculeType() != MoleculeType.UNKNOWN) {
            return provider;
        }
        return ResidueTypeDetector
                .detectResidueTypeFromAtoms(atomNames, residueName);
    }

    public static ResidueInformationProvider detectResidueTypeFromResidueName(
            final String residueName) {
        for (final ResidueInformationProvider provider : ResidueTypeDetector
                .PROVIDERS) {
            if (provider.getPdbNames().contains(residueName)) {
                return provider;
            }
        }
        return new InvalidResidueInformationProvider(residueName);
    }

    public static ResidueInformationProvider detectResidueTypeFromAtoms(
            final Collection<AtomName> atomNames, final String residueName) {
        final boolean hasHydrogen = ResidueTypeDetector.hasHydrogen(atomNames);
        final Predicate<AtomName> isHeavyAtomPredicate =
                PredicateUtils.invokerPredicate("isHeavy");

        final Iterable<AtomName> actual = EnumSet.copyOf(atomNames);
        if (!hasHydrogen) {
            CollectionUtils.filter(actual, isHeavyAtomPredicate);
        }

        double bestScore = Double.POSITIVE_INFINITY;
        ResidueInformationProvider bestProvider = null;

        for (final ResidueInformationProvider provider : ResidueTypeDetector
                .PROVIDERS) {
            final Collection<AtomName> expected = EnumSet.noneOf(AtomName.class);

            for (final ResidueComponent component : provider
                    .getAllMoleculeComponents()) {
                for (final AtomName atomName : component.getAtoms()) {
                    if (!hasHydrogen && (atomName.getType() == AtomType.H)) {
                        continue;
                    }
                    expected.add(atomName);
                }
            }

            final Collection<AtomName> disjunction =
                    CollectionUtils.disjunction(expected, actual);
            final Collection<AtomName> union =
                    CollectionUtils.union(expected, actual);
            final double score = disjunction.size() / (double) union.size();

            if (score < bestScore) {
                bestScore = score;
                bestProvider = provider;
            }
        }

        // value 0.5 found empirically
        if (bestScore < 0.5) {
            return bestProvider;
        }
        return new InvalidResidueInformationProvider(residueName);
    }

    private static boolean hasHydrogen(final Iterable<AtomName> atomNames) {
        final Predicate<AtomName> notIsHeavyPredicate = PredicateUtils.notPredicate(
                PredicateUtils.invokerPredicate("isHeavy")); //NON-NLS
        return IterableUtils.matchesAny(atomNames, notIsHeavyPredicate);
    }
}
