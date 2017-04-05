package pl.poznan.put.circular.samples;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;
import org.apache.commons.math3.util.MathUtils;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.exception.InvalidCircularOperationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public AngleSample(final Collection<Angle> data) {
        super();
        this.data = new ArrayList<>(data);
        Collections.sort(this.data);

        TrigonometricMoment um1 = getUncenteredMoment(1);
        meanDirection = um1.getMeanDirection();
        meanResultantLength = um1.getMeanResultantLength();
        circularVariance = 1 - meanResultantLength;
        circularStandardDeviation =
                Math.sqrt(-2 * StrictMath.log(meanResultantLength));

        TrigonometricMoment cm2 = getCenteredMoment(2);
        TrigonometricMoment um2 = getUncenteredMoment(2);
        circularDispersion =
                (1.0 - cm2.getMeanResultantLength()) / (2 * StrictMath
                        .pow(meanResultantLength, 2));
        skewness = (cm2.getMeanResultantLength() * StrictMath
                .sin(cm2.getMeanDirection().subtract(meanDirection.multiply(2))
                        .getRadians())) / Math.sqrt(circularVariance);
        kurtosis = ((cm2.getMeanResultantLength() * StrictMath
                .cos(um2.getMeanDirection().subtract(meanDirection.multiply(2))
                        .getRadians())) - StrictMath
                            .pow(meanResultantLength, 4)) / StrictMath
                           .pow(circularVariance, 2);

        UnivariatePointValuePair medianFunctionRoot = minimizeMedianFunction();
        medianDirection = new Angle(medianFunctionRoot.getPoint());
        meanDeviation = medianFunctionRoot.getValue();
    }

    public TrigonometricMoment getUncenteredMoment(final int p) {
        return getMoment(p, false);
    }

    public TrigonometricMoment getCenteredMoment(final int p) {
        return getMoment(p, true);
    }

    private UnivariatePointValuePair minimizeMedianFunction() {
        UnivariateFunction medianObjectiveFunction = new UnivariateFunction() {
            @Override
            public double value(final double v) {
                double sum = 0;

                for (final Angle vector : data) {
                    sum += Angle.subtract(Math.PI,
                                          Angle.subtract(vector.getRadians(),
                                                         v));
                }

                return Math.PI - (sum / data.size());
            }
        };

        UnivariateOptimizer optimizer = new BrentOptimizer(1.0e-10, 1.0e-14);
        return optimizer.optimize(
                new UnivariateObjectiveFunction(medianObjectiveFunction),
                GoalType.MINIMIZE, new SearchInterval(0, 2 * Math.PI),
                new MaxEval(1000));
    }

    private TrigonometricMoment getMoment(
            final int p, final boolean isCentered) {
        double c = 0;
        double s = 0;

        for (final Angle vector : data) {
            double radians = vector.getRadians();

            if (isCentered) {
                radians = vector.subtract(meanDirection).getRadians();
            }

            c += StrictMath.cos(p * radians);
            s += StrictMath.sin(p * radians);
        }

        c /= data.size();
        s /= data.size();

        double rho = Math.sqrt(StrictMath.pow(c, 2) + StrictMath.pow(s, 2));
        double mi;

        if ((s > 0) && (c > 0)) {
            mi = StrictMath.atan(s / c);
        } else if (c < 0) {
            mi = StrictMath.atan(s / c) + Math.PI;
        } else {
            // s < 0 && c > 0
            mi = (StrictMath.atan(s / c) + (2 * Math.PI)) % (2 * Math.PI);
        }

        return new TrigonometricMoment(new Angle(mi), rho);
    }

    public Angle getMeanDirection() {
        return meanDirection;
    }

    public double getMeanResultantLength() {
        return meanResultantLength;
    }

    public double getCircularVariance() {
        return circularVariance;
    }

    public double getCircularStandardDeviation() {
        return circularStandardDeviation;
    }

    public double getCircularDispersion() {
        return circularDispersion;
    }

    public double getSkewness() {
        return skewness;
    }

    public double getKurtosis() {
        return kurtosis;
    }

    public Angle getMedianDirection() {
        return medianDirection;
    }

    public double getMeanDeviation() {
        return meanDeviation;
    }

    public double getCircularRank(final Angle datapoint) {
        if (!data.contains(datapoint)) {
            throw new InvalidCircularOperationException(
                    "Cannot calculate circular rank for an observation "
                    + "outside the sample range");
        }

        int rank = data.indexOf(datapoint) + 1;
        return (MathUtils.TWO_PI * rank) / data.size();
    }

    @Override
    public String toString() {
        return "AngleSample [meanDirection=" + meanDirection
               + ", meanResultantLength=" + meanResultantLength
               + ", circularVariance=" + circularVariance
               + ", circularStandardDeviation=" + circularStandardDeviation
               + ", circularDispersion=" + circularDispersion + ", skewness="
               + skewness + ", kurtosis=" + kurtosis + ", medianDirection="
               + medianDirection + ", meanDeviation=" + meanDeviation + ']';
    }
}
