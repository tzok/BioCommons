package pl.poznan.put.circular.samples;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.immutables.value.Value;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.circular.exception.InvalidCircularOperationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** A sample of angular values and computed statistics. */
@Value.Immutable
public abstract class AngleSample {
  /** @return The collection of values in the sample. */
  @Value.Parameter(order = 1)
  public abstract Collection<Angle> data();

  /** @return A mean angular value of the sample. */
  @Value.Lazy
  public Angle meanDirection() {
    return um1().meanDirection();
  }

  /**
   * @return The length of the mean direction vector in range [0; 1]. The closer it is to 1, the
   *     less diverse are the data in the sample.
   */
  @Value.Lazy
  public double meanResultantLength() {
    return um1().meanResultantLength();
  }

  /** @return A measure of variance of the data on the circle, taking values in range [0; 1]. */
  @Value.Lazy
  public double circularVariance() {
    return 1.0 - meanResultantLength();
  }

  /**
   * @return A measure of variance of the data on the circle, taking values in range [0; &infin;].
   */
  @Value.Lazy
  public double circularStandardDeviation() {
    return FastMath.sqrt(-2.0 * FastMath.log(meanResultantLength()));
  }

  /**
   * @return Another measure of variance of the data depending on the first and second central
   *     trigonometric moment.
   */
  @Value.Lazy
  public double circularDispersion() {
    return (1.0 - cm2().meanResultantLength()) / (2.0 * FastMath.pow(meanResultantLength(), 2));
  }

  /** @return Another measure of variance of the data. */
  @Value.Lazy
  public double skewness() {
    return (cm2().meanResultantLength()
            * FastMath.sin(cm2().meanDirection().subtract(meanDirection().multiply(2.0)).radians()))
        / FastMath.sqrt(circularVariance());
  }

  /** @return Another measure of variance of the data. */
  @Value.Lazy
  public double kurtosis() {
    return ((cm2().meanResultantLength()
                * FastMath.cos(
                    um2().meanDirection().subtract(meanDirection().multiply(2.0)).radians()))
            - FastMath.pow(meanResultantLength(), 4))
        / FastMath.pow(circularVariance(), 2);
  }

  /**
   * @return The median direction i.e. angular value to which all others have the minimum mean
   *     distance.
   */
  @Value.Lazy
  public Angle medianDirection() {
    return medianAndMeanDeviation().getKey();
  }

  /** @return The mean of distances of the observations from the samples' median. */
  @Value.Lazy
  public double meanDeviation() {
    return medianAndMeanDeviation().getValue();
  }

  /**
   * Computes the rank (index) of the value in the current sample assuming that 0 is the beginning
   * of the circle.
   *
   * @param datapoint Value, must be one of those used to create this instance of AngleSample.
   * @return The rank (index) of the value in the sample, when treating 0 as the beginning of the
   *     circle.
   */
  public double circularRank(final Angle datapoint) {
    if (!sortedData().contains(datapoint)) {
      throw new InvalidCircularOperationException(
          "Cannot calculate circular rank for an observation outside the sample range");
    }

    final int rank = sortedData().indexOf(datapoint) + 1;
    return (MathUtils.TWO_PI * (double) rank) / (double) sortedData().size();
  }

  @Override
  public String toString() {
    return "AngleSample [meanDirection="
        + meanDirection()
        + ", meanResultantLength="
        + meanResultantLength()
        + ", circularVariance="
        + circularVariance()
        + ", circularStandardDeviation="
        + circularStandardDeviation()
        + ", circularDispersion="
        + circularDispersion()
        + ", skewness="
        + skewness()
        + ", kurtosis="
        + kurtosis()
        + ", medianDirection="
        + medianDirection()
        + ", meanDeviation="
        + meanDeviation()
        + ']';
  }

  @Value.Check
  protected void check() {
    Validate.notEmpty(data());
  }

  @Value.Lazy
  protected Pair<Angle, Double> medianAndMeanDeviation() {
    // for odd number of observations, one of them will be the median
    // for even number, a middle point will be the median
    final List<Angle> candidates =
        sortedData().size() % 2 == 1 ? sortedData() : computeMiddlePoints();

    double minDeviation = Double.POSITIVE_INFINITY;
    Angle minCandidate = candidates.get(0);

    for (final Angle candidate : candidates) {
      final double deviation = computeMeanDeviation(candidate);
      if (deviation < minDeviation) {
        minDeviation = deviation;
        minCandidate = candidate;
      }

      final Angle candidateAlternative = ImmutableAngle.of(candidate.radians() + FastMath.PI);
      final double deviationAlternative = computeMeanDeviation(candidateAlternative);
      if (deviationAlternative < minDeviation) {
        minDeviation = deviationAlternative;
        minCandidate = candidateAlternative;
      }
    }

    return Pair.of(minCandidate, minDeviation);
  }

  @Value.Lazy
  protected List<Angle> sortedData() {
    return data().stream().sorted().collect(Collectors.toList());
  }

  @Value.Lazy
  protected TrigonometricMoment um1() {
    return TrigonometricMoment.computeUncentered(data(), 1);
  }

  @Value.Lazy
  protected TrigonometricMoment cm2() {
    return TrigonometricMoment.computeCentered(data(), 2, meanDirection());
  }

  @Value.Lazy
  protected TrigonometricMoment um2() {
    return TrigonometricMoment.computeUncentered(data(), 2);
  }

  private double computeMeanDeviation(final Angle alpha) {
    return data().stream()
            .mapToDouble(angle -> angle.subtract(alpha).radians())
            .reduce(Double::sum)
            .orElse(Double.NaN)
        / data().size();
  }

  private List<Angle> computeMiddlePoints() {
    final List<Angle> middlePoints = new ArrayList<>();
    for (int i = 1; i < sortedData().size(); i++) {
      final Angle begin = sortedData().get(i - 1);
      final Angle end = sortedData().get(i);
      final Angle middle = ImmutableAngle.of((begin.radians() + end.radians()) / 2.0);
      middlePoints.add(middle);
    }

    final Angle last = sortedData().get(sortedData().size() - 1);
    final Angle first = sortedData().get(0);
    final Angle middle = ImmutableAngle.of((last.radians() + first.radians()) / 2.0);
    middlePoints.add(middle);
    return middlePoints;
  }
}
