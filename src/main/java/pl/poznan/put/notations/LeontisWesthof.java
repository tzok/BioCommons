package pl.poznan.put.notations;

public enum LeontisWesthof {
    CWW, CWH, CWS, CHW, CHH, CHS, CSW, CSH, CSS, TWW, TWH, TWS, THW, THH, THS, TSW, TSH, TSS, UNKNOWN;

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

        StringBuilder sb = new StringBuilder();
        sb.append(cs[1]);
        sb.append('/');
        sb.append(cs[2]);
        sb.append(' ');
        sb.append(cs[0] == 'C' ? "cis" : "trans");
        return sb.toString();
    }

    public String getFullName() {
        if (this == UNKNOWN) {
            return "n/a";
        }

        char[] cs = name().toCharArray();

        StringBuilder builder = new StringBuilder();
        builder.append(cs[0] == 'C' ? "cis " : "trans ");
        builder.append(cs[1] == 'W' ? "Watson-Crick" : cs[1] == 'H'
                ? "Hoogsteen" : "Sugar Edge");
        builder.append('/');
        builder.append(cs[2] == 'W' ? "Watson-Crick" : cs[2] == 'H'
                ? "Hoogsteen" : "Sugar Edge");
        return builder.toString();
    }
}
