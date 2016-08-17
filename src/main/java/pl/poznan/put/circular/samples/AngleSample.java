package pl.poznan.put.circular.samples;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.exception.InvalidCircularOperationException;
import pl.poznan.put.circular.exception.InvalidCircularValueException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AngleSample {
    private final Collection<Angle> data;
    private final List<Angle> dataSorted;
    private final Angle meanDirection;
    private final double meanResultantLength;
    private final double circularVariance;
    private final double circularStandardDeviation;
    private final double circularDispersion;
    private final double skewness;
    private final double kurtosis;
    private final Angle medianDirection;
    private final double meanDeviation;

    public AngleSample(Collection<Angle> data)
            throws InvalidCircularValueException {
        super();
        this.data = data;
        this.dataSorted = new ArrayList<Angle>(data);
        Collections.sort(dataSorted);

        TrigonometricMoment um1 = getUncenteredMoment(1);
        meanDirection = um1.getMeanDirection();
        meanResultantLength = um1.getMeanResultantLength();
        circularVariance = 1 - meanResultantLength;
        circularStandardDeviation =
                Math.sqrt(-2 * Math.log(meanResultantLength));

        TrigonometricMoment cm2 = getCenteredMoment(2);
        TrigonometricMoment um2 = getUncenteredMoment(2);
        circularDispersion = (1.0 - cm2.getMeanResultantLength()) / (2 * Math
                .pow(meanResultantLength, 2));
        skewness = cm2.getMeanResultantLength() * Math
                .sin(cm2.getMeanDirection().subtract(meanDirection.multiply(2))
                        .getRadians()) / Math.sqrt(circularVariance);
        kurtosis = (cm2.getMeanResultantLength() * Math
                .cos(um2.getMeanDirection().subtract(meanDirection.multiply(2))
                        .getRadians()) - Math.pow(meanResultantLength, 4))
                   / Math.pow(circularVariance, 2);

        UnivariatePointValuePair medianFunctionRoot = minimizeMedianFunction();
        medianDirection = new Angle(medianFunctionRoot.getPoint());
        meanDeviation = medianFunctionRoot.getValue();
    }

    public TrigonometricMoment getUncenteredMoment(int p)
            throws InvalidCircularValueException {
        return getMoment(p, false);
    }

    public TrigonometricMoment getCenteredMoment(int p)
            throws InvalidCircularValueException {
        return getMoment(p, true);
    }

    private UnivariatePointValuePair minimizeMedianFunction() {
        UnivariateFunction medianObjectiveFunction = new UnivariateFunction() {
            @Override
            public double value(double x) {
                double sum = 0;

                for (Angle vector : data) {
                    sum += Angle.subtract(Math.PI,
                                          Angle.subtract(vector.getRadians(),
                                                         x));
                }

                return Math.PI - sum / data.size();
            }
        };

        UnivariateOptimizer optimizer = new BrentOptimizer(1e-10, 1e-14);
        return optimizer.optimize(
                new UnivariateObjectiveFunction(medianObjectiveFunction),
                GoalType.MINIMIZE, new SearchInterval(0, 2 * Math.PI),
                new MaxEval(1000));
    }

    private TrigonometricMoment getMoment(int p, boolean isCentered)
            throws InvalidCircularValueException {
        double c = 0;
        double s = 0;

        for (Angle vector : data) {
            double radians = vector.getRadians();

            if (isCentered) {
                radians = vector.subtract(meanDirection).getRadians();
            }

            c += Math.cos(p * radians);
            s += Math.sin(p * radians);
        }

        c /= data.size();
        s /= data.size();

        double rho = Math.sqrt(Math.pow(c, 2) + Math.pow(s, 2));
        double mi;

        if (s > 0 && c > 0) {
            mi = Math.atan(s / c);
        } else if (c < 0) {
            mi = Math.atan(s / c) + Math.PI;
        } else {
            // s < 0 && c > 0
            mi = (Math.atan(s / c) + 2 * Math.PI) % (2 * Math.PI);
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

    public double getCircularRank(Angle datapoint)
            throws InvalidCircularOperationException {
        if (!data.contains(datapoint)) {
            throw new InvalidCircularOperationException(
                    "Cannot calculate circular rank for an observation "
                    + "outside the sample range");
        }

        int rank = dataSorted.indexOf(datapoint) + 1;
        return 2 * Math.PI * rank / data.size();
    }

    @Override
    public String toString() {
        return "AngleSample [meanDirection=" + meanDirection
               + ", meanResultantLength=" + meanResultantLength
               + ", circularVariance=" + circularVariance
               + ", circularStandardDeviation=" + circularStandardDeviation
               + ", circularDispersion=" + circularDispersion + ", skewness="
               + skewness + ", kurtosis=" + kurtosis + ", medianDirection="
               + medianDirection + ", meanDeviation=" + meanDeviation + "]";
    }
}
