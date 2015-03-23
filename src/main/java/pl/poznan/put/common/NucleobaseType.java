package pl.poznan.put.common;

import pl.poznan.put.rna.base.Adenine;
import pl.poznan.put.rna.base.Cytosine;
import pl.poznan.put.rna.base.Guanine;
import pl.poznan.put.rna.base.Thymine;
import pl.poznan.put.rna.base.Uracil;

public enum NucleobaseType {
    ADENINE(Adenine.getInstance()),
    CYTOSINE(Cytosine.getInstance()),
    GUANINE(Guanine.getInstance()),
    URACIL(Uracil.getInstance()),
    THYMINE(Thymine.getInstance());

    private final ResidueInformationProvider provider;

    private NucleobaseType(ResidueInformationProvider provider) {
        this.provider = provider;
    }

    public ResidueInformationProvider getResidueInformationProvider() {
        return provider;
    }
}
