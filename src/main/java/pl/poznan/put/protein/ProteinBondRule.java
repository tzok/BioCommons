package pl.poznan.put.protein;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Calc;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.StructureException;

import pl.poznan.put.atoms.AtomName;
import pl.poznan.put.atoms.AtomType;
import pl.poznan.put.atoms.Bonds;
import pl.poznan.put.common.ResidueBondRule;
import pl.poznan.put.helper.Helper;

public class ProteinBondRule implements ResidueBondRule {
    @Override
    public boolean areConnected(Group r1, Group r2) {
        Atom c = Helper.findAtom(r1, AtomName.C);
        Atom n = Helper.findAtom(r2, AtomName.N);

        try {
            if (c != null && n != null) {
                double distance = Calc.getDistance(c, n);
                if (distance <= Bonds.length(AtomType.C, AtomType.N).getMax() * 1.5) {
                    return true;
                }
            }
        } catch (StructureException e) {
            // do nothing
        }

        return false;
    }
}
