package pl.poznan.put.notation;

import java.util.Objects;

public enum LeontisWesthof {
    CWW,
    CWH,
    CWS,
    CHW,
    CHH,
    CHS,
    CSW,
    CSH,
    CSS,
    TWW,
    TWH,
    TWS,
    THW,
    THH,
    THS,
    TSW,
    TSH,
    TSS,
    UNKNOWN;

    public static LeontisWesthof fromString(final String str) {
        String lc = str.toLowerCase();

        if (Objects.equals("cww", lc)) {
            return LeontisWesthof.CWW;
        }
        if (Objects.equals("cwh", lc)) {
            return LeontisWesthof.CWH;
        }
        if (Objects.equals("cws", lc)) {
            return LeontisWesthof.CWS;
        }
        if (Objects.equals("chw", lc)) {
            return LeontisWesthof.CHW;
        }
        if (Objects.equals("chh", lc)) {
            return LeontisWesthof.CHH;
        }
        if (Objects.equals("chs", lc)) {
            return LeontisWesthof.CHS;
        }
        if (Objects.equals("csw", lc)) {
            return LeontisWesthof.CSW;
        }
        if (Objects.equals("csh", lc)) {
            return LeontisWesthof.CSH;
        }
        if (Objects.equals("css", lc)) {
            return LeontisWesthof.CSS;
        }
        if (Objects.equals("tww", lc)) {
            return LeontisWesthof.TWW;
        }
        if (Objects.equals("twh", lc)) {
            return LeontisWesthof.TWH;
        }
        if (Objects.equals("tws", lc)) {
            return LeontisWesthof.TWS;
        }
        if (Objects.equals("thw", lc)) {
            return LeontisWesthof.THW;
        }
        if (Objects.equals("thh", lc)) {
            return LeontisWesthof.THH;
        }
        if (Objects.equals("ths", lc)) {
            return LeontisWesthof.THS;
        }
        if (Objects.equals("tsw", lc)) {
            return LeontisWesthof.TSW;
        }
        if (Objects.equals("tsh", lc)) {
            return LeontisWesthof.TSH;
        }
        if (Objects.equals("tss", lc)) {
            return LeontisWesthof.TSS;
        }
        return LeontisWesthof.UNKNOWN;
    }

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
        if (this == LeontisWesthof.UNKNOWN) {
            return "n/a";
        }

        char[] cs = name().toCharArray();

        return String.valueOf(cs[1]) + '/' + cs[2] + ' ' + (cs[0] == 'C' ? "cis"
                                                                         :
                                                            "trans");
    }

    public String getFullName() {
        if (this == LeontisWesthof.UNKNOWN) {
            return "n/a";
        }

        char[] cs = name().toCharArray();

        return (cs[0] == 'C' ? "cis " : "trans ") + (cs[1] == 'W'
                                                     ? "Watson-Crick"
                                                     : cs[1] == 'H'
                                                       ? "Hoogsteen"
                                                       : "Sugar Edge") + '/' + (
                       cs[2] == 'W' ? "Watson-Crick"
                                    : cs[2] == 'H' ? "Hoogsteen"
                                                   : "Sugar Edge");
    }

    public LeontisWesthof invert() {
        switch (this) {
            case CWW:
                return LeontisWesthof.CWW;
            case CWH:
                return LeontisWesthof.CHW;
            case CWS:
                return LeontisWesthof.CSW;
            case CHW:
                return LeontisWesthof.CWH;
            case CHH:
                return LeontisWesthof.CHH;
            case CHS:
                return LeontisWesthof.CSH;
            case CSW:
                return LeontisWesthof.CWS;
            case CSH:
                return LeontisWesthof.CHS;
            case CSS:
                return LeontisWesthof.CSS;
            case TWW:
                return LeontisWesthof.TWW;
            case TWH:
                return LeontisWesthof.THW;
            case TWS:
                return LeontisWesthof.TSW;
            case THW:
                return LeontisWesthof.TWH;
            case THH:
                return LeontisWesthof.THH;
            case THS:
                return LeontisWesthof.TSH;
            case TSW:
                return LeontisWesthof.TWS;
            case TSH:
                return LeontisWesthof.THS;
            case TSS:
                return LeontisWesthof.TSS;
            case UNKNOWN:
            default:
                return LeontisWesthof.UNKNOWN;
        }
    }
}
