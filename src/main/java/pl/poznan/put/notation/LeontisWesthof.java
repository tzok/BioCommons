package pl.poznan.put.notation;

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

    public static LeontisWesthof fromString(String str) {
        String lc = str.toLowerCase();

        if ("cww".equals(lc)) {
            return CWW;
        }
        if ("cwh".equals(lc)) {
            return CWH;
        }
        if ("cws".equals(lc)) {
            return CWS;
        }
        if ("chw".equals(lc)) {
            return CHW;
        }
        if ("chh".equals(lc)) {
            return CHH;
        }
        if ("chs".equals(lc)) {
            return CHS;
        }
        if ("csw".equals(lc)) {
            return CSW;
        }
        if ("csh".equals(lc)) {
            return CSH;
        }
        if ("css".equals(lc)) {
            return CSS;
        }
        if ("tww".equals(lc)) {
            return TWW;
        }
        if ("twh".equals(lc)) {
            return TWH;
        }
        if ("tws".equals(lc)) {
            return TWS;
        }
        if ("thw".equals(lc)) {
            return THW;
        }
        if ("thh".equals(lc)) {
            return THH;
        }
        if ("ths".equals(lc)) {
            return THS;
        }
        if ("tsw".equals(lc)) {
            return TSW;
        }
        if ("tsh".equals(lc)) {
            return TSH;
        }
        if ("tss".equals(lc)) {
            return TSS;
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        if (this == UNKNOWN) {
            return "n/a";
        }

        char[] cs = name().toCharArray();

        return String.valueOf(cs[1]) +
                '/' +
                cs[2] +
                ' ' +
                (cs[0] == 'C' ? "cis" : "trans");
    }

    public String getFullName() {
        if (this == UNKNOWN) {
            return "n/a";
        }

        char[] cs = name().toCharArray();

        return (cs[0] == 'C' ? "cis " : "trans ") +
                (cs[1] == 'W' ? "Watson-Crick" : cs[1] == 'H' ? "Hoogsteen" : "Sugar Edge") +
                '/' +
                (cs[2] == 'W' ? "Watson-Crick" : cs[2] == 'H' ? "Hoogsteen" : "Sugar Edge");
    }
}
