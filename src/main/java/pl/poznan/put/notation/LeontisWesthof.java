package pl.poznan.put.notation;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
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

  public static LeontisWesthof fromString(final CharSequence input) {
    for (final LeontisWesthof leontisWesthof : LeontisWesthof.values()) {
      if (StringUtils.equalsIgnoreCase(leontisWesthof.name(), input)) {
        return leontisWesthof;
      }
    }
    return LeontisWesthof.UNKNOWN;
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

  @Override
  public String toString() {
    return getShortName();
  }

  /**
   * Generates a three letter representation. "c" for cis, "t" for trans. Next, "W" for
   * Watson-Crick, "H" for Hoogsteen and "S" for sugar.
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

  public String getFullName() {
    if (this == LeontisWesthof.UNKNOWN) {
      return "n/a";
    }

    final char[] cs = name().toCharArray();

    return String.format(
        "%s%s/%s",
        cs[0] == 'C' ? "cis " : "trans ",
        cs[1] == 'W' ? "Watson-Crick" : cs[1] == 'H' ? "Hoogsteen" : "Sugar Edge",
        cs[2] == 'W' ? "Watson-Crick" : cs[2] == 'H' ? "Hoogsteen" : "Sugar Edge");
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
}
