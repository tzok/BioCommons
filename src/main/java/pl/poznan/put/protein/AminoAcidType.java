package pl.poznan.put.protein;

public enum AminoAcidType {
    ALANINE('A', "ALA"), ARGININE('R', "ARG"), ASPARAGINE('N', "ASN"),
    ASPARTIC_ACID('D', "ASP"), CYSTEINE('C', "CYS"), GLUTAMINE('Q', "GLN"),
    GLUTAMIC_ACID('E', "GLU"), GLYCINE('G', "GLY"),
    HISTIDINE('H', "HIS", "HSD", "HSE", "HSP"), ISOLEUCINE('I', "ILE"),
    LEUCINE('L', "LEU"), LYSINE('K', "LYS"), METHIONINE('M', "MET"),
    PHENYLALANINE('F', "PHE"), PROLINE('P', "PRO"), SERINE('S', "SER"),
    THREONINE('T', "THR"), TRYPTOPHAN('W', "TRP"), TYROSINE('Y', "TYR"),
    VALINE('V', "VAL"), UNKNOWN('X');

    private final char oneLetter;
    private final String[] names;

    private AminoAcidType(char oneLetter, String... names) {
        this.oneLetter = oneLetter;
        this.names = names;
    }

    public static AminoAcidType fromString(String pdbName) {
        pdbName = pdbName.trim();

        for (AminoAcidType aminoAcid : AminoAcidType.values()) {
            for (String name : aminoAcid.names) {
                if (name.equalsIgnoreCase(pdbName)) {
                    return aminoAcid;
                }
            }
        }

        return AminoAcidType.UNKNOWN;
    }

    public static AminoAcidType fromOneLetter(char oneLetter) {
        oneLetter = Character.toUpperCase(oneLetter);

        for (AminoAcidType aminoAcid : AminoAcidType.values()) {
            if (aminoAcid.oneLetter == oneLetter) {
                return aminoAcid;
            }
        }

        return AminoAcidType.UNKNOWN;
    }
}
