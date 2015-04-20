package pl.poznan.put.torsion.type;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.common.MoleculeType;
import pl.poznan.put.types.Quadruplet;

public abstract class AtomBasedTorsionAngleType extends TorsionAngleType {
    private final String displayName;
    private final Quadruplet<AtomName> atoms;
    private final Quadruplet<Integer> residueRule;

    public AtomBasedTorsionAngleType(MoleculeType moleculeType,
            String displayName, Quadruplet<AtomName> atoms,
            Quadruplet<Integer> residueRule) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (atoms == null ? 0 : atoms.hashCode());
        result = prime * result + (displayName == null ? 0 : displayName.hashCode());
        result = prime * result + (residueRule == null ? 0 : residueRule.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AtomBasedTorsionAngleType other = (AtomBasedTorsionAngleType) obj;
        if (atoms == null) {
            if (other.atoms != null) {
                return false;
            }
        } else if (!atoms.equals(other.atoms)) {
            return false;
        }
        if (displayName == null) {
            if (other.displayName != null) {
                return false;
            }
        } else if (!displayName.equals(other.displayName)) {
            return false;
        }
        if (residueRule == null) {
            if (other.residueRule != null) {
                return false;
            }
        } else if (!residueRule.equals(other.residueRule)) {
            return false;
        }
        return true;
    }
}
