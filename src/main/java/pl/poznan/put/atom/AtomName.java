package pl.poznan.put.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A unified atom name found in PDB and mmCIF files with a list of alternative names. */
public enum AtomName {
  C(AtomType.C, "C"),
  C1(AtomType.C, "C1"),
  C10(AtomType.C, "C10"),
  C11(AtomType.C, "C11"),
  C12(AtomType.C, "C12"),
  C13(AtomType.C, "C13"),
  C14(AtomType.C, "C14"),
  C15(AtomType.C, "C15"),
  C16(AtomType.C, "C16"),
  C19(AtomType.C, "C19"),
  C1p(AtomType.C, "C1'", "C1*"),
  C2(AtomType.C, "C2"),
  C21(AtomType.C, "C21"),
  C23(AtomType.C, "C23"),
  C24(AtomType.C, "C24"),
  C2p(AtomType.C, "C2'", "C2*"),
  C3(AtomType.C, "C3"),
  C3T(AtomType.C, "C3T"),
  C3p(AtomType.C, "C3'", "C3*"),
  C4(AtomType.C, "C4"),
  C4p(AtomType.C, "C4'", "C4*"),
  C5(AtomType.C, "C5"),
  C5M(AtomType.C, "C5M"),
  C5T(AtomType.C, "C5T"),
  C5p(AtomType.C, "C5'", "C5*"),
  C6(AtomType.C, "C6"),
  C8(AtomType.C, "C8"),
  CA(AtomType.C, "CA"),
  CAT(AtomType.C, "CAT"),
  CAY(AtomType.C, "CAY"),
  CB(AtomType.C, "CB"),
  CB1(AtomType.C, "CB1", "1CB"),
  CB2(AtomType.C, "CB2", "2CB"),
  CD(AtomType.C, "CD"),
  CD1(AtomType.C, "CD1"),
  CD2(AtomType.C, "CD2"),
  CE(AtomType.C, "CE"),
  CE1(AtomType.C, "CE1"),
  CE2(AtomType.C, "CE2"),
  CE3(AtomType.C, "CE3"),
  CG(AtomType.C, "CG"),
  CG1(AtomType.C, "CG1"),
  CG2(AtomType.C, "CG2"),
  CH2(AtomType.C, "CH2"),
  CL(AtomType.C, "CL"),
  CLP(AtomType.C, "CLP"),
  CM1(AtomType.C, "CM1"),
  CM2(AtomType.C, "CM2"),
  CM5(AtomType.C, "CM5"),
  CM7(AtomType.C, "CM7"),
  CR(AtomType.C, "CR"),
  CRP(AtomType.C, "CRP"),
  CT(AtomType.C, "CT"),
  CY(AtomType.C, "CY"),
  CZ(AtomType.C, "CZ"),
  CZ2(AtomType.C, "CZ2"),
  CZ3(AtomType.C, "CZ3"),
  H1(AtomType.H, "H1"),
  H1p(AtomType.H, "H1'", "H1*"),
  H2(AtomType.H, "H2"),
  H21(AtomType.H, "H21"),
  H22(AtomType.H, "H22"),
  H2p(AtomType.H, "H2'", "H2*"),
  H2pp(AtomType.H, "H2''", "H2**"),
  H3(AtomType.H, "H3"),
  H3T(AtomType.H, "H3T", "HO3'"),
  H3T1(AtomType.H, "H3T1"),
  H3T2(AtomType.H, "H3T2"),
  H3T3(AtomType.H, "H3T3"),
  H3p(AtomType.H, "H3'", "H3*"),
  H41(AtomType.H, "H41"),
  H42(AtomType.H, "H42"),
  H4p(AtomType.H, "H4'", "H4*"),
  H5(AtomType.H, "H5"),
  H51(AtomType.H, "H51"),
  H52(AtomType.H, "H52"),
  H53(AtomType.H, "H53"),
  H53p(AtomType.H, "H53'", "H53*"),
  H5T(AtomType.H, "H5T", "HO5'"),
  H5T1(AtomType.H, "H5T1"),
  H5T2(AtomType.H, "H5T2"),
  H5T3(AtomType.H, "H5T3"),
  H5p(AtomType.H, "H5'", "H5*"),
  H5pp(AtomType.H, "H5''", "H5**", "'H5'"),
  H6(AtomType.H, "H6"),
  H61(AtomType.H, "H61"),
  H62(AtomType.H, "H62"),
  H8(AtomType.H, "H8"),
  HA(AtomType.H, "HA"),
  HA1(AtomType.H, "HA1"),
  HA2(AtomType.H, "HA2"),
  HB(AtomType.H, "HB"),
  HB1(AtomType.H, "HB1"),
  HB2(AtomType.H, "HB2"),
  HB3(AtomType.H, "HB3"),
  HD1(AtomType.H, "HD1"),
  HD11(AtomType.H, "HD11"),
  HD12(AtomType.H, "HD12"),
  HD13(AtomType.H, "HD13"),
  HD2(AtomType.H, "HD2"),
  HD21(AtomType.H, "HD21"),
  HD22(AtomType.H, "HD22"),
  HD23(AtomType.H, "HD23"),
  HD3(AtomType.H, "HD3"),
  HE(AtomType.H, "HE"),
  HE1(AtomType.H, "HE1"),
  HE2(AtomType.H, "HE2"),
  HE21(AtomType.H, "HE21"),
  HE22(AtomType.H, "HE22"),
  HE3(AtomType.H, "HE3"),
  HG(AtomType.H, "HG"),
  HG1(AtomType.H, "HG1"),
  HG11(AtomType.H, "HG11"),
  HG12(AtomType.H, "HG12"),
  HG13(AtomType.H, "HG13"),
  HG2(AtomType.H, "HG2"),
  HG21(AtomType.H, "HG21"),
  HG22(AtomType.H, "HG22"),
  HG23(AtomType.H, "HG23"),
  HH(AtomType.H, "HH"),
  HH11(AtomType.H, "HH11"),
  HH12(AtomType.H, "HH12"),
  HH2(AtomType.H, "HH2"),
  HH21(AtomType.H, "HH21"),
  HH22(AtomType.H, "HH22"),
  HL(AtomType.H, "HL"),
  HL1(AtomType.H, "HL1"),
  HL2(AtomType.H, "HL2"),
  HL3(AtomType.H, "HL3"),
  HN(AtomType.H, "HN"),
  HN1(AtomType.H, "HN1"),
  HN2(AtomType.H, "HN2"),
  HNT(AtomType.H, "HNT"),
  HO2p(AtomType.H, "HO2'", "'HO2"),
  HR(AtomType.H, "HR"),
  HR1(AtomType.H, "HR1"),
  HR2(AtomType.H, "HR2"),
  HR3(AtomType.H, "HR3"),
  HT1(AtomType.H, "HT1"),
  HT2(AtomType.H, "HT2"),
  HT3(AtomType.H, "HT3"),
  HY1(AtomType.H, "HY1"),
  HY2(AtomType.H, "HY2"),
  HY3(AtomType.H, "HY3"),
  HZ(AtomType.H, "HZ"),
  HZ1(AtomType.H, "HZ1"),
  HZ2(AtomType.H, "HZ2"),
  HZ3(AtomType.H, "HZ3"),
  K(AtomType.OTHER, "K"),
  MG(AtomType.OTHER, "MG"),
  MN(AtomType.OTHER, "MN"),
  N(AtomType.N, "N"),
  N1(AtomType.N, "N1"),
  N2(AtomType.N, "N2"),
  N20(AtomType.N, "N20"),
  N3(AtomType.N, "N3"),
  N4(AtomType.N, "N4"),
  N6(AtomType.N, "N6"),
  N7(AtomType.N, "N7"),
  N9(AtomType.N, "N9"),
  ND1(AtomType.N, "ND1"),
  ND2(AtomType.N, "ND2"),
  NE(AtomType.N, "NE"),
  NE1(AtomType.N, "NE1"),
  NE2(AtomType.N, "NE2"),
  NH1(AtomType.N, "NH1"),
  NH2(AtomType.N, "NH2"),
  NL(AtomType.N, "NL"),
  NR(AtomType.N, "NR"),
  NT(AtomType.N, "NT"),
  NZ(AtomType.N, "NZ"),
  O(AtomType.O, "O"),
  O1(AtomType.O, "O1"),
  O17(AtomType.O, "O17"),
  O18(AtomType.O, "O18"),
  O1A(AtomType.O, "O1A"),
  O1B(AtomType.O, "O1B"),
  O1C(AtomType.O, "O1C"),
  O1G(AtomType.O, "O1G"),
  O1P(AtomType.O, "O1P", "OP1"),
  O1P3(AtomType.O, "O1P3"),
  O2(AtomType.O, "O2"),
  O22(AtomType.O, "O22"),
  O23(AtomType.O, "O23"),
  O2A(AtomType.O, "O2A"),
  O2B(AtomType.O, "O2B"),
  O2C(AtomType.O, "O2C"),
  O2G(AtomType.O, "O2G"),
  O2P(AtomType.O, "O2P", "OP2"),
  O2P3(AtomType.O, "O2P3"),
  O2p(AtomType.O, "O2'", "O2*"),
  O3(AtomType.O, "O3"),
  O3A(AtomType.O, "O3A"),
  O3B(AtomType.O, "O3B"),
  O3C(AtomType.O, "O3C"),
  O3G(AtomType.O, "O3G"),
  O3P(AtomType.O, "O3P", "OP3"),
  O3P3(AtomType.O, "O3P3"),
  O3T(AtomType.O, "O3T"),
  O3p(AtomType.O, "O3'", "O3*"),
  O4(AtomType.O, "O4"),
  O4p(AtomType.O, "O4'", "O4*"),
  O5(AtomType.O, "O5"),
  O5T(AtomType.O, "O5T"),
  O5p(AtomType.O, "O5'", "O5*"),
  O6(AtomType.O, "O6"),
  OD1(AtomType.O, "OD1"),
  OD2(AtomType.O, "OD2"),
  OE1(AtomType.O, "OE1"),
  OE2(AtomType.O, "OE2"),
  OG(AtomType.O, "OG"),
  OG1(AtomType.O, "OG1"),
  OH(AtomType.O, "OH"),
  OL(AtomType.O, "OL"),
  OR(AtomType.O, "OR"),
  OT1(AtomType.O, "OT1"),
  OT2(AtomType.O, "OT2"),
  OY(AtomType.O, "OY"),
  P(AtomType.P, "P"),
  PA(AtomType.P, "PA"),
  PB(AtomType.P, "PB"),
  PC(AtomType.P, "PC"),
  PG(AtomType.P, "PG"),
  P3(AtomType.P, "P3"),
  SD(AtomType.S, "SD"),
  SG(AtomType.S, "SG"),
  SG1(AtomType.S, "SG1", "1SG"),
  SG2(AtomType.S, "SG2", "2SG"),
  UNKNOWN(AtomType.OTHER, "");

