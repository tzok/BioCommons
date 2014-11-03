package pl.poznan.put.torsion;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;

public enum ChiTorsionAngleType implements TorsionAngle {
    CHI(MoleculeType.RNA, Unicode.CHI),
    CHI1(MoleculeType.PROTEIN, Unicode.CHI1),
    CHI2(MoleculeType.PROTEIN, Unicode.CHI2),
    CHI3(MoleculeType.PROTEIN, Unicode.CHI3),
    CHI4(MoleculeType.PROTEIN, Unicode.CHI4),
    CHI5(MoleculeType.PROTEIN, Unicode.CHI5);

    public static ChiTorsionAngleType[] getChiTorsionAngles(
            MoleculeType moleculeType) {
        List<ChiTorsionAngleType> result = new ArrayList<ChiTorsionAngleType>();
        for (ChiTorsionAngleType type : ChiTorsionAngleType.values()) {
            if (type.getMoleculeType() == moleculeType) {
                result.add(type);
            }
        }
        return result.toArray(new ChiTorsionAngleType[result.size()]);
    }

    private final MoleculeType moleculeType;
    private final String longDisplayName;
    private final String shortDisplayName;

    private ChiTorsionAngleType(MoleculeType moleculeType, String unicodeName) {
        this.moleculeType = moleculeType;
        this.longDisplayName = unicodeName + " (" + name().toLowerCase() + ")";
        this.shortDisplayName = unicodeName;
    }

    @Override
    public String getLongDisplayName() {
        return longDisplayName;
    }

    @Override
    public String getShortDisplayName() {
        return shortDisplayName;
    }

    @Override
    public String getExportName() {
        return name();
    }

    @Override
    public MoleculeType getMoleculeType() {
        return moleculeType;
    }
}
