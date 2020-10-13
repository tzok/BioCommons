package pl.poznan.put.circular.samples;

import org.apache.commons.math3.util.FastMath;
import org.immutables.value.Value;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;

import java.util.Collection;

/** A quantitative measure of a trigonometric sample shape. */
@Value.Immutable
public abstract class TrigonometricMoment {
  /**
   * Computes an uncentered moment i.e. from the data points not relative to any specific point.
   *
   * @param data A collection of angular values.
   * @param p A p-th moment to be calculated.
   * @return The p-th uncentered trigonometric moment of the sample.
   */
  public static TrigonometricMoment computeUncentered(final Collection<Angle> data, final int p) {
    return TrigonometricMoment.compute(data, p, ImmutableAngle.of(0.0));
  }

  /**
   * Computes a centered moment i.e. relative to the mean value in the sample.
   *
   * @param data A collection of angular values.
   * @param p A p-th moment to be calculated.
   * @param theta The mean value in the sample
   * @return The p-th centered trigonometric moment of the sample.
   */
  public static TrigonometricMoment computeCentered(
      final Collection<Angle> data, final int p, final Angle theta) {
    return TrigonometricMoment.compute(data, p, theta);
  }

  private static TrigonometricMoment compute(
      final Collection<Angle> data, final int p, final Angle theta) {
    assert !data.isEmpty();

    double c = 0.0;
    double s = 0.0;

    for (final Angle vector : data) {
      final double radians = vector.orderedSubtract(theta).radians();
      c += FastMath.cos(p * radians);
      s += FastMath.sin(p * radians);
    }

    c /= data.size();
    s /= data.size();

    final double rho = FastMath.sqrt(FastMath.pow(c, 2) + FastMath.pow(s, 2));
    final double mi = FastMath.atan2(s, c);
    return ImmutableTrigonometricMoment.of(ImmutableAngle.of(mi), rho);
  }

  /** @return The mean direction. */
  @Value.Parameter(order = 1)
  public abstract Angle meanDirection();

  /** @return The length of the vector representing the mean direction. */
  @Value.Parameter(order = 2)
  public abstract double meanResultantLength();
}
