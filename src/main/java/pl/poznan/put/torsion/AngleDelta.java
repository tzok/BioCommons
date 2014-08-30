package pl.poznan.put.torsion;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.helper.TorsionAnglesHelper;
import pl.poznan.put.utility.CommonNumberFormat;

public class AngleDelta {
    public enum State {
        TORSION_TARGET_INVALID, TORSION_MODEL_INVALID, BOTH_INVALID,
        BOTH_VALID, DIFFERENT_CHI;
    }

    private final TorsionAngle torsionAngle;
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

    public AngleValue getTargetValue() {
        return targetValue;
    }

    public AngleValue getModelValue() {
        return modelValue;
    }

    public State getState() {
        return state;
    }

    public double getDelta() {
        return delta;
    }

    public TorsionAngle getTorsionAngle() {
        return torsionAngle;
    }

    @Override
    public String toString() {
        return "AngleDelta [state=" + state + ", targetValue=" + targetValue
                + ", modelValue=" + modelValue + ", delta=" + delta + "]";
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
            result += Unicode.DEGREE;
        }

        return result;
    }

    AngleDelta(TorsionAngle torsionAngle, AngleValue targetValue,
            AngleValue modelValue, State state, double delta) {
        super();
        this.torsionAngle = torsionAngle;
        this.targetValue = targetValue;
        this.modelValue = modelValue;
        this.state = state;
        this.delta = delta;
    }

    AngleDelta(AngleValue targetValue, AngleValue modelValue, State state,
            double delta) {
        super();
        assert targetValue.getAngle().equals(modelValue.getAngle());
        this.torsionAngle = targetValue.getAngle();
        this.targetValue = targetValue;
        this.modelValue = modelValue;
        this.state = state;
        this.delta = delta;
    }
}
