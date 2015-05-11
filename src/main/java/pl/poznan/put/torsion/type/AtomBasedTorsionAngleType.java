package pl.poznan.put.torsion.type;

import java.util.List;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.torsion.TorsionAngleValue;
import pl.poznan.put.torsion.TorsionAnglesHelper;
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

    @Override
    public TorsionAngleValue calculate(List<PdbResidue> residues,
            int currentIndex) throws InvalidCircularValueException {
        PdbAtomLine[] foundAtoms = new PdbAtomLine[4];

        for (int i = 0; i < 4; i++) {
            int index = currentIndex + residueRule.get(i);
            if (index < 0 || index >= residues.size()) {
                return TorsionAngleValue.invalidInstance(this);
            }

            PdbResidue residue = residues.get(index);
            if (!residue.hasAtom(atoms.get(i))) {
                return TorsionAngleValue.invalidInstance(this);
            }

            foundAtoms[i] = residue.findAtom(atoms.get(i));
        }

        return new TorsionAngleValue(this, TorsionAnglesHelper.calculateTorsionAngle(foundAtoms[0], foundAtoms[1], foundAtoms[2], foundAtoms[3]));
    }

    public TorsionAngleValue calculate(PdbAtomLine a1, PdbAtomLine a2,
            PdbAtomLine a3, PdbAtomLine a4) {
        return new TorsionAngleValue(this, TorsionAnglesHelper.calculateTorsionAngle(a1, a2, a3, a4));
    }
}
