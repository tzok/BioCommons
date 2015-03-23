package pl.poznan.put.rna;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Calc;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.StructureException;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.AtomType;
import pl.poznan.put.atom.Bond;
import pl.poznan.put.common.ResidueBondRule;
import pl.poznan.put.structure.tertiary.StructureHelper;

public class RNABondRule implements ResidueBondRule {
    @Override
    public boolean areConnected(Group r1, Group r2) {
        Atom o3p = StructureHelper.findAtom(r1, AtomName.O3p);
        Atom p = StructureHelper.findAtom(r2, AtomName.P);

        try {
            if (o3p != null && p != null) {
                double distance = Calc.getDistance(o3p, p);
                if (distance <= Bond.length(AtomType.O, AtomType.P).getMax() * 1.5) {
                    return true;
                }
            }
        } catch (StructureException e) {
            // do nothing
        }

        return false;
    }
}
