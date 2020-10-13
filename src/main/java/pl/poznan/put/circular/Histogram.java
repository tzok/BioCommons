package pl.poznan.put.circular;

import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** A collection of angular observations put into bins of specified width. */
@Value.Immutable
public abstract class Histogram {
  /** @return A collection of angular data. */
  @Value.Parameter(order = 1)
  public abstract Collection<Angle> data();

  /** @return The width of bin in range [0, pi). */
  @Value.Parameter(order = 2)
  public abstract double binWidth();

  /**
   * Finds the largest bin and calculate its relative size.
   *
   * @return Value in range [0; 1] describing relative size of the largest bin.
   */
  public final double largestBinSize() {
    double maxFrequency = Double.NEGATIVE_INFINITY;
    for (double d = 0.0; d < MathUtils.TWO_PI; d += binWidth()) {
      final double frequency = (double) findBin(d).size() / data().size();
      maxFrequency = FastMath.max(frequency, maxFrequency);
    }
    return maxFrequency;
  }

  /**
   * Finds a bin which starts at a given point.
   *
   * @param radiansStart Value in radians which describes bin starting point (precision 1.0e-3).
   * @return A collection of circular values in the found bin.
   */
  public final Collection<Angle> findBin(final double radiansStart) {
    return bins().stream()
        .filter(bin -> Precision.equals(bin.radiansStart(), radiansStart, 1.0e-3))
        .findFirst()
        .map(Bin::data)
        .orElse(Collections.emptyList());
  }

  @Value.Lazy
  protected Collection<Bin> bins() {
    final Collection<Bin> bins = new ArrayList<>();

    for (double radiansStart = 0.0; radiansStart < MathUtils.TWO_PI; radiansStart += binWidth()) {
      final List<Angle> binData = new ArrayList<>();

      for (final Angle circular : data()) {
        final double radians = circular.radians2PI();
        if ((radians >= radiansStart) && (radians < (radiansStart + binWidth()))) {
          binData.add(circular);
        }
      }

      bins.add(ImmutableBin.of(radiansStart, binData));
    }

    return bins;
  }

  @Value.Check
  protected void check() {
    Validate.inclusiveBetween(0.0, FastMath.PI, binWidth());
  }

  @Value.Immutable
  interface Bin {
    @Value.Parameter(order = 1)
    double radiansStart();

    @Value.Parameter(order = 2)
    List<Angle> data();
  }
}
