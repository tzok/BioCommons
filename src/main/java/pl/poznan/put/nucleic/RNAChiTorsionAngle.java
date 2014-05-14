package pl.poznan.put.nucleic;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.TorsionAngle;
import pl.poznan.put.helper.Constants;
import pl.poznan.put.helper.UniTypeQuadruplet;

public enum RNAChiTorsionAngle implements TorsionAngle {
    PURINE(AtomName.O4p, AtomName.C1p, AtomName.N9, AtomName.C4, 0, 0, 0, 0, "CHI", Constants.UNICODE_CHI),
    PYRIMIDINE(AtomName.O4p, AtomName.C1p, AtomName.N1, AtomName.C2, 0, 0, 0, 0, "CHI", Constants.UNICODE_CHI);

    private final UniTypeQuadruplet<AtomName> atoms;
    private final UniTypeQuadruplet<Integer> residueRule;
    private final String name;
    private final String displayName;

    private RNAChiTorsionAngle(AtomName a1, AtomName a2, AtomName a3,
            AtomName a4, int r1, int r2, int r3, int r4, String name,
            String unicodeName) {
        this.atoms = new UniTypeQuadruplet<AtomName>(a1, a2, a3, a4);
        this.residueRule = new UniTypeQuadruplet<Integer>(r1, r2, r3, r4);
        this.name = name;
        this.displayName = unicodeName + " (" + name.toLowerCase() + ") "
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
    public String toString() {
        return name;
    }
}
