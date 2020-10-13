package pl.poznan.put.rna;

import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.torsion.TorsionAngleType;

import java.util.List;

/** A nucleotide (A, C, G, U or T) with all details regarding atoms, torsion angles, etc. */
public enum Nucleotide implements ResidueInformationProvider {
  ADENINE(ImmutableAdenine.of()),
  CYTOSINE(ImmutableCytosine.of()),
  GUANINE(ImmutableGuanine.of()),
  URACIL(ImmutableUracil.of()),
  THYMINE(ImmutableThymine.of());

  private final Nucleobase nucleobase;

  Nucleotide(final Nucleobase nucleobase) {
    this.nucleobase = nucleobase;
  }

  /** @return An instance of nucleobase in this nucleotide. */
  public Nucleobase nucleobase() {
    return nucleobase;
  }

  @Override
  public MoleculeType moleculeType() {
    return MoleculeType.RNA;
  }

  @Override
  public List<ResidueComponent> moleculeComponents() {
    return nucleobase.moleculeComponents();
  }

  @Override
  public char oneLetterName() {
    return nucleobase.oneLetterName();
  }

  @Override
  public List<String> aliases() {
    return nucleobase.aliases();
  }

  @Override
  public List<TorsionAngleType> torsionAngleTypes() {
    return nucleobase.torsionAngleTypes();
  }
}
