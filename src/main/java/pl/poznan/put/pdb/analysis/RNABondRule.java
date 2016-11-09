package pl.poznan.put.pdb.analysis;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.AtomType;
import pl.poznan.put.atom.Bond;
import pl.poznan.put.pdb.PdbAtomLine;

public class RNABondRule implements ResidueBondRule {
    private static final long serialVersionUID = 2268285422405616742L;

    @Override
    public final boolean areConnected(
            final PdbResidue r1, final PdbResidue r2) {
        if (!r1.hasAtom(AtomName.O3p) || !r2.hasAtom(AtomName.P)) {
            return false;
        }

        PdbAtomLine o3p = r1.findAtom(AtomName.O3p);
        PdbAtomLine p = r2.findAtom(AtomName.P);
        return o3p.distanceTo(p) <= (
                Bond.length(AtomType.O, AtomType.P).getMax() * 1.5);
    }
}
