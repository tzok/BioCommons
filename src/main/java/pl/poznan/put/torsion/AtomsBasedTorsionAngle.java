package pl.poznan.put.torsion;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.types.UniTypeQuadruplet;

public interface AtomsBasedTorsionAngle extends TorsionAngle {
    UniTypeQuadruplet<AtomName> getAtoms();

    UniTypeQuadruplet<Integer> getResidueRule();
}
