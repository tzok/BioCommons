package pl.poznan.put.protein;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.torsion.AtomsBasedTorsionAngle;
import pl.poznan.put.types.UniTypeQuadruplet;

public enum ProteinTorsionAngle implements AtomsBasedTorsionAngle {
    PHI(AtomName.C, AtomName.N, AtomName.CA, AtomName.C, 0, 1, 1, 1, Unicode.PHI),
    PSI(AtomName.N, AtomName.CA, AtomName.C, AtomName.N, 0, 0, 0, 1, Unicode.PSI),
    OMEGA(AtomName.CA, AtomName.C, AtomName.N, AtomName.CA, 0, 0, 1, 1, Unicode.OMEGA),
    CALPHA(AtomName.CA, AtomName.CA, AtomName.CA, AtomName.CA, 0, 1, 2, 3, Unicode.CALPHA);

    private final UniTypeQuadruplet<AtomName> atoms;
    private final UniTypeQuadruplet<Integer> residueRule;
    private final String longDisplayName;
    private final String shortDisplayName;

    private ProteinTorsionAngle(AtomName a1, AtomName a2, AtomName a3,
            AtomName a4, int r1, int r2, int r3, int r4, String unicodeName) {
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
        return MoleculeType.PROTEIN;
    }
}
