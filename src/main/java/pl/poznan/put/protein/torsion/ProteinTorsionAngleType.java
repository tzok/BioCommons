package pl.poznan.put.protein.torsion;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.AverageTorsionAngleType;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;

public enum ProteinTorsionAngleType implements MasterTorsionAngleType {
    PHI(Phi.getInstance()),
    PSI(Psi.getInstance()),
    OMEGA(Omega.getInstance()),
    CALPHA(Calpha.getInstance()),
    CHI1(Chi1.getInstances()),
    CHI2(Chi2.getInstances()),
    CHI3(Chi3.getInstances()),
    CHI4(Chi4.getInstances()),
    CHI5(Chi5.getInstances());

    private final List<TorsionAngleType> angleTypes;

    private ProteinTorsionAngleType(TorsionAngleType... angleTypes) {
        this.angleTypes = Arrays.asList(angleTypes);
    }

    @Override
    public Collection<? extends TorsionAngleType> getAngleTypes() {
        return angleTypes;
    }

    private static final MasterTorsionAngleType[] MAIN = new MasterTorsionAngleType[] { PHI, PSI, OMEGA };
    private static final AverageTorsionAngleType AVERAGE_TORSION_INSTANCE = new AverageTorsionAngleType(MoleculeType.PROTEIN, ProteinTorsionAngleType.MAIN);

    public static MasterTorsionAngleType[] mainAngles() {
        return ProteinTorsionAngleType.MAIN;
    }

    public static AverageTorsionAngleType getAverageOverMainAngles() {
        return ProteinTorsionAngleType.AVERAGE_TORSION_INSTANCE;
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
