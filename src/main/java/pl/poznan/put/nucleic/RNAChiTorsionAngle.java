package pl.poznan.put.nucleic;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.torsion.AtomsBasedTorsionAngle;
import pl.poznan.put.torsion.ChiTorsionAngle;
import pl.poznan.put.torsion.ChiTorsionAngleType;
import pl.poznan.put.types.UniTypeQuadruplet;

public enum RNAChiTorsionAngle implements AtomsBasedTorsionAngle,
        ChiTorsionAngle {
    PURINE(ChiTorsionAngleType.CHI, AtomName.O4p, AtomName.C1p, AtomName.N9, AtomName.C4, 0, 0, 0, 0, "CHI", Unicode.CHI),
    PYRIMIDINE(ChiTorsionAngleType.CHI, AtomName.O4p, AtomName.C1p, AtomName.N1, AtomName.C2, 0, 0, 0, 0, "CHI", Unicode.CHI);

    private final ChiTorsionAngleType type;
    private final UniTypeQuadruplet<AtomName> atoms;
    private final UniTypeQuadruplet<Integer> residueRule;
    private final String name;
    private final String longDisplayName;
    private final String shortDisplayName;

    private RNAChiTorsionAngle(ChiTorsionAngleType type, AtomName a1,
            AtomName a2, AtomName a3, AtomName a4, int r1, int r2, int r3,
            int r4, String name, String unicodeName) {
        this.type = type;
        this.atoms = new UniTypeQuadruplet<>(a1, a2, a3, a4);
        this.residueRule = new UniTypeQuadruplet<>(r1, r2, r3, r4);
        this.name = name;
        this.longDisplayName = unicodeName + " (" + name.toLowerCase() + ") "
                + a1.getName() + "-" + a2.getName() + "-" + a3.getName() + "-"
                + a4.getName();
        this.shortDisplayName = unicodeName;
    }

    @Override
    public ChiTorsionAngleType getType() {
        return type;
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
        return name;
    }

    @Override
    public MoleculeType getMoleculeType() {
        return MoleculeType.RNA;
    }

    @Override
    public String toString() {
        return name;
    }
}
