package pl.poznan.put.pdb.analysis;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.AtomType;
import pl.poznan.put.atom.Bond;
import pl.poznan.put.pdb.PdbAtomLine;

public enum MoleculeType {
  RNA,
  PROTEIN,
  UNKNOWN;

  private static boolean areNucleotidesConnected(final PdbResidue r1, final PdbResidue r2) {
    if (!r1.hasAtom(AtomName.O3p) || !r2.hasAtom(AtomName.P)) {
      return false;
    }

    final PdbAtomLine o3p = r1.findAtom(AtomName.O3p);
    final PdbAtomLine p = r2.findAtom(AtomName.P);
    return o3p.distanceTo(p) <= (Bond.length(AtomType.O, AtomType.P).max() * 1.5);
  }

  private static boolean areAminoAcidsConnected(final PdbResidue r1, final PdbResidue r2) {
    if (!r1.hasAtom(AtomName.C) || !r2.hasAtom(AtomName.N)) {
      return false;
    }

    final PdbAtomLine c = r1.findAtom(AtomName.C);
    final PdbAtomLine n = r2.findAtom(AtomName.N);
    return c.distanceTo(n) <= (Bond.length(AtomType.C, AtomType.N).max() * 1.5);
  }

  public boolean areConnected(final PdbResidue r1, final PdbResidue r2) {
    switch (this) {
      case RNA:
        return MoleculeType.areNucleotidesConnected(r1, r2);
      case PROTEIN:
        return MoleculeType.areAminoAcidsConnected(r1, r2);
      case UNKNOWN:
      default:
        return false;
    }
  }
}
