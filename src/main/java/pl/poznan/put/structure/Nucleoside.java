package pl.poznan.put.structure;

public class Nucleoside extends Residue {
    private static final long serialVersionUID = -943641369052306989L;

    private boolean isC2;
    private boolean isC6;
    private boolean isN1;

    public Nucleoside(char chain, String residueName, int residueNumber,
            char insertionCode, boolean isMissing) {
        super(chain, residueName, residueNumber, insertionCode, isMissing);
    }

    public boolean isC2() {
        return isC2;
    }

    public boolean isC6() {
        return isC6;
    }

    public boolean isN1() {
        return isN1;
    }

    public void setC2(boolean isC2) {
        this.isC2 = isC2;
    }

    public void setC6(boolean isC6) {
        this.isC6 = isC6;
    }

    public void setN1(boolean isN1) {
        this.isN1 = isN1;
    }
}
