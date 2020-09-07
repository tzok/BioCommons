package pl.poznan.put.notation;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * A classification of RNA base pairs described in: Geometric Nomenclature and Classification of RNA
 * Base Pairs. N.B. Leontis, E. Westhof. RNA. 2001. 7(4):499–512. doi:10.1017/S1355838201002515
 */
public enum LeontisWesthof {
  CWW(Stericity.CIS, NucleobaseEdge.WATSON_CRICK, NucleobaseEdge.WATSON_CRICK),
  CWH(Stericity.CIS, NucleobaseEdge.WATSON_CRICK, NucleobaseEdge.HOOGSTEEN),
  CWS(Stericity.CIS, NucleobaseEdge.WATSON_CRICK, NucleobaseEdge.SUGAR),
  CHW(Stericity.CIS, NucleobaseEdge.HOOGSTEEN, NucleobaseEdge.WATSON_CRICK),
  CHH(Stericity.CIS, NucleobaseEdge.HOOGSTEEN, NucleobaseEdge.HOOGSTEEN),
  CHS(Stericity.CIS, NucleobaseEdge.HOOGSTEEN, NucleobaseEdge.SUGAR),
  CSW(Stericity.CIS, NucleobaseEdge.SUGAR, NucleobaseEdge.WATSON_CRICK),
  CSH(Stericity.CIS, NucleobaseEdge.SUGAR, NucleobaseEdge.HOOGSTEEN),
  CSS(Stericity.CIS, NucleobaseEdge.SUGAR, NucleobaseEdge.SUGAR),
  TWW(Stericity.TRANS, NucleobaseEdge.WATSON_CRICK, NucleobaseEdge.WATSON_CRICK),
  TWH(Stericity.TRANS, NucleobaseEdge.WATSON_CRICK, NucleobaseEdge.HOOGSTEEN),
  TWS(Stericity.TRANS, NucleobaseEdge.WATSON_CRICK, NucleobaseEdge.SUGAR),
  THW(Stericity.TRANS, NucleobaseEdge.HOOGSTEEN, NucleobaseEdge.WATSON_CRICK),
  THH(Stericity.TRANS, NucleobaseEdge.HOOGSTEEN, NucleobaseEdge.HOOGSTEEN),
  THS(Stericity.TRANS, NucleobaseEdge.HOOGSTEEN, NucleobaseEdge.SUGAR),
  TSW(Stericity.TRANS, NucleobaseEdge.SUGAR, NucleobaseEdge.WATSON_CRICK),
  TSH(Stericity.TRANS, NucleobaseEdge.SUGAR, NucleobaseEdge.HOOGSTEEN),
  TSS(Stericity.TRANS, NucleobaseEdge.SUGAR, NucleobaseEdge.SUGAR),
  UNKNOWN(Stericity.UNKNOWN, NucleobaseEdge.UNKNOWN, NucleobaseEdge.UNKNOWN);

  private final Stericity stericity;
  private final NucleobaseEdge edge5;
  private final NucleobaseEdge edge3;

  LeontisWesthof(
      final Stericity stericity, final NucleobaseEdge edge5, final NucleobaseEdge edge3) {
    this.stericity = stericity;
    this.edge5 = edge5;
    this.edge3 = edge3;
  }

  /**
   * This is not a "real" enum's ordinal, but a numeric index of Leontis-Westhof pair as used by
   * other tools.
   *
   * @param ordinal Value between 1-12.
   * @return Enum value represented by the ordinal value.
   */
  public static LeontisWesthof fromOrdinal(final int ordinal) {
    switch (ordinal) {
      case 1:
        return LeontisWesthof.CWW;
      case 2:
        return LeontisWesthof.TWW;
      case 3:
        return LeontisWesthof.CWH;
      case 4:
        return LeontisWesthof.TWH;
      case 5:
        return LeontisWesthof.CWS;
      case 6:
        return LeontisWesthof.TWS;
      case 7:
        return LeontisWesthof.CHH;
      case 8:
        return LeontisWesthof.THH;
      case 9:
        return LeontisWesthof.CHS;
      case 10:
        return LeontisWesthof.THS;
      case 11:
        return LeontisWesthof.CSS;
      case 12:
        return LeontisWesthof.TSS;
      default:
        return LeontisWesthof.UNKNOWN;
    }
  }

