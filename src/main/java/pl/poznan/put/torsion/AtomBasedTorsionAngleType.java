package pl.poznan.put.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.types.Quadruplet;

import java.util.List;
import java.util.Objects;

public abstract class AtomBasedTorsionAngleType extends TorsionAngleType {
    private final String displayName;
    private final Quadruplet<AtomName> atoms;
    private final Quadruplet<Integer> residueRule;

    protected AtomBasedTorsionAngleType(
            final MoleculeType moleculeType, final String displayName,
            final Quadruplet<AtomName> atoms,
            final Quadruplet<Integer> residueRule) {
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
        return String.format("%s(%s)%s-%s-%s-%s", displayName, getExportName(), atoms.a, atoms.b, atoms.c, atoms.d);
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
        result = (prime * result) + ((atoms == null) ? 0 : atoms.hashCode());
        result = (prime * result) + ((displayName == null) ? 0 : displayName
                .hashCode());
        result = (prime * result) + ((residueRule == null) ? 0 : residueRule
                .hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AtomBasedTorsionAngleType other = (AtomBasedTorsionAngleType) obj;
        if (atoms == null) {
            if (other.atoms != null) {
                return false;
            }
        } else if (!Objects.equals(atoms, other.atoms)) {
            return false;
        }
        if (displayName == null) {
            if (other.displayName != null) {
                return false;
            }
        } else if (!Objects.equals(displayName, other.displayName)) {
            return false;
        }
        if (residueRule == null) {
            if (other.residueRule != null) {
                return false;
            }
        } else if (!Objects.equals(residueRule, other.residueRule)) {
            return false;
        }
        return true;
    }

    @Override
    public TorsionAngleValue calculate(
            final List<PdbResidue> residues, final int currentIndex) {
        final PdbAtomLine[] foundAtoms = new PdbAtomLine[4];

        for (int i = 0; i < 4; i++) {
            final int index = currentIndex + residueRule.get(i);
            if ((index < 0) || (index >= residues.size())) {
                return TorsionAngleValue.invalidInstance(this);
            }

            final PdbResidue residue = residues.get(index);
            if (!residue.hasAtom(atoms.get(i))) {
                return TorsionAngleValue.invalidInstance(this);
            }

            foundAtoms[i] = residue.findAtom(atoms.get(i));
        }

        return new TorsionAngleValue(this, TorsionAnglesHelper
                .calculateTorsionAngle(foundAtoms[0], foundAtoms[1],
                                       foundAtoms[2], foundAtoms[3]));
    }

    public TorsionAngleValue calculate(
            final PdbAtomLine a1, final PdbAtomLine a2, final PdbAtomLine a3,
            final PdbAtomLine a4) {
        return new TorsionAngleValue(this, TorsionAnglesHelper
                .calculateTorsionAngle(a1, a2, a3, a4));
    }
}
