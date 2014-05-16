package pl.poznan.put.nucleic;

import pl.poznan.put.common.TorsionAngle;
import pl.poznan.put.common.TorsionAngleValue;

public class PseudophasePucker implements TorsionAngle {
    @Override
    public String getDisplayName() {
        return "P";
    }

    public static TorsionAngleValue calculate(TorsionAngleValue tau0,
            TorsionAngleValue tau1, TorsionAngleValue tau2,
            TorsionAngleValue tau3, TorsionAngleValue tau4) {
        if (tau0.getTorsionAngle() != RNATorsionAngle.TAU0
                || tau1.getTorsionAngle() != RNATorsionAngle.TAU1
                || tau2.getTorsionAngle() != RNATorsionAngle.TAU2
                || tau3.getTorsionAngle() != RNATorsionAngle.TAU3
                || tau4.getTorsionAngle() != RNATorsionAngle.TAU4) {
            return new TorsionAngleValue(new PseudophasePucker(), Double.NaN);
        }

        double scale = 2 * (Math.sin(Math.toRadians(36.0)) + Math.sin(Math.toRadians(72.0)));
        double y1 = tau1.getValue() + tau4.getValue() - tau0.getValue()
                - tau3.getValue();
        double x1 = tau2.getValue() * scale;
        return new TorsionAngleValue(new PseudophasePucker(),
                Math.atan2(y1, x1));
    }
}
