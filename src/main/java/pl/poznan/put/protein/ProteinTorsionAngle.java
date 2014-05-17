package pl.poznan.put.protein;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.AtomsBasedTorsionAngle;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.helper.Constants;
import pl.poznan.put.helper.UniTypeQuadruplet;

public enum ProteinTorsionAngle implements AtomsBasedTorsionAngle {
    PHI(AtomName.C, AtomName.N, AtomName.CA, AtomName.C, 0, 1, 1, 1, Constants.UNICODE_PHI),
    PSI(AtomName.N, AtomName.CA, AtomName.C, AtomName.N, 0, 0, 0, 1, Constants.UNICODE_PSI),
    OMEGA(AtomName.CA, AtomName.C, AtomName.N, AtomName.CA, 0, 0, 1, 1, Constants.UNICODE_OMEGA),
    CALPHA(AtomName.CA, AtomName.CA, AtomName.CA, AtomName.CA, 0, 1, 2, 3, Constants.UNICODE_CALPHA);

    private final UniTypeQuadruplet<AtomName> atoms;
    private final UniTypeQuadruplet<Integer> residueRule;
    private final String displayName;

    private ProteinTorsionAngle(AtomName a1, AtomName a2, AtomName a3,
            AtomName a4, int r1, int r2, int r3, int r4, String unicodeName) {
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
        return MoleculeType.PROTEIN;
    }
}