  private static final Map<String, AtomName> LOOKUP_TABLE = new HashMap<>();
  private static final Logger LOGGER = LoggerFactory.getLogger(AtomName.class);

  static {
    for (final AtomName atomName : AtomName.values()) {
      for (final String name : atomName.names) {
        AtomName.LOOKUP_TABLE.put(name, atomName);
      }
    }
  }

  private final AtomType type;
  private final List<String> names;

  AtomName(final AtomType type, final String... names) {
    this.type = type;
    this.names = Arrays.asList(names);
  }

  /**
   * Creates an instance from String. For null or unknown atom names, a special constant UNKNOWN is
   * used.
   *
   * @param pdbName String representation of the atom name.
   * @return An instance of this enum.
   */
  public static AtomName fromString(final String pdbName) {
    if (pdbName == null) {
      return AtomName.UNKNOWN;
    }

    if (!AtomName.LOOKUP_TABLE.containsKey(pdbName)) {
      AtomName.LOGGER.trace("Unknown atom name: {}", pdbName);
      return AtomName.UNKNOWN;
    }

    return AtomName.LOOKUP_TABLE.get(pdbName);
  }

  /**
   * Checks if this constant matches a name found in PDB or mmCIF file. Certain atom names have more
   * than one matching PDB names e.g. O2P/OP2.
   *
   * @param pdbName Name found in PDB or mmCIF file.
   * @return True if this constant matches pdbName.
   */
  public boolean matchesName(final String pdbName) {
    return names.contains(pdbName.trim());
  }

  /**
   * Gets the default atom name (if more than one is configured).
   *
   * @return The default name for this constant.
   */
  public String getName() {
    return names.get(0);
  }

  /**
   * Checks if atom is heavy (i.e. not a hydrogen).
   *
   * @return True if this constant describes an atom which is not a hydrogen.
   */
  public boolean isHeavy() {
    return type.isHeavy();
  }

  /**
   * Gets enum describing the type of atom that this constant represents.
   *
   * @return {{@link AtomType}} object representing the atom type.
   */
  public AtomType getType() {
    return type;
  }
}
