package pl.poznan.put.common;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.helper.UniTypeQuadruplet;

public interface AtomsBasedTorsionAngle extends TorsionAngle {
    UniTypeQuadruplet<AtomName> getAtoms();

    UniTypeQuadruplet<Integer> getResidueRule();
}
