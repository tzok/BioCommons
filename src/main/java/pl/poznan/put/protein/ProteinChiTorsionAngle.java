package pl.poznan.put.protein;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.torsion.AtomsBasedTorsionAngle;
import pl.poznan.put.torsion.ChiTorsionAngle;
import pl.poznan.put.torsion.ChiTorsionAngleType;
import pl.poznan.put.types.UniTypeQuadruplet;

public enum ProteinChiTorsionAngle implements AtomsBasedTorsionAngle,
        ChiTorsionAngle {
    ARG_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    ARG_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    ARG_CHI3(ChiTorsionAngleType.CHI3, AtomName.CB, AtomName.CG, AtomName.CD, AtomName.NE, 0, 0, 0, 0, "CHI3", Unicode.CHI3),
    ARG_CHI4(ChiTorsionAngleType.CHI4, AtomName.CG, AtomName.CD, AtomName.NE, AtomName.CZ, 0, 0, 0, 0, "CHI4", Unicode.CHI4),
    ARG_CHI5(ChiTorsionAngleType.CHI5, AtomName.CD, AtomName.NE, AtomName.CZ, AtomName.NH1, 0, 0, 0, 0, "CHI5", Unicode.CHI5),
    ASN_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    ASN_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.OD1, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    ASP_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    ASP_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.OD1, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    CYS_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.SG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    GLU_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    GLU_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    GLU_CHI3(ChiTorsionAngleType.CHI3, AtomName.CB, AtomName.CG, AtomName.CD, AtomName.OE1, 0, 0, 0, 0, "CHI3", Unicode.CHI3),
    GLN_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    GLN_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    GLN_CHI3(ChiTorsionAngleType.CHI3, AtomName.CB, AtomName.CG, AtomName.CD, AtomName.OE1, 0, 0, 0, 0, "CHI3", Unicode.CHI3),
    HIS_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    HIS_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.ND1, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    ILE_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    ILE_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG1, AtomName.CD1, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    LEU_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    LEU_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    LYS_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    LYS_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    LYS_CHI3(ChiTorsionAngleType.CHI3, AtomName.CB, AtomName.CG, AtomName.CD, AtomName.CE, 0, 0, 0, 0, "CHI3", Unicode.CHI3),
    LYS_CHI4(ChiTorsionAngleType.CHI4, AtomName.CG, AtomName.CD, AtomName.CE, AtomName.NZ, 0, 0, 0, 0, "CHI4", Unicode.CHI4),
    MET_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    MET_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.SD, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    MET_CHI3(ChiTorsionAngleType.CHI3, AtomName.CB, AtomName.CG, AtomName.SD, AtomName.CE, 0, 0, 0, 0, "CHI3", Unicode.CHI3),
    PHE_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    PHE_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    PRO_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    PRO_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    SER_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.OG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    THR_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.OG1, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    TRP_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    TRP_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    TYR_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG, 0, 0, 0, 0, "CHI1", Unicode.CHI1),
    TYR_CHI2(ChiTorsionAngleType.CHI2, AtomName.CA, AtomName.CB, AtomName.CG, AtomName.CD1, 0, 0, 0, 0, "CHI2", Unicode.CHI2),
    VAL_CHI1(ChiTorsionAngleType.CHI1, AtomName.N, AtomName.CA, AtomName.CB, AtomName.CG1, 0, 0, 0, 0, "CHI1", Unicode.CHI1);

    private final ChiTorsionAngleType type;
    private final UniTypeQuadruplet<AtomName> atoms;
    private final UniTypeQuadruplet<Integer> residueRule;
    private final String name;
    private final String displayName;

    private ProteinChiTorsionAngle(ChiTorsionAngleType type, AtomName a1,
            AtomName a2, AtomName a3, AtomName a4, int r1, int r2, int r3,
            int r4, String name, String unicodeName) {
        this.type = type;
        this.atoms = new UniTypeQuadruplet<>(a1, a2, a3, a4);
        this.residueRule = new UniTypeQuadruplet<>(r1, r2, r3, r4);
        this.name = name;
        this.displayName = unicodeName + " (" + name.toLowerCase() + ") "
                + a1.getName() + "-" + a2.getName() + "-" + a3.getName() + "-"
                + a4.getName();
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
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getExportName() {
        return name;
    }

    @Override
    public MoleculeType getMoleculeType() {
        return MoleculeType.PROTEIN;
    }
}
