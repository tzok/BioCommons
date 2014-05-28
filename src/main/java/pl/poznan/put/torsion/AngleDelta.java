package pl.poznan.put.torsion;

import java.util.ArrayList;
import java.util.List;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.helper.CommonNumberFormat;
import pl.poznan.put.helper.Constants;
import pl.poznan.put.helper.TorsionAnglesHelper;

public class AngleDelta {
    public enum State {
        TORSION_TARGET_INVALID, TORSION_MODEL_INVALID, BOTH_INVALID,
        BOTH_VALID, DIFFERENT_CHI;
    }

    private final AngleValue targetValue;
    private final AngleValue modelValue;
    private final State state;
    private final double delta;

    public static AngleDelta calculate(AngleValue target, AngleValue model) {
        State state;
        double delta = Double.NaN;

        if (!target.isValid() && !model.isValid()) {
            state = State.BOTH_INVALID;
        } else if (!target.isValid() && model.isValid()) {
            state = State.TORSION_TARGET_INVALID;
        } else if (target.isValid() && !model.isValid()) {
            state = State.TORSION_MODEL_INVALID;
        } else {
            state = State.BOTH_VALID;
            delta = TorsionAnglesHelper.subtractTorsions(target.getValue(),
                    model.getValue());
        }

        return new AngleDelta(target, model, state, delta);
    }

    public static AngleDelta calculateChiDelta(AngleValue target,
            AngleValue model, boolean matchChiByType) {
        TorsionAngle torL = target.getAngle();
        TorsionAngle torR = model.getAngle();

        if (!matchChiByType && torL.getMoleculeType() == MoleculeType.RNA
                && !torL.equals(torR)) {
            return new AngleDelta(target, model, State.DIFFERENT_CHI,
                    Double.NaN);
        }

        return AngleDelta.calculate(target, model);
    }

    public static AngleDelta calculateAverage(MoleculeType moleculeType,
            List<AngleDelta> angleDeltas) {
        List<AngleValue> targetValues = new ArrayList<AngleValue>();
        List<AngleValue> modelValues = new ArrayList<AngleValue>();
        List<Double> deltas = new ArrayList<Double>();

        for (AngleDelta tad : angleDeltas) {
            TorsionAngle torsionAngle = tad.getTorsionAngle();
            if (torsionAngle.getMoleculeType() != moleculeType) {
                continue;
            }

            if (tad.state == State.BOTH_VALID) {
                targetValues.add(tad.targetValue);
                modelValues.add(tad.modelValue);
                deltas.add(tad.delta);
            } else if (tad.state == State.TORSION_TARGET_INVALID) {
                modelValues.add(tad.modelValue);
            } else if (tad.state == State.TORSION_MODEL_INVALID) {
                targetValues.add(tad.targetValue);
            }
        }

        double mcq = TorsionAnglesHelper.calculateMean(deltas);
        return new AngleDelta(
                AverageAngle.calculate(moleculeType, targetValues),
                AverageAngle.calculate(moleculeType, modelValues),
                Double.isNaN(mcq) ? State.BOTH_INVALID : State.BOTH_VALID, mcq);
    }

    public AngleValue getTargetValue() {
        return targetValue;
    }

    public AngleValue getmodelValue() {
        return modelValue;
    }

    public State getState() {
        return state;
    }

    public double getDelta() {
        return delta;
    }

    public TorsionAngle getTorsionAngle() {
        return targetValue.getAngle();
    }

    @Override
    public String toString() {
        return "TorsionAngleDelta [torsionAngleValueLeft=" + targetValue
                + ", torsionAngleValueRight=" + modelValue + ", state=" + state
                + ", delta=" + delta + "]";
    }

    /**
     * Represent numeric value in a way external tools understand (dot as
     * fraction point and no UNICODE_DEGREE sign).
     * 
     * @return String representation of this delta object understandable by
     *         external tools.
     */
    public String toExportString() {
        switch (state) {
        case BOTH_INVALID:
            return null;
        case BOTH_VALID:
            return CommonNumberFormat.formatDouble(Math.toDegrees(delta));
        case TORSION_TARGET_INVALID:
            return "Missing atoms in target";
        case TORSION_MODEL_INVALID:
            return "Missing atoms in model";
        case DIFFERENT_CHI:
            return "Purine/pyrimidine mismatch";
        default:
            return "Error";
        }
    }

    /**
     * Represent object as a String which will be displayed to user in the GUI.
     * 
     * @return String representation of object to be shown in the GUI.
     */
    public String toDisplayString() {
        String result = toExportString();

        if (state == State.BOTH_INVALID) {
            result = "";
        } else if (state == State.BOTH_VALID) {
            result += Constants.UNICODE_DEGREE;
        }

        return result;
    }

    private AngleDelta(AngleValue torsion1, AngleValue torsion2, State state,
            double delta) {
        super();
        this.targetValue = torsion1;
        this.modelValue = torsion2;
        this.state = state;
        this.delta = delta;
    }
}
