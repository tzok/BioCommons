package pl.poznan.put.notation;

import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.base.NucleobaseType;

/**
 * Base-phosphate notation.
 * <p>
 * Zirbel, C. L., Šponer, J. E., Šponer, J., Stombaugh, J., & Leontis, N. B.
 * (2009). Classification and energetics of the base-phosphate interactions in
 * RNA. Nucleic Acids Research, 37(15), 4898–4918. http://doi
 * .org/10.1093/nar/gkp468
 */
public enum BPh {
    _0("0BPh"),
    _1("1BPh"),
    _2("2BPh"),
    _3("3BPh"),
    _4("4BPh"),
    _5("5BPh"),
    _6("6BPh"),
    _7("7BPh"),
    _8("8BPh"),
    _9("9BPh"),
    UNKNOWN("UNKNOWN");

    private final String displayName;

    BPh(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
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
}
