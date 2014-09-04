package pl.poznan.put.nucleic;

import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.torsion.AngleValue;
import pl.poznan.put.torsion.TorsionAngle;

public class PseudophasePuckerAngle implements TorsionAngle {
    public static AngleValue calculate(AngleValue tau0, AngleValue tau1,
            AngleValue tau2, AngleValue tau3, AngleValue tau4) {
        if (tau0 == null || tau1 == null || tau2 == null || tau3 == null
                || tau4 == null || tau0.getAngle() != RNATorsionAngle.TAU0
                || tau1.getAngle() != RNATorsionAngle.TAU1
                || tau2.getAngle() != RNATorsionAngle.TAU2
                || tau3.getAngle() != RNATorsionAngle.TAU3
                || tau4.getAngle() != RNATorsionAngle.TAU4) {
            return new AngleValue(new PseudophasePuckerAngle(), Double.NaN);
        }

        double scale = 2 * (Math.sin(Math.toRadians(36.0)) + Math.sin(Math.toRadians(72.0)));
        double y1 = tau1.getValue() + tau4.getValue() - tau0.getValue()
                - tau3.getValue();
        double x1 = tau2.getValue() * scale;
        return new AngleValue(PseudophasePuckerAngle.INSTANCE, Math.atan2(y1,
                x1));
    }

    public static TorsionAngle[] requiredAngles() {
        return new TorsionAngle[] { RNATorsionAngle.TAU0, RNATorsionAngle.TAU1, RNATorsionAngle.TAU2, RNATorsionAngle.TAU3, RNATorsionAngle.TAU3 };
    }

    private static final PseudophasePuckerAngle INSTANCE = new PseudophasePuckerAngle();

    public static PseudophasePuckerAngle getInstance() {
        return PseudophasePuckerAngle.INSTANCE;
    }

    @Override
    public String getLongDisplayName() {
        return "P";
    }

    @Override
    public String getShortDisplayName() {
        return "P";
    }

    @Override
    public String getExportName() {
        return "P";
    }

    @Override
    public String toString() {
        return "P";
    }

    @Override
    public MoleculeType getMoleculeType() {
        return MoleculeType.RNA;
    }

    private PseudophasePuckerAngle() {
    }
}
