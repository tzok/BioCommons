package pl.poznan.put.common;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.helper.TorsionAnglesHelper;

public class AverageAngle implements TorsionAngle {
    public static TorsionAngleValue calculate(MoleculeType moleculeType,
            List<TorsionAngleValue> values) {
        List<Double> angles = new ArrayList<Double>();

        for (TorsionAngleValue tav : values) {
            angles.add(tav.getValue());
        }

        return new TorsionAngleValue(AverageAngle.getInstance(moleculeType),
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
    public MoleculeType getMoleculeType() {
        return moleculeType;
    }

    @Override
    public String toString() {
        return "Average";
    }

    private AverageAngle(MoleculeType moleculeType) {
        this.moleculeType = moleculeType;
    }
}