package pl.poznan.put.torsion;

import pl.poznan.put.circular.Angle;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.RangeDifference;
import pl.poznan.put.utility.AngleFormat;

public class TorsionAngleDelta {
  private final MasterTorsionAngleType masterTorsionAngleType;
  private final State state;
  private final Angle target;
  private final Angle model;
  private final Angle delta;
  private final RangeDifference rangeDifference;

  public TorsionAngleDelta(
      final MasterTorsionAngleType masterTorsionAngleType,
      final State state,
      final Angle target,
      final Angle model,
      final Angle delta,
      final RangeDifference rangeDifference) {
    super();
    this.masterTorsionAngleType = masterTorsionAngleType;
    this.state = state;
    this.target = target;
    this.model = model;
    this.delta = delta;
    this.rangeDifference = rangeDifference;
  }

  public static TorsionAngleDelta bothInvalidInstance(final MasterTorsionAngleType masterType) {
    return new TorsionAngleDelta(
        masterType,
        State.BOTH_INVALID,
        Angle.invalidInstance(),
        Angle.invalidInstance(),
        Angle.invalidInstance(),
        RangeDifference.INVALID);
  }

  public static TorsionAngleDelta subtractTorsionAngleValues(
      final MasterTorsionAngleType masterType,
      final TorsionAngleValue targetValue,
      final TorsionAngleValue modelValue) {
    Angle delta = Angle.invalidInstance();
    RangeDifference rangeDifference = RangeDifference.INVALID;
    final Angle target = targetValue.getValue();
    final Angle model = modelValue.getValue();
    final State state;

    if (!target.isValid() && !model.isValid()) {
      state = State.BOTH_INVALID;
    } else if (!target.isValid() && model.isValid()) {
      state = State.TARGET_INVALID;
    } else if (target.isValid() && !model.isValid()) {
      state = State.MODEL_INVALID;
    } else {
      state = State.BOTH_VALID;
      delta = target.subtract(model);

      final Range targetRange = masterType.getRange(target);
      final Range modelRange = masterType.getRange(model);
      rangeDifference = targetRange.compare(modelRange);
    }

    return new TorsionAngleDelta(masterType, state, target, model, delta, rangeDifference);
  }

  public Angle getTarget() {
    return target;
  }

  public Angle getModel() {
    return model;
  }

  public final State getState() {
    return state;
  }

  public final Angle getDelta() {
    return delta;
  }

  public final RangeDifference getRangeDifference() {
    return rangeDifference;
  }

  public final MasterTorsionAngleType getMasterTorsionAngleType() {
    return masterTorsionAngleType;
  }

  @Override
  public final String toString() {
    return String.format(
        "TorsionAngleDelta [state=%s, delta=%s, " + "rangeDifference=%s]",
        state, delta, rangeDifference);
  }

  /**
   * Represent numeric value in a way external tools understand (dot as fraction point and no
   * UNICODE_DEGREE sign).
   *
   * @return String representation of this delta object understandable by external tools.
   */
  public final String toExportString() {
    return toString(false);
  }

  public final String toString(final boolean isDisplayable) {
    switch (state) {
      case BOTH_INVALID:
        return isDisplayable ? "" : null;
      case BOTH_VALID:
        return isDisplayable
            ? AngleFormat.degreesRoundedToHundredth(delta.getRadians())
            : AngleFormat.degrees(delta.getRadians());
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
