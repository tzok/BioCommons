package pl.poznan.put.nucleotide;

public enum NucleotideType {
    ADENINE('A', "ADE", "A"), GUANINE('G', "GUA", "G"),
    CYTOSINE('C', "CYT", "C"), URACIL('U', "URA", "URI", "U"),
    THYMINE('T', "THY", "T"), UNKNOWN('N');

    private char oneLetter;
    private String[] names;

    private NucleotideType(char oneLetter, String... names) {
        this.oneLetter = oneLetter;
        this.names = names;
    }

    public static NucleotideType fromString(String pdbName) {
        pdbName = pdbName.trim();

        for (NucleotideType nucleotide : NucleotideType.values()) {
            for (String name : nucleotide.names) {
                if (name.equalsIgnoreCase(pdbName)) {
                    return nucleotide;
                }
            }
        }

        return NucleotideType.UNKNOWN;
    }

    public static NucleotideType fromOneLetter(char oneLetter) {
        oneLetter = Character.toUpperCase(oneLetter);

        for (NucleotideType nucleotide : NucleotideType.values()) {
            if (nucleotide.oneLetter == Character.toUpperCase(oneLetter)) {
                return nucleotide;
            }
        }

        return NucleotideType.UNKNOWN;
    }
}
