package pl.poznan.put.torsion.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.poznan.put.common.MoleculeType;

public class AverageTorsionAngleType extends TorsionAngleType {
    // private static AverageTorsionAngleType proteinAll;
    // private static AverageTorsionAngleType proteinMain;
    // private static AverageTorsionAngleType rnaAll;
    // private static AverageTorsionAngleType rnaMain;
    //
    // public static AverageTorsionAngleType getInstanceAllAngles(MoleculeType
    // moleculeType) {
    // switch (moleculeType) {
    // case PROTEIN:
    // if (AverageTorsionAngleType.proteinAll == null) {
    // List<TorsionAngleType> angles = new ArrayList<TorsionAngleType>();
    // angles.addAll(Arrays.asList(ProteinTorsionAngle.values()));
    // angles.addAll(Arrays.asList(ChiTorsionAngleType.getChiTorsionAngles(MoleculeType.PROTEIN)));
    // AverageTorsionAngleType.proteinAll = new AverageTorsionAngleType(angles,
    // "MCQ(selected)", "MCQ_SELECTED");
    // }
    // return AverageTorsionAngleType.proteinAll;
    // case RNA:
    // if (AverageTorsionAngleType.rnaAll == null) {
    // List<TorsionAngleType> angles = new ArrayList<TorsionAngleType>();
    // angles.addAll(Arrays.asList(RNATorsionAngle.values()));
    // angles.addAll(Arrays.asList(ChiTorsionAngleType.getChiTorsionAngles(MoleculeType.RNA)));
    // angles.add(PseudophasePucker.getInstance());
    // AverageTorsionAngleType.rnaAll = new AverageTorsionAngleType(angles,
    // "MCQ(selected)", "MCQ_SELECTED");
    // }
    // return AverageTorsionAngleType.rnaAll;
    // case UNKNOWN:
    // default:
    // return null;
    // }
    // }
    //
    // public static AverageTorsionAngleType getInstanceMainAngles(MoleculeType
    // moleculeType) {
    // switch (moleculeType) {
    // case PROTEIN:
    // if (AverageTorsionAngleType.proteinMain == null) {
    // List<TorsionAngleType> angles = new ArrayList<TorsionAngleType>();
    // angles.add(ProteinTorsionAngle.PHI);
    // angles.add(ProteinTorsionAngle.PSI);
    // angles.add(ProteinTorsionAngle.OMEGA);
    // AverageTorsionAngleType.proteinMain = new
    // AverageTorsionAngleType(angles);
    // }
    // return AverageTorsionAngleType.proteinMain;
    // case RNA:
    // if (AverageTorsionAngleType.rnaMain == null) {
    // List<TorsionAngleType> angles = new ArrayList<TorsionAngleType>();
    // angles.add(RNATorsionAngle.ALPHA);
    // angles.add(RNATorsionAngle.BETA);
    // angles.add(RNATorsionAngle.GAMMA);
    // angles.add(RNATorsionAngle.DELTA);
    // angles.add(RNATorsionAngle.EPSILON);
    // angles.add(RNATorsionAngle.ZETA);
    // angles.add(ChiTorsionAngleType.CHI);
    // angles.add(PseudophasePucker.getInstance());
    // AverageTorsionAngleType.rnaMain = new AverageTorsionAngleType(angles);
    // }
    // return AverageTorsionAngleType.rnaMain;
    // case UNKNOWN:
    // default:
    // return null;
    // }
    // }
    //
    // private static MoleculeType commonMoleculeType(List<TorsionAngleType>
    // consideredAngles) {
    // MoleculeType type = null;
    //
    // for (TorsionAngleType angle : consideredAngles) {
    // if (type == null) {
    // type = angle.getMoleculeType();
    // }
    //
    // if (type != angle.getMoleculeType()) {
    // throw new IllegalArgumentException("Considered angles must " +
    // "all be of the same molecule type!");
    // }
    // }
    //
    // return type;
    // }

