package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.rna.Nucleotide;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** A residue (nucleotide or amino acid). */
public interface PdbResidue extends ChainNumberICode {
  /** @return The identifier of a residue. */
  PdbResidueIdentifier identifier();

  /**
   * @return The usual name of the residue. For example, a pseudouridine will be seen as uracil
   *     here.
   */
  String standardResidueName();

  /** @return The name of the residue as read from PDB or mmCIF file. */
  String modifiedResidueName();

  /** @return The list of atoms. */
  List<PdbAtomLine> atoms();

  /** @return A text representation of this residue in PDB format. */
  default String toPdb() {
    return atoms().stream().map(String::valueOf).collect(Collectors.joining("\n"));
  }

  /** @return A text representation of this residue in mmCIF format. */
  default String toCif() {
    return atoms().stream().map(PdbAtomLine::toCif).collect(Collectors.joining("\n"));
  }

  /** @return True if the list of atoms is empty. */
  default boolean isMissing() {
    return atoms().isEmpty();
  }

  /**
   * Detects the type of residue by its name and atom content.
   *
   * @return An instance with details about what this reside represents.
   */
  default ResidueInformationProvider residueInformationProvider() {
    return ResidueTypeDetector.detectResidueType(modifiedResidueName(), atomNames());
  }

  /** @return A one letter name which is lowercase for modified residues and uppercase otherwise. */
  default char oneLetterName() {
    return isModified()
        ? Character.toLowerCase(residueInformationProvider().oneLetterName())
        : residueInformationProvider().oneLetterName();
  }

  /**
   * Checks if this residue is connected with another one (see {@link
   * MoleculeType#areConnected(PdbResidue, PdbResidue)}).
   *
   * @param other The other residue.
   * @return True if this residue and the other one are connected.
   */
  default boolean isConnectedTo(final PdbResidue other) {
    return residueInformationProvider().moleculeType().areConnected(this, other);
  }

  @Override
  default String chainIdentifier() {
    return identifier().chainIdentifier();
  }

  @Override
  default int residueNumber() {
    return identifier().residueNumber();
  }

  @Override
  default String insertionCode() {
    return identifier().insertionCode();
  }

  /**
   * Finds an atom of a given name.
   *
   * @param atomName An atom name.
   * @return An instance of atom (with coordinates) of the given type.
   */
  default PdbAtomLine findAtom(final AtomName atomName) {
    return atoms().stream()
        .filter(atom -> atom.detectAtomName() == atomName)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Failed to find: " + atomName));
  }

  /**
   * Calculates the plane equation of the nucleobase in this residue. This method works only for
   * nucleotides and will throw an {@code IllegalArgumentException} for amino acids. The plane for
   * purines (A or G) is based on N9, C2 and C6 atoms. The plane for pyrimidines (C, U or T) is
   * based on N1, N3 and C5 atoms.
   *
   * @return The plane of the nucleobase.
   */
  default Plane nucleobasePlane() {
    if (residueInformationProvider() instanceof Nucleotide) {
      switch ((Nucleotide) residueInformationProvider()) {
        case ADENINE:
        case GUANINE:
          return new Plane(
              findAtom(AtomName.N9).toVector3D(),
              findAtom(AtomName.C2).toVector3D(),
              findAtom(AtomName.C6).toVector3D(),
              1.0e-3);
        case CYTOSINE:
        case URACIL:
        case THYMINE:
          return new Plane(
              findAtom(AtomName.N1).toVector3D(),
              findAtom(AtomName.N3).toVector3D(),
              findAtom(AtomName.C5).toVector3D(),
              1.0e-3);
      }
    }

    throw new IllegalArgumentException(
        "Cannot compute base plane for not a nucleotide: " + identifier());
  }

  /** @return The instance of named identifier. */
  default PdbNamedResidueIdentifier namedResidueIdentifier() {
    return ImmutablePdbNamedResidueIdentifier.of(
        identifier().chainIdentifier(),
        identifier().residueNumber(),
        identifier().insertionCode(),
        isModified() ? Character.toLowerCase(oneLetterName()) : oneLetterName());
  }

  /** @return The set of all atom names available in this residue. */
  default Set<AtomName> atomNames() {
    return atoms().stream().map(PdbAtomLine::detectAtomName).collect(Collectors.toSet());
  }

  /**
   * Checks whether this residue has atom of the given name.
   *
   * @param atomName The atom name.
   * @return True if this residue has an atom of the given name.
   */
  default boolean hasAtom(final AtomName atomName) {
    return atomNames().contains(atomName);
  }

  /** @return True if there is any hydrogen atom available in this residue. */
  default boolean hasAnyHydrogen() {
    return atomNames().stream().anyMatch(atomName -> !atomName.isHeavy());
  }

  /**
   * Compares the set of actual atoms in this residue with the set of expected atoms derived from
   * the detected type of residue.
   *
   * @return True if all expected heavy atoms (non-hydrogen) for this residue type are present in
   *     this residue.
   */
  default boolean hasAllHeavyAtoms() {
    final Set<AtomName> heavyAtoms =
        atomNames().stream().filter(AtomName::isHeavy).collect(Collectors.toSet());
    final Set<AtomName> expectedHeavyAtoms =
        residueInformationProvider().moleculeComponents().stream()
            .map(ResidueComponent::requiredAtoms)
            .flatMap(Collection::stream)
            .filter(AtomName::isHeavy)
            .collect(Collectors.toSet());
    return SetUtils.isEqualSet(heavyAtoms, expectedHeavyAtoms);
  }

  /**
   * @return True if the standard and modified residue names differ or if there are some atoms in
   *     the residue, but they do not match the expected set of atoms.
   */
  default boolean isModified() {
    return !Objects.equals(standardResidueName(), modifiedResidueName())
        || (!isMissing() && !hasAllHeavyAtoms());
  }
}
