package pl.poznan.put.circular.samples;

import org.apache.commons.math3.util.FastMath;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;

import java.util.Collection;

/** A quantitative measure of a trigonometric sample shape. */
public final class TrigonometricMoment {
  private final Angle meanDirection;
  private final double meanResultantLength;

  private TrigonometricMoment(final Angle meanDirection, final double meanResultantLength) {
    super();
    this.meanDirection = meanDirection;
    this.meanResultantLength = meanResultantLength;
  }

  /**
   * Compute uncentered moment i.e. from the data points not relative to any specific point.
   *
   * @param data A collection of angular values.
   * @param p A p-th moment to be calculated.
   * @return The p-th uncentered trigonometric moment of the sample.
   */
  public static TrigonometricMoment computeUncentered(
      final Collection<? extends Angle> data, final int p) {
    return TrigonometricMoment.compute(data, p, ImmutableAngle.of(0.0));
  }

  /**
   * Compute centered moment i.e. relative to the mean value in the sample.
   *
   * @param data A collection of angular values.
   * @param p A p-th moment to be calculated.
   * @param theta The mean value in the sample
   * @return The p-th centered trigonometric moment of the sample.
   */
  public static TrigonometricMoment computeCentered(
      final Collection<? extends Angle> data, final int p, final Angle theta) {
    return TrigonometricMoment.compute(data, p, theta);
  }

  private static TrigonometricMoment compute(
      final Collection<? extends Angle> data, final int p, final Angle theta) {
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
    return new TrigonometricMoment(ImmutableAngle.of(mi), rho);
  }

  public Angle getMeanDirection() {
    return meanDirection;
  }

  public double getMeanResultantLength() {
    return meanResultantLength;
  }

  @Override
  public String toString() {
    return "TrigonometricMoment [meanDirection="
        + meanDirection
        + ", meanResultantLength="
        + meanResultantLength
        + ']';
  }
}
