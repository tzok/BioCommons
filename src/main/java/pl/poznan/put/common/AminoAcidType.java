package pl.poznan.put.common;

import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.aminoacid.Alanine;
import pl.poznan.put.protein.aminoacid.Arginine;
import pl.poznan.put.protein.aminoacid.Asparagine;
import pl.poznan.put.protein.aminoacid.AsparticAcid;
import pl.poznan.put.protein.aminoacid.Cysteine;
import pl.poznan.put.protein.aminoacid.GlutamicAcid;
import pl.poznan.put.protein.aminoacid.Glutamine;
import pl.poznan.put.protein.aminoacid.Glycine;
import pl.poznan.put.protein.aminoacid.Histidine;
import pl.poznan.put.protein.aminoacid.IsoLeucine;
import pl.poznan.put.protein.aminoacid.Leucine;
import pl.poznan.put.protein.aminoacid.Lysine;
import pl.poznan.put.protein.aminoacid.Methionine;
import pl.poznan.put.protein.aminoacid.Phenylalanine;
import pl.poznan.put.protein.aminoacid.Proline;
import pl.poznan.put.protein.aminoacid.Serine;
import pl.poznan.put.protein.aminoacid.Threonine;
import pl.poznan.put.protein.aminoacid.Tryptophan;
import pl.poznan.put.protein.aminoacid.Tyrosine;
import pl.poznan.put.protein.aminoacid.Valine;

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
