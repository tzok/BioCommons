package pl.poznan.put.nucleic;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.common.TorsionAngle;
import pl.poznan.put.common.TorsionAngleValue;

public class PseudophasePuckerAngle implements TorsionAngle {
    public static TorsionAngleValue calculate(TorsionAngleValue tau0,
            TorsionAngleValue tau1, TorsionAngleValue tau2,
            TorsionAngleValue tau3, TorsionAngleValue tau4) {
        if (tau0 == null || tau1 == null || tau2 == null || tau3 == null
                || tau4 == null
                || tau0.getTorsionAngle() != RNATorsionAngle.TAU0
                || tau1.getTorsionAngle() != RNATorsionAngle.TAU1
                || tau2.getTorsionAngle() != RNATorsionAngle.TAU2
                || tau3.getTorsionAngle() != RNATorsionAngle.TAU3
                || tau4.getTorsionAngle() != RNATorsionAngle.TAU4) {
            return new TorsionAngleValue(new PseudophasePuckerAngle(),
                    Double.NaN);
        }

        double scale = 2 * (Math.sin(Math.toRadians(36.0)) + Math.sin(Math.toRadians(72.0)));
        double y1 = tau1.getValue() + tau4.getValue() - tau0.getValue()
                - tau3.getValue();
        double x1 = tau2.getValue() * scale;
        return new TorsionAngleValue(PseudophasePuckerAngle.INSTANCE,
                Math.atan2(y1, x1));
    }

    private static final PseudophasePuckerAngle INSTANCE = new PseudophasePuckerAngle();

    public static PseudophasePuckerAngle getInstance() {
        return PseudophasePuckerAngle.INSTANCE;
    }

    @Override
    public String getDisplayName() {
        return "P";
    }

    @Override
    public MoleculeType getMoleculeType() {
        return MoleculeType.RNA;
    }

    @Override
    public String toString() {
        return "P";
    }

    private PseudophasePuckerAngle() {
    }
}
