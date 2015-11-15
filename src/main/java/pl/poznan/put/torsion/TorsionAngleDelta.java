package pl.poznan.put.torsion;

import pl.poznan.put.circular.Angle;
import pl.poznan.put.utility.AngleFormat;

public class TorsionAngleDelta {
    public enum State {
        TARGET_INVALID, MODEL_INVALID, BOTH_INVALID, BOTH_VALID
    }

    public static TorsionAngleDelta bothInvalidInstance(
            MasterTorsionAngleType masterType) {
        return new TorsionAngleDelta(masterType, State.BOTH_INVALID, Angle.invalidInstance());
    }

    public static TorsionAngleDelta subtractTorsionAngleValues(
            MasterTorsionAngleType masterType, TorsionAngleValue targetValue,
            TorsionAngleValue modelValue) {
        State state;
        Angle delta = Angle.invalidInstance();

        if (!targetValue.isValid() && !modelValue.isValid()) {
            state = State.BOTH_INVALID;
        } else if (!targetValue.isValid() && modelValue.isValid()) {
            state = State.TARGET_INVALID;
        } else if (targetValue.isValid() && !modelValue.isValid()) {
            state = State.MODEL_INVALID;
        } else {
            state = State.BOTH_VALID;
            delta = targetValue.getValue().subtract(modelValue.getValue());
        }

        return new TorsionAngleDelta(masterType, state, delta);
    }

    private final MasterTorsionAngleType masterTorsionAngleType;
    private final State state;
    private final Angle delta;

    public TorsionAngleDelta(MasterTorsionAngleType masterTorsionAngleType,
            State state, Angle delta) {
        super();
        this.masterTorsionAngleType = masterTorsionAngleType;
        this.state = state;
        this.delta = delta;
    }

    public State getState() {
        return state;
    }

    public Angle getDelta() {
        return delta;
    }

    public MasterTorsionAngleType getMasterTorsionAngleType() {
        return masterTorsionAngleType;
    }

    @Override
    public String toString() {
        return "AngleDelta [state=" + state + ", delta=" + delta + "]";
    }

    /**
     * Represent numeric value in a way external tools understand (dot as
     * fraction point and no UNICODE_DEGREE sign).
     *
     * @return String representation of this delta object understandable by
     *         external tools.
     */
    public String toExportString() {
        return toString(false);
    }

    /**
     * Represent object as a String which will be displayed to user in the GUI.
     *
     * @return String representation of object to be shown in the GUI.
     */
    public String toDisplayString() {
        return toString(true);
    }

    public String toString(boolean isDisplayable) {
        switch (state) {
        case BOTH_INVALID:
            return isDisplayable ? "" : null;
        case BOTH_VALID:
            return isDisplayable ? AngleFormat.formatDisplayShort(delta.getRadians()) : AngleFormat.formatExport(delta.getRadians());
        case TARGET_INVALID:
            return "Missing atoms in target";
        case MODEL_INVALID:
            return "Missing atoms in model";
        default:
            return "Error";
        }
    }
}
