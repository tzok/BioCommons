package pl.poznan.put.nucleic;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.AtomsBasedTorsionAngle;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.helper.Constants;
import pl.poznan.put.helper.UniTypeQuadruplet;

public enum RNATorsionAngle implements AtomsBasedTorsionAngle {
    ALPHA(AtomName.O3p, AtomName.P, AtomName.O5p, AtomName.C5p, -1, 0, 0, 0, Constants.UNICODE_ALPHA),
    BETA(AtomName.P, AtomName.O5p, AtomName.C5p, AtomName.C4p, 0, 0, 0, 0, Constants.UNICODE_BETA),
    GAMMA(AtomName.O5p, AtomName.C5p, AtomName.C4p, AtomName.C3p, 0, 0, 0, 0, Constants.UNICODE_GAMMA),
    DELTA(AtomName.C5p, AtomName.C4p, AtomName.C3p, AtomName.O3p, 0, 0, 0, 0, Constants.UNICODE_DELTA),
    EPSILON(AtomName.C4p, AtomName.C3p, AtomName.O3p, AtomName.P, 0, 0, 0, 1, Constants.UNICODE_EPSILON),
    ZETA(AtomName.C3p, AtomName.O3p, AtomName.P, AtomName.O5p, 0, 0, 1, 1, Constants.UNICODE_ZETA),
    TAU0(AtomName.C4p, AtomName.O4p, AtomName.C1p, AtomName.C2p, 0, 0, 0, 0, Constants.UNICODE_TAU0),
    TAU1(AtomName.O4p, AtomName.C1p, AtomName.C2p, AtomName.C3p, 0, 0, 0, 0, Constants.UNICODE_TAU1),
    TAU2(AtomName.C1p, AtomName.C2p, AtomName.C3p, AtomName.C4p, 0, 0, 0, 0, Constants.UNICODE_TAU2),
    TAU3(AtomName.C2p, AtomName.C3p, AtomName.C4p, AtomName.O4p, 0, 0, 0, 0, Constants.UNICODE_TAU3),
    TAU4(AtomName.C3p, AtomName.C4p, AtomName.O4p, AtomName.C1p, 0, 0, 0, 0, Constants.UNICODE_TAU4),
    ETA(AtomName.C4p, AtomName.P, AtomName.C4p, AtomName.P, 0, 1, 1, 2, Constants.UNICODE_ETA),
    THETA(AtomName.P, AtomName.C4p, AtomName.P, AtomName.C4p, 0, 0, 1, 1, Constants.UNICODE_THETA),
    ETA_PRIM(AtomName.C1p, AtomName.P, AtomName.C1p, AtomName.P, 0, 1, 1, 2, Constants.UNICODE_ETA_PRIM),
    THETA_PRIM(AtomName.P, AtomName.C1p, AtomName.P, AtomName.C1p, 0, 0, 1, 1, Constants.UNICODE_THETA_PRIM);

    private final UniTypeQuadruplet<AtomName> atoms;
    private final UniTypeQuadruplet<Integer> residueRule;
    private final String displayName;

    private RNATorsionAngle(AtomName a1, AtomName a2, AtomName a3, AtomName a4,
            int r1, int r2, int r3, int r4, String unicodeName) {
        this.atoms = new UniTypeQuadruplet<AtomName>(a1, a2, a3, a4);
        this.residueRule = new UniTypeQuadruplet<Integer>(r1, r2, r3, r4);
        this.displayName = unicodeName + " (" + name().toLowerCase() + ") "
                + a1.getName() + "-" + a2.getName() + "-" + a3.getName() + "-"
                + a4.getName();
    }

    @Override
    public UniTypeQuadruplet<AtomName> getAtoms() {
        return atoms;
    }

    @Override
    public UniTypeQuadruplet<Integer> getResidueRule() {
        return residueRule;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public MoleculeType getMoleculeType() {
        return MoleculeType.RNA;
    }
}
