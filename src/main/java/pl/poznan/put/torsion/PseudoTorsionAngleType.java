package pl.poznan.put.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.types.Quadruplet;

/*
 * A pseudotorsion angle is defined on atoms which are not covalently bound.
 */
public abstract class PseudoTorsionAngleType extends AtomBasedTorsionAngleType {
    protected PseudoTorsionAngleType(
            final MoleculeType moleculeType, final String displayName,
            final Quadruplet<AtomName> atoms,
            final Quadruplet<Integer> residueRule) {
        super(moleculeType, displayName, atoms, residueRule);
    }
}
