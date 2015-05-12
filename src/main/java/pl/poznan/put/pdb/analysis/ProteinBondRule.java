package pl.poznan.put.pdb.analysis;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.AtomType;
import pl.poznan.put.atom.Bond;
import pl.poznan.put.pdb.PdbAtomLine;

public class ProteinBondRule implements ResidueBondRule {
    @Override
    public boolean areConnected(PdbResidue r1, PdbResidue r2) {
        if (!r1.hasAtom(AtomName.C) || !r2.hasAtom(AtomName.N)) {
            return false;
        }

        PdbAtomLine c = r1.findAtom(AtomName.C);
        PdbAtomLine n = r2.findAtom(AtomName.N);
        return c.distanceTo(n) <= Bond.length(AtomType.C, AtomType.N).getMax() * 1.5;
    }
}