    private final String displayName;
    private final String exportName;
    private final List<TorsionAngleType> consideredAngles;

    private AverageTorsionAngleType(MoleculeType moleculeType, List<TorsionAngleType> angles) {
        super(moleculeType);

        if (angles == null || angles.size() == 0) {
            throw new IllegalArgumentException("Considered angles list cannot be empty!");
        }

        consideredAngles = new ArrayList<TorsionAngleType>(angles);

        StringBuilder display = new StringBuilder("MCQ(");
        StringBuilder export = new StringBuilder("MCQ_");

        display.append(angles.get(0).getShortDisplayName());
        export.append(angles.get(0).getExportName());

        for (int i = 1; i < angles.size(); i++) {
            TorsionAngleType angle = angles.get(i);
            display.append(", ");
            display.append(angle.getShortDisplayName());
            export.append("_");
            export.append(angle.getExportName());
        }
        display.append(')');

        displayName = display.toString();
        exportName = export.toString();
    }

    private AverageTorsionAngleType(MoleculeType moleculeType, List<TorsionAngleType> angles, String displayName, String exportName) {
        super(moleculeType);

        consideredAngles = new ArrayList<TorsionAngleType>(angles);
        this.displayName = displayName;
        this.exportName = exportName;
    }

    // public TorsionAngleValue calculateValue(List<TorsionAngleValue>
    // angleValues) {
    // List<TorsionAngleType> angles = new ArrayList<TorsionAngleType>();
    // List<Double> values = new ArrayList<Double>();
    //
    // for (TorsionAngleValue angleValue : angleValues) {
    // TorsionAngleType angle = angleValue.getAngle();
    // double value = angleValue.getValue();
    //
    // if (consideredAngles.contains(angle)) {
    // angles.add(angle);
    // values.add(value);
    // }
    // }
    //
    // if (angles.size() == 0) {
    // return TorsionAngleValue.getInvalidInstance(this);
    // }
    //
    // return new TorsionAngleValue(new AverageTorsionAngleType(angles),
    // TorsionAnglesHelper.calculateMean(values));
    // }
    //
    // public AngleDelta calculateDelta(List<AngleDelta> angleDeltas) {
    // List<TorsionAngleValue> targetValues = new
    // ArrayList<TorsionAngleValue>();
    // List<TorsionAngleValue> modelValues = new ArrayList<TorsionAngleValue>();
    // List<Double> deltas = new ArrayList<Double>();
    //
    // for (AngleDelta tad : angleDeltas) {
    // TorsionAngleType torsionAngle = tad.getTorsionAngle();
    // if (torsionAngle instanceof ChiTorsionAngle) {
    // torsionAngle = ((ChiTorsionAngle) torsionAngle).getType();
    // }
    // if (!consideredAngles.contains(torsionAngle)) {
    // continue;
    // }
    //
    // if (tad.getState() == State.BOTH_VALID) {
    // targetValues.add(tad.getTargetValue());
    // modelValues.add(tad.getModelValue());
    // deltas.add(tad.getDelta());
    // } else if (tad.getState() == State.TORSION_TARGET_INVALID) {
    // modelValues.add(tad.getModelValue());
    // } else if (tad.getState() == State.TORSION_MODEL_INVALID) {
    // targetValues.add(tad.getTargetValue());
    // }
    // }
    //
    // TorsionAngleValue averageTarget = calculateValue(targetValues);
    // TorsionAngleValue averageModel = calculateValue(modelValues);
    // double mcq = TorsionAnglesHelper.calculateMean(deltas);
    // return new AngleDelta(this, averageTarget, averageModel,
    // Double.isNaN(mcq) ? State.BOTH_INVALID : State.BOTH_VALID, mcq);
    // }

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

    public List<TorsionAngleType> getConsideredAngles() {
        return Collections.unmodifiableList(consideredAngles);
    }
}