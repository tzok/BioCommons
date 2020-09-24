package pl.poznan.put.torsion;

import org.immutables.value.Value;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.RangeDifference;
import pl.poznan.put.utility.AngleFormat;

@Value.Immutable
public abstract class TorsionAngleDelta {
  public static TorsionAngleDelta bothInvalidInstance(final MasterTorsionAngleType masterType) {
    return ImmutableTorsionAngleDelta.of(
        masterType,
        State.BOTH_INVALID,
        ImmutableAngle.of(Double.NaN),
        ImmutableAngle.of(Double.NaN),
        ImmutableAngle.of(Double.NaN),
        RangeDifference.INVALID);
  }

  public static TorsionAngleDelta subtractTorsionAngleValues(
      final MasterTorsionAngleType masterType, final Angle target, final Angle model) {
    Angle delta = ImmutableAngle.of(Double.NaN);
    RangeDifference rangeDifference = RangeDifference.INVALID;
    final State state;

    if (!target.isValid() && !model.isValid()) {
      state = State.BOTH_INVALID;
    } else if (!target.isValid()) {
      state = State.TARGET_INVALID;
    } else if (!model.isValid()) {
      state = State.MODEL_INVALID;
    } else {
      state = State.BOTH_VALID;
      delta = target.subtract(model);

      final Range targetRange = masterType.range(target);
      final Range modelRange = masterType.range(model);
      rangeDifference = targetRange.compare(modelRange);
    }

    return ImmutableTorsionAngleDelta.of(masterType, state, target, model, delta, rangeDifference);
  }

  @Value.Parameter(order = 1)
  public abstract MasterTorsionAngleType masterTorsionAngleType();

  @Value.Parameter(order = 2)
  public abstract State state();

  @Value.Parameter(order = 3)
  public abstract Angle target();

  @Value.Parameter(order = 4)
  public abstract Angle model();

  @Value.Parameter(order = 5)
  public abstract Angle delta();

  @Value.Parameter(order = 6)
  public abstract RangeDifference rangeDifference();

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
    switch (state()) {
      case BOTH_INVALID:
        return "Missing atoms both in target and model";
      case BOTH_VALID:
        return isDisplayable
            ? AngleFormat.degreesRoundedToHundredth(delta().radians())
            : AngleFormat.degrees(delta().radians());
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
