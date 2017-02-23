package pl.poznan.put.notation;

import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.base.NucleobaseType;

import java.util.Objects;

/**
 * Base-phosphate notation.
 * <p>
 * Zirbel, C. L., Šponer, J. E., Šponer, J., Stombaugh, J., & Leontis, N. B.
 * (2009). Classification and energetics of the base-phosphate interactions in
 * RNA. Nucleic Acids Research, 37(15), 4898–4918. http://doi
 * .org/10.1093/nar/gkp468
 */
public enum BPh {
    _0("0BPh", "n0BPh", "0PhB", "n0PhB"),
    _1("1BPh", "n1BPh", "1PhB", "n1PhB"),
    _2("2BPh", "n2BPh", "2PhB", "n2PhB"),
    _3("3BPh", "n3BPh", "3PhB", "n3PhB"),
    _4("4BPh", "n4BPh", "4PhB", "n4PhB"),
    _5("5BPh", "n5BPh", "5PhB", "n5PhB"),
    _6("6BPh", "n6BPh", "6PhB", "n6PhB"),
    _7("7BPh", "n7BPh", "7PhB", "n7PhB"),
    _8("8BPh", "n8BPh", "8PhB", "n8PhB"),
    _9("9BPh", "n9BPh", "9PhB", "n9PhB"),
    UNKNOWN("UNKNOWN");

    private final String[] displayNames;

    BPh(final String... displayNames) {
        this.displayNames = displayNames;
    }

    public String getDisplayName() {
        return displayNames[0];
    }

    public static BPh detect(
            final PdbResidue base, final PdbResidue phosphate) {
        ResidueInformationProvider provider =
                base.getResidueInformationProvider();
        if (!(provider instanceof NucleobaseType)) {
            throw new IllegalArgumentException(
                    "Provided residue is not a nucleotide");
        }

        NucleobaseType nucleobaseType = (NucleobaseType) provider;
        switch (nucleobaseType) {
            case ADENINE:
                return BPh.detectForAdenine(base, phosphate);
            case CYTOSINE:
                return BPh.detectForCytosine(base, phosphate);
            case GUANINE:
                return BPh.detectForGuanine(base, phosphate);
            case URACIL:
                return BPh.detectForUracil(base, phosphate);
            case THYMINE:
            default:
                throw new IllegalArgumentException(
                        "Only RNA nucleotides are supported");
        }
    }

    private static BPh detectForUracil(
            final PdbResidue base, final PdbResidue phosphate) {
        // FIXME: implement this
        return BPh.UNKNOWN;
    }

    private static BPh detectForGuanine(
            final PdbResidue base, final PdbResidue phosphate) {
        // FIXME: implement this
        return BPh.UNKNOWN;
    }

    private static BPh detectForCytosine(
            final PdbResidue base, final PdbResidue phosphate) {
        // FIXME: implement this
        return BPh.UNKNOWN;
    }

    private static BPh detectForAdenine(
            final PdbResidue base, final PdbResidue phosphate) {
        // FIXME: implement this
        return BPh.UNKNOWN;
    }

    public static BPh fromString(final String candidate) {
        for (final BPh bph : BPh.values()) {
            for (final String displayName : bph.displayNames) {
                if (Objects.equals(displayName, candidate)) {
                    return bph;
                }
            }
        }
        return BPh.UNKNOWN;
    }
}
