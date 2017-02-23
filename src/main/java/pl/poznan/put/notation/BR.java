package pl.poznan.put.notation;

import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.base.NucleobaseType;

/**
 * Base-ribose notation.
 * <p>
 * Zirbel, C. L., Šponer, J. E., Šponer, J., Stombaugh, J., & Leontis, N. B.
 * (2009). Classification and energetics of the base-phosphate interactions in
 * RNA. Nucleic Acids Research, 37(15), 4898–4918. http://doi
 * .org/10.1093/nar/gkp468
 */
public enum BR {
    _0("0BR"),
    _1("1BR"),
    _2("2BR"),
    _3("3BR"),
    _4("4BR"),
    _5("5BR"),
    _6("6BR"),
    _7("7BR"),
    _8("8BR"),
    _9("9BR"),
    UNKNOWN("UNKNOWN");

    private final String displayName;

    BR(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BR detect(
            final PdbResidue base, final PdbResidue ribose) {
        ResidueInformationProvider provider =
                base.getResidueInformationProvider();
        if (!(provider instanceof NucleobaseType)) {
            throw new IllegalArgumentException(
                    "Provided residue is not a nucleotide");
        }

        NucleobaseType nucleobaseType = (NucleobaseType) provider;
        switch (nucleobaseType) {
            case ADENINE:
                return BR.detectForAdenine(base, ribose);
            case CYTOSINE:
                return BR.detectForCytosine(base, ribose);
            case GUANINE:
                return BR.detectForGuanine(base, ribose);
            case URACIL:
                return BR.detectForUracil(base, ribose);
            case THYMINE:
            default:
                throw new IllegalArgumentException(
                        "Only RNA nucleotides are supported");
        }
    }

    private static BR detectForUracil(
            final PdbResidue base, final PdbResidue ribose) {
        // FIXME: implement this
        return BR.UNKNOWN;
    }

    private static BR detectForGuanine(
            final PdbResidue base, final PdbResidue ribose) {
        // FIXME: implement this
        return BR.UNKNOWN;
    }

    private static BR detectForCytosine(
            final PdbResidue base, final PdbResidue ribose) {
        // FIXME: implement this
        return BR.UNKNOWN;
    }

    private static BR detectForAdenine(
            final PdbResidue base, final PdbResidue ribose) {
        // FIXME: implement this
        return BR.UNKNOWN;
    }
}
