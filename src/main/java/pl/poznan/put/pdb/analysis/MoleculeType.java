package pl.poznan.put.pdb.analysis;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.AtomType;
import pl.poznan.put.atom.Bond;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.protein.AminoAcidTorsionAngle;
import pl.poznan.put.rna.NucleotideTorsionAngle;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.torsion.MasterTorsionAngleType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** A type of molecule (RNA or protein). */
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

  /**
   * Checks if two residues are connected. For nucleotides, the check is on O3'-P bond. For amino
   * acids, the check is on C-N bond.
   *
   * @param r1 The first residue.
   * @param r2 The second residue.
   * @return True if residues are connected.
   */
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

  /**
   * Generates a list of all atom-based torsion angle types for this molecule type.
   *
   * @return A list of atom-based torsion angle types.
   */
  public List<MasterTorsionAngleType> allAngleTypes() {
    switch (this) {
      case RNA:
        return Arrays.stream(NucleotideTorsionAngle.values())
            .filter(
                masterType ->
                    masterType.angleTypes().stream()
                        .allMatch(angleType -> angleType instanceof AtomBasedTorsionAngleType))
            .collect(Collectors.toList());
      case PROTEIN:
        return Arrays.stream(AminoAcidTorsionAngle.values())
            .filter(
                masterType ->
                    masterType.angleTypes().stream()
                        .allMatch(angleType -> angleType instanceof AtomBasedTorsionAngleType))
            .collect(Collectors.toList());
      case UNKNOWN:
      default:
        return Collections.emptyList();
    }
  }

  /**
   * Generates a list of main atom-based torsion angle types for this molecule type.
   *
   * @return A list of atom-based torsion angle types.
   */
  public List<MasterTorsionAngleType> mainAngleTypes() {
    switch (this) {
      case RNA:
        return Stream.of(
                NucleotideTorsionAngle.ALPHA,
                NucleotideTorsionAngle.BETA,
                NucleotideTorsionAngle.GAMMA,
                NucleotideTorsionAngle.DELTA,
                NucleotideTorsionAngle.EPSILON,
                NucleotideTorsionAngle.ZETA,
                NucleotideTorsionAngle.CHI)
            .collect(Collectors.toList());
      case PROTEIN:
        return Stream.of(
                AminoAcidTorsionAngle.PHI, AminoAcidTorsionAngle.PSI, AminoAcidTorsionAngle.OMEGA)
            .collect(Collectors.toList());
      case UNKNOWN:
      default:
        return Collections.emptyList();
    }
  }
}