  /**
   * This is not a "real" enum's ordinal, but a numeric index of Leontis-Westhof pair as used by
   * other tools.
   *
   * @return Value between 1-12 or Integer.MAX_VALUE for unknown LW.
   */
  public int toOrdinal() {
    switch (this) {
      case CWW:
        return 1;
      case TWW:
        return 2;
      case CWH:
        return 3;
      case TWH:
        return 4;
      case CWS:
        return 5;
      case TWS:
        return 6;
      case CHH:
        return 7;
      case THH:
        return 8;
      case CHS:
        return 9;
      case THS:
        return 10;
      case CSS:
        return 11;
      case TSS:
        return 12;
      default:
        return Integer.MAX_VALUE;
    }
  }

  @Override
  public String toString() {
    return getShortName();
  }

  /**
   * Generates a three letter representation. "c" for cis, "t" for trans. Next, "W" for
   * Watson-Crick, "H" for Hoogsteen and "S" for sugar. This method returns "n/a" for UNKNOWN.
   *
   * @return A three letter representation.
   */
  public String getShortName() {
    if (this == LeontisWesthof.UNKNOWN) {
      return "n/a";
    }

    final char[] chars = name().toCharArray();
    chars[0] = Character.toLowerCase(chars[0]);
    return new String(chars);
  }

  /**
   * Generates a full name representation. It consists of words "cis" or "trans", then
   * "Watson-Crick", "Hoogsteen" or "Sugar". This method returns "n/a" for UNKNOWN.
   *
   * @return A long representation.
   */
  public String getFullName() {
    if (this == LeontisWesthof.UNKNOWN) {
      return "n/a";
    }

    final char[] cs = name().toCharArray();
    return String.format(
        "%s%s/%s",
        LeontisWesthof.edgeName(cs[0]),
        LeontisWesthof.edgeName(cs[1]),
        LeontisWesthof.edgeName(cs[2]));
  }

  private static String edgeName(final char c) {
    switch (Character.toLowerCase(c)) {
      case 'c':
        return "cis";
      case 't':
        return "trans";
      case 'w':
        return "Watson-Crick";
      case 'h':
        return "Hoogsteen";
      case 's':
        return "Sugar";
      default:
        throw new IllegalArgumentException(
            String.format("Letter %s is not recognized in Leontis-Westhof notation", c));
    }
  }

  /**
   * Inverts the base edges e.g. cHS becomes cSH. Stericity (i.e. cis or trans) stays as it was.
   *
   * @return An enum value.
   */
  public LeontisWesthof invert() {
    if (this == LeontisWesthof.UNKNOWN) {
      return LeontisWesthof.UNKNOWN;
    }

    final char[] chars = name().toCharArray();
    final char tmp = chars[1];
    chars[1] = chars[2];
    chars[2] = tmp;
    return LeontisWesthof.fromString(new String(chars));
  }

  /**
   * Find a constant that matches a given name in case-insensitive manner or return UNKNOWN
   * otherwise. For example, cww is the same as cWW.
   *
   * @param input A string representing LW notation.
   * @return An instance of this class that matches the given name or UNKNONW if none does.
   */
  public static LeontisWesthof fromString(final CharSequence input) {
    return Arrays.stream(LeontisWesthof.values())
        .filter(leontisWesthof -> StringUtils.equalsIgnoreCase(leontisWesthof.name(), input))
        .findFirst()
        .orElse(LeontisWesthof.UNKNOWN);
  }

  /** @return Stericity i.e. cis, trans or unknown. */
  public Stericity getStericity() {
    return stericity;
  }

  /** @return Edge of the 5' partner i.e. Watson-Crick, Hoogsteen, sugar or unknown. */
  public NucleobaseEdge getEdge5() {
    return edge5;
  }

  /** @return Edge of the 3' partner i.e. Watson-Crick, Hoogsteen, sugar or unknown. */
  public NucleobaseEdge getEdge3() {
    return edge3;
  }
}
