package pl.poznan.put.torsion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.helper.TorsionAnglesHelper;
import pl.poznan.put.nucleic.PseudophasePuckerAngle;
import pl.poznan.put.nucleic.RNATorsionAngle;
import pl.poznan.put.protein.ProteinTorsionAngle;
import pl.poznan.put.torsion.AngleDelta.State;

public class AverageAngle implements TorsionAngle {
    private static AverageAngle PROTEIN_INSTANCE_ALL;
    private static AverageAngle PROTEIN_INSTANCE_MAIN;
    private static AverageAngle RNA_INSTANCE_ALL;
    private static AverageAngle RNA_INSTANCE_MAIN;

    public static AverageAngle getInstanceAllAngles(MoleculeType moleculeType) {
        switch (moleculeType) {
        case PROTEIN:
            if (AverageAngle.PROTEIN_INSTANCE_ALL == null) {
                List<TorsionAngle> angles = new ArrayList<>();
                angles.addAll(Arrays.asList(ProteinTorsionAngle.values()));
                angles.addAll(Arrays.asList(ChiTorsionAngleType.getChiTorsionAngles(MoleculeType.PROTEIN)));
                AverageAngle.PROTEIN_INSTANCE_ALL = new AverageAngle(angles,
                        "MCQ(selected)", "MCQ_SELECTED");
            }
            return AverageAngle.PROTEIN_INSTANCE_ALL;
        case RNA:
            if (AverageAngle.RNA_INSTANCE_ALL == null) {
                List<TorsionAngle> angles = new ArrayList<>();
                angles.addAll(Arrays.asList(RNATorsionAngle.values()));
                angles.addAll(Arrays.asList(ChiTorsionAngleType.getChiTorsionAngles(MoleculeType.RNA)));
                angles.add(PseudophasePuckerAngle.getInstance());
                AverageAngle.RNA_INSTANCE_ALL = new AverageAngle(angles,
                        "MCQ(selected)", "MCQ_SELECTED");
            }
            return AverageAngle.RNA_INSTANCE_ALL;
        case UNKNOWN:
        default:
            return null;
        }
    }

    public static AverageAngle getInstanceMainAngles(MoleculeType moleculeType) {
        switch (moleculeType) {
        case PROTEIN:
            if (AverageAngle.PROTEIN_INSTANCE_MAIN == null) {
                List<TorsionAngle> angles = new ArrayList<>();
                angles.add(ProteinTorsionAngle.PHI);
                angles.add(ProteinTorsionAngle.PSI);
                angles.add(ProteinTorsionAngle.OMEGA);
                AverageAngle.PROTEIN_INSTANCE_MAIN = new AverageAngle(angles);
            }
            return AverageAngle.PROTEIN_INSTANCE_MAIN;
        case RNA:
            if (AverageAngle.RNA_INSTANCE_MAIN == null) {
                List<TorsionAngle> angles = new ArrayList<>();
                angles.add(RNATorsionAngle.ALPHA);
                angles.add(RNATorsionAngle.BETA);
                angles.add(RNATorsionAngle.GAMMA);
                angles.add(RNATorsionAngle.DELTA);
                angles.add(RNATorsionAngle.EPSILON);
                angles.add(RNATorsionAngle.ZETA);
                angles.add(ChiTorsionAngleType.CHI);
                angles.add(PseudophasePuckerAngle.getInstance());
                AverageAngle.RNA_INSTANCE_MAIN = new AverageAngle(angles);
            }
            return AverageAngle.RNA_INSTANCE_MAIN;
        case UNKNOWN:
        default:
            return null;
        }
    }

    private static MoleculeType commonMoleculeType(
            List<TorsionAngle> consideredAngles) {
        MoleculeType type = null;

        for (TorsionAngle angle : consideredAngles) {
            if (type == null) {
                type = angle.getMoleculeType();
            }

            if (type != angle.getMoleculeType()) {
                throw new IllegalArgumentException("Considered angles must "
                        + "all be of the same molecule type!");
            }
        }

        return type;
    }

    private final MoleculeType moleculeType;
    private final String displayName;
    private final String exportName;
    private final List<TorsionAngle> consideredAngles;

    public AverageAngle(List<TorsionAngle> angles) {
        super();

        if (angles == null || angles.size() == 0) {
            throw new IllegalArgumentException("Considered angles list cannot "
                    + "be empty!");
        }

        moleculeType = AverageAngle.commonMoleculeType(angles);
        consideredAngles = new ArrayList<>(angles);

        StringBuilder display = new StringBuilder("MCQ(");
        StringBuilder export = new StringBuilder("MCQ_");

        display.append(angles.get(0).getShortDisplayName());
        export.append(angles.get(0).getExportName());

        for (int i = 1; i < angles.size(); i++) {
            TorsionAngle angle = angles.get(i);
            display.append(", ");
            display.append(angle.getShortDisplayName());
            export.append("_");
            export.append(angle.getExportName());
        }
        display.append(')');

        displayName = display.toString();
        exportName = export.toString();
    }

    private AverageAngle(List<TorsionAngle> angles, String displayName,
            String exportName) {
        super();

        moleculeType = AverageAngle.commonMoleculeType(angles);
        consideredAngles = new ArrayList<>(angles);
        this.displayName = displayName;
        this.exportName = exportName;
    }

    public AngleValue calculateValue(List<AngleValue> angleValues) {
        List<TorsionAngle> angles = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (AngleValue angleValue : angleValues) {
            TorsionAngle angle = angleValue.getAngle();
            double value = angleValue.getValue();

            if (consideredAngles.contains(angle)) {
                angles.add(angle);
                values.add(value);
            }
        }

        if (angles.size() == 0) {
            return AngleValue.getInvalidInstance(this);
        }

        return new AngleValue(new AverageAngle(angles),
                TorsionAnglesHelper.calculateMean(values));
    }

    public AngleDelta calculateDelta(List<AngleDelta> angleDeltas) {
        List<AngleValue> targetValues = new ArrayList<>();
        List<AngleValue> modelValues = new ArrayList<>();
        List<Double> deltas = new ArrayList<>();

        for (AngleDelta tad : angleDeltas) {
            TorsionAngle torsionAngle = tad.getTorsionAngle();
            if (torsionAngle instanceof ChiTorsionAngle) {
                torsionAngle = ((ChiTorsionAngle) torsionAngle).getType();
            }
            if (!consideredAngles.contains(torsionAngle)) {
                continue;
            }

            if (tad.getState() == State.BOTH_VALID) {
                targetValues.add(tad.getTargetValue());
                modelValues.add(tad.getModelValue());
                deltas.add(tad.getDelta());
            } else if (tad.getState() == State.TORSION_TARGET_INVALID) {
                modelValues.add(tad.getModelValue());
            } else if (tad.getState() == State.TORSION_MODEL_INVALID) {
                targetValues.add(tad.getTargetValue());
            }
        }

        AngleValue averageTarget = calculateValue(targetValues);
        AngleValue averageModel = calculateValue(modelValues);
        double mcq = TorsionAnglesHelper.calculateMean(deltas);
        return new AngleDelta(this, averageTarget, averageModel,
                Double.isNaN(mcq) ? State.BOTH_INVALID : State.BOTH_VALID, mcq);
    }

    @Override
    public String getLongDisplayName() {
        return displayName;
    }

    @Override
    public String getShortDisplayName() {
        return displayName;
    }

    @Override
    public String getExportName() {
        return exportName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public MoleculeType getMoleculeType() {
        return moleculeType;
    }

    public List<TorsionAngle> getConsideredAngles() {
        return Collections.unmodifiableList(consideredAngles);
    }
}