package pl.poznan.put.protein.aminoacid;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.torsion.Chi1;
import pl.poznan.put.protein.torsion.Chi2;
import pl.poznan.put.protein.torsion.Chi3;
import pl.poznan.put.protein.torsion.Chi4;
import pl.poznan.put.protein.torsion.Chi5;
import pl.poznan.put.protein.torsion.ProteinChiType;
import pl.poznan.put.torsion.TorsionAngleType;

public enum AminoAcidType implements ResidueInformationProvider {
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

    private final ProteinSidechain sidechain;

    AminoAcidType(ProteinSidechain sidechain) {
        this.sidechain = sidechain;
    }

    public ProteinSidechain getProteinSidechainInstance() {
        return sidechain;
    }

    public static TorsionAngleType[] getChiInstances(ProteinChiType chiType) {
        List<TorsionAngleType> typesList = new ArrayList<TorsionAngleType>();

        for (AminoAcidType aminoAcidType : values()) {
            ProteinSidechain residueComponent = aminoAcidType.getProteinSidechainInstance();
            if (!residueComponent.hasChiDefined(chiType)) {
                continue;
            }

            switch (chiType) {
            case CHI1:
                typesList.add(Chi1.getInstance(residueComponent.getChiAtoms(chiType)));
                break;
            case CHI2:
                typesList.add(Chi2.getInstance(residueComponent.getChiAtoms(chiType)));
                break;
            case CHI3:
                typesList.add(Chi3.getInstance(residueComponent.getChiAtoms(chiType)));
                break;
            case CHI4:
                typesList.add(Chi4.getInstance(residueComponent.getChiAtoms(chiType)));
                break;
            case CHI5:
                typesList.add(Chi5.getInstance(residueComponent.getChiAtoms(chiType)));
                break;
            default:
                break;
            }
        }

        return typesList.toArray(new TorsionAngleType[typesList.size()]);
    }

    @Override
    public MoleculeType getMoleculeType() {
        return sidechain.getMoleculeType();
    }

    @Override
    public List<ResidueComponent> getAllMoleculeComponents() {
        return sidechain.getAllMoleculeComponents();
    }

    @Override
    public String getDescription() {
        return sidechain.getDescription();
    }

    @Override
    public char getOneLetterName() {
        return sidechain.getOneLetterName();
    }

    @Override
    public String getDefaultPdbName() {
        return sidechain.getDefaultPdbName();
    }

    @Override
    public List<String> getPdbNames() {
        return sidechain.getPdbNames();
    }

    @Override
    public List<TorsionAngleType> getTorsionAngleTypes() {
        return sidechain.getTorsionAngleTypes();
    }
}
