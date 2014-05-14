package pl.poznan.put.common;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.helper.UniTypeQuadruplet;

public class TorsionAngleImpl implements TorsionAngle {
    protected final UniTypeQuadruplet<AtomName> atoms;
    protected final UniTypeQuadruplet<Integer> residueRule;
    protected final String displayName;
    private final String name;

    public TorsionAngleImpl(AtomName a1, AtomName a2, AtomName a3, AtomName a4,
            int r1, int r2, int r3, int r4, String name, String unicodeName) {
        this.atoms = new UniTypeQuadruplet<AtomName>(a1, a2, a3, a4);
        this.residueRule = new UniTypeQuadruplet<Integer>(r1, r2, r3, r4);
        this.displayName = unicodeName + " (" + name + ") " + a1.getName()
                + "-" + a2.getName() + "-" + a3.getName() + "-" + a4.getName();
        this.name = name;
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
    public String toString() {
        return name;
    }
}
