package pl.poznan.put.circular;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import pl.poznan.put.circular.exception.InvalidCircularValueException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Histogram {
  private final Collection<Bin> bins = new ArrayList<>();
  private final double binWidth;
  private final double dataSize;

  public Histogram(final Collection<? extends Circular> data, final double binWidth) {
    super();
    this.binWidth = binWidth;
    dataSize = data.size();

    if ((binWidth < 0) || (binWidth >= FastMath.PI)) {
      throw new InvalidCircularValueException("A bins bin size must be in range [0..180)");
    }

    for (double radiansStart = 0; radiansStart < MathUtils.TWO_PI; radiansStart += binWidth) {
      final List<Circular> binData = new ArrayList<>();

      for (final Circular circular : data) {
        final double radians = circular.getRadians2PI();
        if ((radians >= radiansStart) && (radians < (radiansStart + binWidth))) {
          binData.add(circular);
        }
      }

      bins.add(ImmutableBin.of(radiansStart, binData));
    }
  }

  public final int getBinSize(final double radiansStart) {
    return getBin(radiansStart).size();
  }

  private Collection<Circular> getBin(final double radiansStart) {
    return bins.stream()
        .filter(bin -> Precision.equals(bin.radiansStart(), radiansStart, 1.0e-3))
        .findFirst()
        .map(Bin::data)
        .orElse(Collections.emptyList());
  }

  public final double getMaxFrequency() {
    double maxFrequency = Double.NEGATIVE_INFINITY;
    for (double d = 0; d < MathUtils.TWO_PI; d += binWidth) {
      final double frequency = getBinSize(d) / dataSize;
      maxFrequency = FastMath.max(frequency, maxFrequency);
    }
    return maxFrequency;
  }
}
