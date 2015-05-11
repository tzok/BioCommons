package pl.poznan.put.protein.torsion;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import pl.poznan.put.protein.ProteinChiType;
import pl.poznan.put.torsion.type.MasterTorsionAngleType;
import pl.poznan.put.torsion.type.TorsionAngleType;

public enum ProteinTorsionAngleType implements MasterTorsionAngleType {
    PHI(Phi.getInstance()),
    PSI(Psi.getInstance()),
    OMEGA(Omega.getInstance()),
    CALPHA(Calpha.getInstance()),
    CHI1(ProteinChiType.CHI1.getAngleTypes()),
    CHI2(ProteinChiType.CHI2.getAngleTypes()),
    CHI3(ProteinChiType.CHI3.getAngleTypes()),
    CHI4(ProteinChiType.CHI4.getAngleTypes()),
    CHI5(ProteinChiType.CHI5.getAngleTypes());

    private final List<TorsionAngleType> angleTypes;

    private ProteinTorsionAngleType(TorsionAngleType... angleTypes) {
        this.angleTypes = Arrays.asList(angleTypes);
    }

    @Override
    public Collection<? extends TorsionAngleType> getAngleTypes() {
        return angleTypes;
    }

    private static final MasterTorsionAngleType[] MAIN = new MasterTorsionAngleType[] { PHI, PSI, OMEGA };

    public static MasterTorsionAngleType[] mainAngles() {
        return ProteinTorsionAngleType.MAIN;
    }

    @Override
    public String getLongDisplayName() {
        assert angleTypes.size() > 0;
        return angleTypes.get(0).getLongDisplayName();
    }

    @Override
    public String getShortDisplayName() {
        assert angleTypes.size() > 0;
        return angleTypes.get(0).getShortDisplayName();
    }

    @Override
    public String getExportName() {
        assert angleTypes.size() > 0;
        return angleTypes.get(0).getExportName();
    }
}
