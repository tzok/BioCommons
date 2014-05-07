package pl.poznan.put.nucleotide;

import java.util.Arrays;
import java.util.List;

public enum AtomName {
    C1p(AtomType.C, "C1'"), C2(AtomType.C, "C2"), C2p(AtomType.C, "C2'"),
    C3p(AtomType.C, "C3'"), C4(AtomType.C, "C4"), C4p(AtomType.C, "C4'"),
    C5(AtomType.C, "C5"), C5M(AtomType.C, "C5M"), C5p(AtomType.C, "C5'"),
    C6(AtomType.C, "C6"), C8(AtomType.C, "C8"), H1(AtomType.H, "H1"),
    H1p(AtomType.H, "H1'"), H2(AtomType.H, "H2"), H21(AtomType.H, "H21"),
    H22(AtomType.H, "H22"), H2p(AtomType.H, "H2'"), H2pp(AtomType.H, "H2''"),
    H3(AtomType.H, "H3"), H3p(AtomType.H, "H3'"), H41(AtomType.H, "H41"),
    H42(AtomType.H, "H42"), H4p(AtomType.H, "H4'"), H5(AtomType.H, "H5"),
    H51(AtomType.H, "H51"), H52(AtomType.H, "H52"), H53(AtomType.H, "H53"),
    H5p(AtomType.H, "H5'"), H5pp(AtomType.H, "H5''"), H6(AtomType.H, "H6"),
    H61(AtomType.H, "H61"), H62(AtomType.H, "H62"), H8(AtomType.H, "H8"),
    N1(AtomType.N, "N1"), N2(AtomType.N, "N2"), N3(AtomType.N, "N3"),
    N4(AtomType.N, "N4"), N6(AtomType.N, "N6"), N7(AtomType.N, "N7"),
    N9(AtomType.N, "N9"), O1P(AtomType.O, "O1P", "OP1"), O2(AtomType.O, "O2"),
    O2P(AtomType.O, "O2P", "OP2"), O2p(AtomType.O, "O2'"),
    O3p(AtomType.O, "O3'"), O4(AtomType.O, "O4"), O4p(AtomType.O, "O4'"),
    O5p(AtomType.O, "O5'"), O6(AtomType.O, "O6"), P(AtomType.P, "P");

    private final AtomType type;
    private final List<String> names;

    AtomName(AtomType type, String... names) {
        this.type = type;
        this.names = Arrays.asList(names);
    }

    public AtomType getType() {
        return type;
    }

    public boolean matchesName(String pdbName) {
        return names.contains(pdbName.trim());
    }
}
