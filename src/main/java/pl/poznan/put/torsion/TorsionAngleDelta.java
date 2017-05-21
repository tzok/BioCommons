package pl.poznan.put.torsion;

import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.Range;
import pl.poznan.put.circular.enums.RangeDifference;
import pl.poznan.put.utility.AngleFormat;

public class TorsionAngleDelta {
    private final MasterTorsionAngleType masterTorsionAngleType;
    private final State state;
    private final Angle delta;
    private final RangeDifference rangeDifference;

    public TorsionAngleDelta(
            final MasterTorsionAngleType masterTorsionAngleType,
            final State state, final Angle delta,
            final RangeDifference rangeDifference) {
        super();
        this.masterTorsionAngleType = masterTorsionAngleType;
        this.state = state;
        this.delta = delta;
        this.rangeDifference = rangeDifference;
    }

    public static TorsionAngleDelta bothInvalidInstance(
            final MasterTorsionAngleType masterType) {
        return new TorsionAngleDelta(masterType, State.BOTH_INVALID,
                                     Angle.invalidInstance(),
                                     RangeDifference.INVALID);
    }

    public static TorsionAngleDelta subtractTorsionAngleValues(
            final MasterTorsionAngleType masterType,
            final TorsionAngleValue targetValue,
            final TorsionAngleValue modelValue) {
        final State state;
        Angle delta = Angle.invalidInstance();
        RangeDifference rangeDifference = RangeDifference.INVALID;

        if (!targetValue.isValid() && !modelValue.isValid()) {
            state = State.BOTH_INVALID;
        } else if (!targetValue.isValid() && modelValue.isValid()) {
            state = State.TARGET_INVALID;
        } else if (targetValue.isValid() && !modelValue.isValid()) {
            state = State.MODEL_INVALID;
        } else {
            final Angle target = targetValue.getValue();
            final Angle model = modelValue.getValue();
            delta = target.subtract(model);
            rangeDifference =
                    Range.fromAngle(target).difference(Range.fromAngle(model));
            state = State.BOTH_VALID;
        }

        return new TorsionAngleDelta(masterType, state, delta, rangeDifference);
    }

    public final State getState() {
        return state;
    }

    public final Angle getDelta() {
        return delta;
    }

    public RangeDifference getRangeDifference() {
        return rangeDifference;
    }

    public final MasterTorsionAngleType getMasterTorsionAngleType() {
        return masterTorsionAngleType;
    }

    @Override
    public final String toString() {
        return "AngleDelta [state=" + state + ", delta=" + delta
               + ", rangeDifference=" + rangeDifference + ']';
    }

    /**
     * Represent numeric value in a way external tools understand (dot as
     * fraction point and no UNICODE_DEGREE sign).
     *
     * @return String representation of this delta object understandable by
     * external tools.
     */
    public final String toExportString() {
        return toString(false);
    }

    public final String toString(final boolean isDisplayable) {
        switch (state) {
            case BOTH_INVALID:
                return isDisplayable ? "" : null;
            case BOTH_VALID:
                return isDisplayable ? AngleFormat
                        .formatDisplayShort(delta.getRadians()) : AngleFormat
                               .formatExport(delta.getRadians());
            case TARGET_INVALID:
                return "Missing atoms in target";
            case MODEL_INVALID:
                return "Missing atoms in model";
            default:
                return "Error";
        }
    }

    /**
     * Represent object as a String which will be displayed to user in the GUI.
     *
     * @return String representation of object to be shown in the GUI.
     */
    public final String toDisplayString() {
        return toString(true);
    }

    public enum State {
        TARGET_INVALID,
        MODEL_INVALID,
        BOTH_INVALID,
        BOTH_VALID
    }
}
