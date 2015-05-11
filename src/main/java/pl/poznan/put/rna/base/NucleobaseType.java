package pl.poznan.put.rna.base;

import pl.poznan.put.rna.Base;

public enum NucleobaseType {
    ADENINE(Adenine.getInstance()),
    CYTOSINE(Cytosine.getInstance()),
    GUANINE(Guanine.getInstance()),
    URACIL(Uracil.getInstance()),
    THYMINE(Thymine.getInstance()),
    UNKNOWN(Base.invalidInstance());

    private final Base provider;

    private NucleobaseType(Base provider) {
        this.provider = provider;
    }

    public Base getResidueComponent() {
        return provider;
    }

    public static NucleobaseType fromOneLetterName(char oneLetterName) {
        for (NucleobaseType candidate : NucleobaseType.values()) {
            if (Character.toLowerCase(oneLetterName) == Character.toLowerCase(candidate.provider.getOneLetterName())) {
                return candidate;
            }
        }
        return NucleobaseType.UNKNOWN;
    }
}
