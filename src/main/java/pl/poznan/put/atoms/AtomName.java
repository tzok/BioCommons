package pl.poznan.put.atoms;

import java.util.Arrays;
import java.util.List;

public enum AtomName {
    _1CB(AtomType.C, "1CB"), _1SG(AtomType.S, "1SG"), _2CB(AtomType.C, "2CB"),
    _2SG(AtomType.S, "2SG"), C1p(AtomType.C, "C1'", "C1*"),
    C2p(AtomType.C, "C2'", "C2*"), C2(AtomType.C, "C2"),
    C3p(AtomType.C, "C3'", "C3*"), C3T(AtomType.C, "C3T"),
    C4p(AtomType.C, "C4'", "C4*"), C4(AtomType.C, "C4"),
    C5p(AtomType.C, "C5'", "C5*"), C5(AtomType.C, "C5"),
    C5M(AtomType.C, "C5M"), C5T(AtomType.C, "C5T"), C6(AtomType.C, "C6"),
    C8(AtomType.C, "C8"), CA(AtomType.C, "CA"), CAT(AtomType.C, "CAT"),
    C(AtomType.C, "C"), CAY(AtomType.C, "CAY"), CB(AtomType.C, "CB"),
    CD1(AtomType.C, "CD1"), CD2(AtomType.C, "CD2"), CD(AtomType.C, "CD"),
    CE1(AtomType.C, "CE1"), CE2(AtomType.C, "CE2"), CE3(AtomType.C, "CE3"),
    CE(AtomType.C, "CE"), CG1(AtomType.C, "CG1"), CG2(AtomType.C, "CG2"),
    CG(AtomType.C, "CG"), CH2(AtomType.C, "CH2"), CL(AtomType.C, "CL"),
    CLP(AtomType.C, "CLP"), CR(AtomType.C, "CR"), CRP(AtomType.C, "CRP"),
    CT(AtomType.C, "CT"), CY(AtomType.C, "CY"), CZ2(AtomType.C, "CZ2"),
    CZ3(AtomType.C, "CZ3"), CZ(AtomType.C, "CZ"),
    H1p(AtomType.H, "H1'", "H1*"), H1(AtomType.H, "H1"),
    H21(AtomType.H, "H21"), H22(AtomType.H, "H22"),
    H2pp(AtomType.H, "H2''", "H2**"), H2p(AtomType.H, "H2'", "H2*"),
    H2(AtomType.H, "H2"), H3p(AtomType.H, "H3'", "H3*"), H3(AtomType.H, "H3"),
    H3T1(AtomType.H, "H3T1"), H3T2(AtomType.H, "H3T2"),
    H3T3(AtomType.H, "H3T3"), H3T(AtomType.H, "H3T"), H41(AtomType.H, "H41"),
    H42(AtomType.H, "H42"), H4p(AtomType.H, "H4'", "H4*"),
    H51(AtomType.H, "H51"), H52(AtomType.H, "H52"),
    H53p(AtomType.H, "H53'", "H53*"), H53(AtomType.H, "H53"),
    H5pp(AtomType.H, "H5''", "H5**"), H5p(AtomType.H, "H5'", "H5*"),
    H5(AtomType.H, "H5"), H5T1(AtomType.H, "H5T1"), H5T2(AtomType.H, "H5T2"),
    H5T3(AtomType.H, "H5T3"), H5T(AtomType.H, "H5T"), H61(AtomType.H, "H61"),
    H62(AtomType.H, "H62"), H6(AtomType.H, "H6"), H8(AtomType.H, "H8"),
    HA1(AtomType.H, "HA1"), HA2(AtomType.H, "HA2"), HA(AtomType.H, "HA"),
    HB1(AtomType.H, "HB1"), HB2(AtomType.H, "HB2"), HB3(AtomType.H, "HB3"),
    HB(AtomType.H, "HB"), HD11(AtomType.H, "HD11"), HD12(AtomType.H, "HD12"),
    HD13(AtomType.H, "HD13"), HD1(AtomType.H, "HD1"), HD21(AtomType.H, "HD21"),
    HD22(AtomType.H, "HD22"), HD23(AtomType.H, "HD23"), HD2(AtomType.H, "HD2"),
    HD3(AtomType.H, "HD3"), HE1(AtomType.H, "HE1"), HE21(AtomType.H, "HE21"),
    HE22(AtomType.H, "HE22"), HE2(AtomType.H, "HE2"), HE3(AtomType.H, "HE3"),
    HE(AtomType.H, "HE"), HG11(AtomType.H, "HG11"), HG12(AtomType.H, "HG12"),
    HG13(AtomType.H, "HG13"), HG1(AtomType.H, "HG1"), HG21(AtomType.H, "HG21"),
    HG22(AtomType.H, "HG22"), HG23(AtomType.H, "HG23"), HG2(AtomType.H, "HG2"),
    HG(AtomType.H, "HG"), HH11(AtomType.H, "HH11"), HH12(AtomType.H, "HH12"),
    HH21(AtomType.H, "HH21"), HH22(AtomType.H, "HH22"), HH2(AtomType.H, "HH2"),
    HH(AtomType.H, "HH"), HL1(AtomType.H, "HL1"), HL2(AtomType.H, "HL2"),
    HL3(AtomType.H, "HL3"), HL(AtomType.H, "HL"), HN1(AtomType.H, "HN1"),
    HN2(AtomType.H, "HN2"), HN(AtomType.H, "HN"), HNT(AtomType.H, "HNT"),
    HR1(AtomType.H, "HR1"), HR2(AtomType.H, "HR2"), HR3(AtomType.H, "HR3"),
    HR(AtomType.H, "HR"), HT1(AtomType.H, "HT1"), HT2(AtomType.H, "HT2"),
    HT3(AtomType.H, "HT3"), HY1(AtomType.H, "HY1"), HY2(AtomType.H, "HY2"),
    HY3(AtomType.H, "HY3"), HZ1(AtomType.H, "HZ1"), HZ2(AtomType.H, "HZ2"),
    HZ3(AtomType.H, "HZ3"), HZ(AtomType.H, "HZ"), N1(AtomType.N, "N1"),
    N2(AtomType.N, "N2"), N3(AtomType.N, "N3"), N4(AtomType.N, "N4"),
    N6(AtomType.N, "N6"), N7(AtomType.N, "N7"), N9(AtomType.N, "N9"),
    N(AtomType.N, "N"), ND1(AtomType.N, "ND1"), ND2(AtomType.N, "ND2"),
    NE1(AtomType.N, "NE1"), NE2(AtomType.N, "NE2"), NE(AtomType.N, "NE"),
    NH1(AtomType.N, "NH1"), NH2(AtomType.N, "NH2"), NL(AtomType.N, "NL"),
    NR(AtomType.N, "NR"), NT(AtomType.N, "NT"), NZ(AtomType.N, "NZ"),
    O1P3(AtomType.O, "O1P3"), O1P(AtomType.O, "O1P", "OP1"),
    O2p(AtomType.O, "O2'", "O2*"), O2(AtomType.O, "O2"),
    O2P3(AtomType.O, "O2P3"), O2P(AtomType.O, "O2P", "OP2"),
    O3p(AtomType.O, "O3'", "O3*"), O3P3(AtomType.O, "O3P3"),
    O3T(AtomType.O, "O3T"), O4p(AtomType.O, "O4'", "O4*"),
    O4(AtomType.O, "O4"), O5p(AtomType.O, "O5'", "O5*"),
    O5T(AtomType.O, "O5T"), O6(AtomType.O, "O6"), O(AtomType.O, "O"),
    OD1(AtomType.O, "OD1"), OD2(AtomType.O, "OD2"), OE1(AtomType.O, "OE1"),
    OE2(AtomType.O, "OE2"), OG1(AtomType.O, "OG1"), OG(AtomType.O, "OG"),
    OH(AtomType.O, "OH"), OL(AtomType.O, "OL"), OR(AtomType.O, "OR"),
    OT1(AtomType.O, "OT1"), OT2(AtomType.O, "OT2"), OY(AtomType.O, "OY"),
    P3(AtomType.P, "P3"), P(AtomType.P, "P"), SD(AtomType.S, "SD"),
    SG(AtomType.S, "SG");

    private final AtomType type;
    private final List<String> names;

    AtomName(AtomType type, String... names) {
        this.type = type;
        this.names = Arrays.asList(names);
    }

    public AtomType getType() {
        return type;
    }

    public String getName() {
        return names.get(0);
    }

    public boolean matchesName(String pdbName) {
        return names.contains(pdbName.trim());
    }
}
