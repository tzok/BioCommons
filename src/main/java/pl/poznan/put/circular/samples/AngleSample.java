package pl.poznan.put.circular.samples;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.circular.exception.InvalidCircularOperationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A class to compute statistics from a sample of angular values. */
public final class AngleSample {
  private final List<Angle> data;
  private final Angle meanDirection;
  private final double meanResultantLength;
  private final double circularVariance;
  private final double circularStandardDeviation;
  private final double circularDispersion;
  private final double skewness;
  private final double kurtosis;
  private final Angle medianDirection;
  private final double meanDeviation;

  public AngleSample(final List<Angle> data) {
    super();

    if (data.isEmpty()) {
      throw new IllegalArgumentException("The sample cannot be empty");
    }

    this.data = new ArrayList<>(data);
    Collections.sort(this.data);

    final TrigonometricMoment um1 = TrigonometricMoment.computeUncentered(data, 1);
    meanDirection = um1.getMeanDirection();
    meanResultantLength = um1.getMeanResultantLength();
    circularVariance = 1.0 - meanResultantLength;
    circularStandardDeviation = FastMath.sqrt(-2.0 * FastMath.log(meanResultantLength));

    final TrigonometricMoment cm2 = TrigonometricMoment.computeCentered(data, 2, meanDirection);
    final TrigonometricMoment um2 = TrigonometricMoment.computeUncentered(data, 2);
    circularDispersion =
        (1.0 - cm2.getMeanResultantLength()) / (2.0 * FastMath.pow(meanResultantLength, 2));
    skewness =
        (cm2.getMeanResultantLength()
                * FastMath.sin(
                    cm2.getMeanDirection().subtract(meanDirection.multiply(2.0)).getRadians()))
            / FastMath.sqrt(circularVariance);
    kurtosis =
        ((cm2.getMeanResultantLength()
                    * FastMath.cos(
                        um2.getMeanDirection().subtract(meanDirection.multiply(2.0)).getRadians()))
                - FastMath.pow(meanResultantLength, 4))
            / FastMath.pow(circularVariance, 2);

    final Pair<Angle, Double> pair = findMedianAndMeanDeviation();
    medianDirection = pair.getKey();
    meanDeviation = pair.getValue();
  }

  private double computeMeanDeviation(final Angle alpha) {
    return data.stream()
            .mapToDouble(angle -> angle.subtract(alpha).getRadians())
            .reduce(0.0, Double::sum)
        / data.size();
  }

  private Pair<Angle, Double> findMedianAndMeanDeviation() {
    // for odd number of observations, one of them will be the median
    // for even number, a middle point will be the median
    final List<? extends Angle> candidates = data.size() % 2 == 1 ? data : computeMiddlePoints();

    double minDeviation = Double.POSITIVE_INFINITY;
    Angle minCandidate = candidates.get(0);

    for (final Angle candidate : candidates) {
      final double deviation = computeMeanDeviation(candidate);
      if (deviation < minDeviation) {
        minDeviation = deviation;
        minCandidate = candidate;
      }

      final Angle candidateAlternative =
          new Angle(candidate.getRadians() + FastMath.PI, ValueType.RADIANS);
      final double deviationAlternative = computeMeanDeviation(candidateAlternative);
      if (deviationAlternative < minDeviation) {
        minDeviation = deviationAlternative;
        minCandidate = candidateAlternative;
      }
    }

    return Pair.of(minCandidate, minDeviation);
  }

  private List<Angle> computeMiddlePoints() {
    final List<Angle> middlePoints = new ArrayList<>();
    for (int i = 1; i < data.size(); i++) {
      final Angle begin = data.get(i - 1);
      final Angle end = data.get(i);
      final Angle middle =
          new Angle((begin.getRadians() + end.getRadians()) / 2.0, ValueType.RADIANS);
      middlePoints.add(middle);
    }

    final Angle last = data.get(data.size() - 1);
    final Angle first = data.get(0);
    final Angle middle =
        new Angle((last.getRadians() + first.getRadians()) / 2.0, ValueType.RADIANS);
    middlePoints.add(middle);
    return middlePoints;
  }

  private Angle selectBetterMedian(final Angle candidate) {
    final Angle alternative = new Angle(candidate.getRadians() + Math.PI, ValueType.RADIANS);

    double s1 = 0.0;
    double s2 = 0.0;
    for (final Angle angle : data) {
      s1 += angle.distance(candidate);
      s2 += angle.distance(alternative);
    }

    return s1 <= s2 ? candidate : alternative;
  }

  /** @return A mean angular value of the sample. */
  public Angle getMeanDirection() {
    return meanDirection;
  }

  /**
   * @return Length of the mean direction vector in range [0; 1]. The closer it is to 1, the less
   *     diverse are the data in the sample.
   */
  public double getMeanResultantLength() {
    return meanResultantLength;
  }

  /** @return A measure of variance of the data on the circle, taking values in range [0; 1]. */
  public double getCircularVariance() {
    return circularVariance;
  }

  /**
   * @return A measure of variance of the data on the circle, taking values in range [0; &infin;].
   */
  public double getCircularStandardDeviation() {
    return circularStandardDeviation;
  }

  /**
   * @return Another measure of variance of the data depending on the first and second central
   *     trigonometric moment.
   */
  public double getCircularDispersion() {
    return circularDispersion;
  }

  /** @return Another measure of variance of the data. */
  public double getSkewness() {
    return skewness;
  }

  /** @return Another measure of variance of the data. */
  public double getKurtosis() {
    return kurtosis;
  }

  /**
   * @return The median direction i.e. angular value to which all others have the minimum mean
   *     distance.
   */
  public Angle getMedianDirection() {
    return medianDirection;
  }

  /** @return The mean of distances of the observations from the samples' median. */
  public double getMeanDeviation() {
    return meanDeviation;
  }

  /**
   * @param datapoint Value, must be one of those used to create this instance of AngleSample.
   * @return The rank (index) of the value in the sample, when treating 0 as the beginning of the
   *     circle.
   */
  public double getCircularRank(final Angle datapoint) {
    if (!data.contains(datapoint)) {
      throw new InvalidCircularOperationException(
          "Cannot calculate circular rank for an observation outside the sample range");
    }

    final int rank = data.indexOf(datapoint) + 1;
    return (MathUtils.TWO_PI * (double) rank) / (double) data.size();
  }

  @Override
  public String toString() {
    return "AngleSample [meanDirection="
        + meanDirection
        + ", meanResultantLength="
        + meanResultantLength
        + ", circularVariance="
        + circularVariance
        + ", circularStandardDeviation="
        + circularStandardDeviation
        + ", circularDispersion="
        + circularDispersion
        + ", skewness="
        + skewness
        + ", kurtosis="
        + kurtosis
        + ", medianDirection="
        + medianDirection
        + ", meanDeviation="
        + meanDeviation
        + ']';
  }
}
