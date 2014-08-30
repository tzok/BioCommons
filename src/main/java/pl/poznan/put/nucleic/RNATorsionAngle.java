package pl.poznan.put.nucleic;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.torsion.AtomsBasedTorsionAngle;
import pl.poznan.put.types.UniTypeQuadruplet;

public enum RNATorsionAngle implements AtomsBasedTorsionAngle {
    ALPHA(AtomName.O3p, AtomName.P, AtomName.O5p, AtomName.C5p, -1, 0, 0, 0, Unicode.ALPHA),
    BETA(AtomName.P, AtomName.O5p, AtomName.C5p, AtomName.C4p, 0, 0, 0, 0, Unicode.BETA),
    GAMMA(AtomName.O5p, AtomName.C5p, AtomName.C4p, AtomName.C3p, 0, 0, 0, 0, Unicode.GAMMA),
    DELTA(AtomName.C5p, AtomName.C4p, AtomName.C3p, AtomName.O3p, 0, 0, 0, 0, Unicode.DELTA),
    EPSILON(AtomName.C4p, AtomName.C3p, AtomName.O3p, AtomName.P, 0, 0, 0, 1, Unicode.EPSILON),
    ZETA(AtomName.C3p, AtomName.O3p, AtomName.P, AtomName.O5p, 0, 0, 1, 1, Unicode.ZETA),
    TAU0(AtomName.C4p, AtomName.O4p, AtomName.C1p, AtomName.C2p, 0, 0, 0, 0, Unicode.TAU0),
    TAU1(AtomName.O4p, AtomName.C1p, AtomName.C2p, AtomName.C3p, 0, 0, 0, 0, Unicode.TAU1),
    TAU2(AtomName.C1p, AtomName.C2p, AtomName.C3p, AtomName.C4p, 0, 0, 0, 0, Unicode.TAU2),
    TAU3(AtomName.C2p, AtomName.C3p, AtomName.C4p, AtomName.O4p, 0, 0, 0, 0, Unicode.TAU3),
    TAU4(AtomName.C3p, AtomName.C4p, AtomName.O4p, AtomName.C1p, 0, 0, 0, 0, Unicode.TAU4),
    ETA(AtomName.C4p, AtomName.P, AtomName.C4p, AtomName.P, 0, 1, 1, 2, Unicode.ETA),
    THETA(AtomName.P, AtomName.C4p, AtomName.P, AtomName.C4p, 0, 0, 1, 1, Unicode.THETA),
    ETA_PRIM(AtomName.C1p, AtomName.P, AtomName.C1p, AtomName.P, 0, 1, 1, 2, Unicode.ETA_PRIM),
    THETA_PRIM(AtomName.P, AtomName.C1p, AtomName.P, AtomName.C1p, 0, 0, 1, 1, Unicode.THETA_PRIM);

    private final UniTypeQuadruplet<AtomName> atoms;
    private final UniTypeQuadruplet<Integer> residueRule;
    private final String longDisplayName;
    private final String shortDisplayName;

    private RNATorsionAngle(AtomName a1, AtomName a2, AtomName a3, AtomName a4,
            int r1, int r2, int r3, int r4, String unicodeName) {
        this.atoms = new UniTypeQuadruplet<>(a1, a2, a3, a4);
        this.residueRule = new UniTypeQuadruplet<>(r1, r2, r3, r4);
        this.longDisplayName = unicodeName + " (" + name().toLowerCase() + ") "
                + a1.getName() + "-" + a2.getName() + "-" + a3.getName() + "-"
                + a4.getName();
        this.shortDisplayName = unicodeName;
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
    public String getLongDisplayName() {
        return longDisplayName;
    }

    @Override
    public String getShortDisplayName() {
        return shortDisplayName;
    }

    @Override
    public String getExportName() {
        return name();
    }

    @Override
    public MoleculeType getMoleculeType() {
        return MoleculeType.RNA;
    }
}
