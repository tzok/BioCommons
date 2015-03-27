package pl.poznan.put.protein.aminoacid;

import pl.poznan.put.common.ResidueInformationProvider;
import pl.poznan.put.protein.ProteinSidechain;

public enum AminoAcidType {
    ALANINE(Alanine.getInstance()),
    ARGININE(Arginine.getInstance()),
    ASPARAGINE(Asparagine.getInstance()),
    ASPARTIC_ACID(AsparticAcid.getInstance()),
    CYSTEINE(Cysteine.getInstance()),
    GLUTAMIC_ACID(GlutamicAcid.getInstance()),
    GLUTAMINE(Glutamine.getInstance()),
    GLYCINE(Glycine.getInstance()),
    HISTIDINE(Histidine.getInstance()),
    ISOLEUCINE(IsoLeucine.getInstance()),
    LEUCINE(Leucine.getInstance()),
    LYSINE(Lysine.getInstance()),
    METHIONINE(Methionine.getInstance()),
    PHENYLALANINE(Phenylalanine.getInstance()),
    PROLINE(Proline.getInstance()),
    SERINE(Serine.getInstance()),
    THREONINE(Threonine.getInstance()),
    TRYPTOPHAN(Tryptophan.getInstance()),
    TYROSINE(Tyrosine.getInstance()),
    VALINE(Valine.getInstance());

    private final ProteinSidechain nameProvider;

    private AminoAcidType(ProteinSidechain nameSupplier) {
        this.nameProvider = nameSupplier;
    }

    public ResidueInformationProvider getResidueInformationProvider() {
        return nameProvider;
    }

    public ProteinSidechain getResidueComponent() {
        return nameProvider;
    }
}
