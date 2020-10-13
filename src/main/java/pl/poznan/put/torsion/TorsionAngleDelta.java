package pl.poznan.put.torsion;

import org.immutables.value.Value;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.torsion.range.RangeDifference;
import pl.poznan.put.utility.AngleFormat;

/** A result of subtracting of two torsion angles. */
@Value.Immutable
public abstract class TorsionAngleDelta implements DisplayableExportable {
  /**
   * Creates an instance of torsion angle delta, where both input angles were invalid.
   *
   * @param masterType The type of torsion angle.
   * @return An instance indicating that both input angles were invalid.
   */
  public static TorsionAngleDelta bothInvalidInstance(final MasterTorsionAngleType masterType) {
    return ImmutableTorsionAngleDelta.of(
        masterType, State.BOTH_INVALID, ImmutableAngle.of(Double.NaN), RangeDifference.INVALID);
  }

  /**
   * Subtracts torsion angle values and creates an instance of this class.
   *
   * @param masterType The type of torsion angle.
   * @param target The value of the first torsion angle.
   * @param model The value of the second torsion angle.
   * @return An instance of this class containing the result of subtraction.
   */
  public static TorsionAngleDelta subtractTorsionAngleValues(
      final MasterTorsionAngleType masterType, final Angle target, final Angle model) {
    final State state = State.fromAngles(target, model);
    final Angle delta =
        state == State.BOTH_VALID ? target.subtract(model) : ImmutableAngle.of(Double.NaN);
    final RangeDifference rangeDifference =
        state == State.BOTH_VALID
            ? masterType.range(target).compare(masterType.range(model))
            : RangeDifference.INVALID;
    return ImmutableTorsionAngleDelta.of(masterType, state, delta, rangeDifference);
  }

  /** @return The type of torsion angle. */
  @Value.Parameter(order = 1)
  public abstract MasterTorsionAngleType angleType();

  /** @return The state of comparison depending on whether the inputs were valid or not. */
  @Value.Parameter(order = 2)
  public abstract State state();

  /** @return The actual result of subtraction. */
  @Value.Parameter(order = 3)
  public abstract Angle delta();

  /** @return The difference in terms of ranges the angles belong to. */
  @Value.Parameter(order = 4)
  public abstract RangeDifference rangeDifference();

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

  @Override
  public final String shortDisplayName() {
    return toString(true);
  }

  @Override
  public final String longDisplayName() {
    return shortDisplayName();
  }

  @Override
  public final String exportName() {
    return toString(false);
  }

  /** A state of comparison of two torsion angle values. */
  public enum State {
    TARGET_INVALID,
    MODEL_INVALID,
    BOTH_INVALID,
    BOTH_VALID;

    static State fromAngles(final Angle target, final Angle model) {
      if (!target.isValid() && !model.isValid()) {
        return State.BOTH_INVALID;
      }
      if (!target.isValid()) {
        return State.TARGET_INVALID;
      }
      if (!model.isValid()) {
        return State.MODEL_INVALID;
      }
      return State.BOTH_VALID;
    }
  }
}
