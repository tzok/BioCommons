package pl.poznan.put.protein.torsion;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.protein.ProteinSidechain;
import pl.poznan.put.protein.aminoacid.AminoAcidType;
import pl.poznan.put.torsion.type.TorsionAngleType;

public enum ProteinChiType {
    CHI1, CHI2, CHI3, CHI4, CHI5;

    public TorsionAngleType[] getAngleTypes() {
        List<TorsionAngleType> typesList = new ArrayList<TorsionAngleType>();

        for (AminoAcidType aminoAcid : AminoAcidType.values()) {
            ProteinSidechain sidechain = aminoAcid.getResidueComponent();

            if (sidechain.hasChiDefined(this)) {
                TorsionAngleType angleType;

                switch (this) {
                case CHI1:
                    angleType = Chi1.getInstance(sidechain);
                    break;
                case CHI2:
                    angleType = Chi2.getInstance(sidechain);
                    break;
                case CHI3:
                    angleType = Chi3.getInstance(sidechain);
                    break;
                case CHI4:
                    angleType = Chi4.getInstance(sidechain);
                    break;
                case CHI5:
                    angleType = Chi5.getInstance(sidechain);
                    break;
                default:
                    angleType = TorsionAngleType.invalidInstance();
                    break;
                }

                typesList.add(angleType);
            }
        }

        return typesList.toArray(new TorsionAngleType[typesList.size()]);
    }
}
