package pl.poznan.put.rna.base;

import pl.poznan.put.common.ResidueInformationProvider;
import pl.poznan.put.rna.Base;

public enum NucleobaseType {
    ADENINE(Adenine.getInstance()),
    CYTOSINE(Cytosine.getInstance()),
    GUANINE(Guanine.getInstance()),
    URACIL(Uracil.getInstance()),
    THYMINE(Thymine.getInstance());

    private final Base provider;

    private NucleobaseType(Base provider) {
        this.provider = provider;
    }

    public ResidueInformationProvider getResidueInformationProvider() {
        return provider;
    }

    public Base getResidueComponent() {
        return provider;
    }
}
