package pl.poznan.put.rna.torsion;

import pl.poznan.put.torsion.type.TorsionAngleType;

public enum RNATorsionAngleType {
    ALPHA(Alpha.getInstance()),
    BETA(Beta.getInstance()),
    GAMMA(Gamma.getInstance()),
    DELTA(Delta.getInstance()),
    EPSILON(Epsilon.getInstance()),
    ZETA(Zeta.getInstance()),
    NU0(Nu0.getInstance()),
    NU1(Nu1.getInstance()),
    NU2(Nu2.getInstance()),
    NU3(Nu3.getInstance()),
    NU4(Nu4.getInstance()),
    ETA(Eta.getInstance()),
    THETA(Theta.getInstance()),
    ETA_PRIM(EtaPrim.getInstance()),
    THETA_PRIM(ThetaPrim.getInstance());

    private final TorsionAngleType type;

    private RNATorsionAngleType(TorsionAngleType type) {
        this.type = type;
    }

    public TorsionAngleType getAngleType() {
        return type;
    }
}
