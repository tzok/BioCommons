package pl.poznan.put.circular;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import pl.poznan.put.circular.exception.InvalidCircularValueException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Angular histogram collecting observations in bins of specified width. */
public class Histogram {
  private final Collection<Bin> bins = new ArrayList<>();
  private final double binWidth;
  private final double dataSize;

  /**
   * Create an instance of histogram with the given data and specified width of bins.
   *
   * @param data A collection of circular data.
   * @param binWidth Bin width in range [0, pi).
   */
  public Histogram(final Collection<Angle> data, final double binWidth) {
    super();
    this.binWidth = binWidth;
    dataSize = (double) data.size();

    if ((binWidth < 0.0) || (binWidth >= FastMath.PI)) {
      throw new InvalidCircularValueException("A bin size must be in range [0..180)");
    }

    for (double radiansStart = 0.0; radiansStart < MathUtils.TWO_PI; radiansStart += binWidth) {
      final List<Angle> binData = new ArrayList<>();

      for (final Angle circular : data) {
        final double radians = circular.radians2PI();
        if ((radians >= radiansStart) && (radians < (radiansStart + binWidth))) {
          binData.add(circular);
        }
      }

      bins.add(ImmutableBin.of(radiansStart, binData));
    }
  }

  /**
   * Find bin which starts at a given point.
   *
   * @param radiansStart Value in radians which describes bin starting point (precision 1.0e-3).
   * @return A collection of circular values in the found bin.
   */
  public final Collection<Angle> getBin(final double radiansStart) {
    return bins.stream()
        .filter(bin -> Precision.equals(bin.radiansStart(), radiansStart, 1.0e-3))
        .findFirst()
        .map(Bin::data)
        .orElse(Collections.emptyList());
  }

  /**
   * Find the largest bin and calculate its relative size.
   *
   * @return Value in range [0; 1] describing relative size of the largest bin.
   */
  public final double getMaxFrequency() {
    double maxFrequency = Double.NEGATIVE_INFINITY;
    for (double d = 0.0; d < MathUtils.TWO_PI; d += binWidth) {
      final double frequency = (double) getBin(d).size() / dataSize;
      maxFrequency = FastMath.max(frequency, maxFrequency);
    }
    return maxFrequency;
  }
}
