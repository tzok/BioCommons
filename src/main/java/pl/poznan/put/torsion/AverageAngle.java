package pl.poznan.put.torsion;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.helper.TorsionAnglesHelper;

public class AverageAngle implements TorsionAngle {
    public static AngleValue calculate(MoleculeType moleculeType,
            List<AngleValue> angleValues) {
        List<Double> angles = new ArrayList<Double>();
        for (AngleValue angleValue : angleValues) {
            angles.add(angleValue.getValue());
        }
        return new AngleValue(AverageAngle.getInstance(moleculeType),
                TorsionAnglesHelper.calculateMean(angles));
    }

    private static final AverageAngle RNA_INSTANCE = new AverageAngle(
            MoleculeType.RNA);
    private static final AverageAngle PROTEIN_INSTANCE = new AverageAngle(
            MoleculeType.PROTEIN);

    public static AverageAngle getInstance(MoleculeType moleculeType) {
        switch (moleculeType) {
        case PROTEIN:
            return AverageAngle.PROTEIN_INSTANCE;
        case RNA:
            return AverageAngle.RNA_INSTANCE;
        case UNKNOWN:
            return null;
        default:
            return null;
        }
    }

    private final MoleculeType moleculeType;

    @Override
    public String getDisplayName() {
        return "Average";
    }

    @Override
    public String getExportName() {
        return "Average";
    }

    @Override
    public String toString() {
        return "Average";
    }

    @Override
    public MoleculeType getMoleculeType() {
        return moleculeType;
    }

    private AverageAngle(MoleculeType moleculeType) {
        this.moleculeType = moleculeType;
    }
}