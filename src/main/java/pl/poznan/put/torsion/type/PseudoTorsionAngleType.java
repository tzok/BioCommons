package pl.poznan.put.torsion.type;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.types.Quadruplet;

/*
 * A pseudotorsion angle is defined on atoms which are not covalently bound.
 */
public abstract class PseudoTorsionAngleType extends AtomBasedTorsionAngleType {
    public PseudoTorsionAngleType(MoleculeType moleculeType, String displayName, Quadruplet<AtomName> atoms, Quadruplet<Integer> residueRule) {
        super(moleculeType, displayName, atoms, residueRule);
    }
}
