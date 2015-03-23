package pl.poznan.put.torsion.type;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.types.Quadruplet;

public abstract class AtomBasedTorsionAngleType extends TorsionAngleType {
    private final String displayName;
    private final Quadruplet<AtomName> atoms;
    private final Quadruplet<Integer> residueRule;

    public AtomBasedTorsionAngleType(MoleculeType moleculeType, String displayName, Quadruplet<AtomName> atoms, Quadruplet<Integer> residueRule) {
        super(moleculeType);
        this.displayName = displayName;
        this.atoms = atoms;
        this.residueRule = residueRule;
    }

    public Quadruplet<AtomName> getAtoms() {
        return atoms;
    }

    public Quadruplet<Integer> getResidueRule() {
        return residueRule;
    }

    @Override
    public String getLongDisplayName() {
        return displayName + "(" + getExportName() + ")" + atoms.a + "-" + atoms.b + "-" + atoms.c + "-" + atoms.d;
    }

    @Override
    public String getShortDisplayName() {
        return displayName;
    }

    @Override
    public String getExportName() {
        return getClass().getSimpleName().toLowerCase();
    }
}
