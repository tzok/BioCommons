package pl.poznan.put.protein;

import java.util.List;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.torsion.TorsionAngleType;

/** An amino acid with all details regarding its atoms, torsion angles, etc. */
public enum AminoAcid implements ResidueInformationProvider {
  ALANINE(ImmutableAlanine.of()),
  ARGININE(ImmutableArginine.of()),
  ASPARAGINE(ImmutableAsparagine.of()),
  ASPARTIC_ACID(ImmutableAsparticAcid.of()),
  CYSTEINE(ImmutableCysteine.of()),
  GLUTAMIC_ACID(ImmutableGlutamicAcid.of()),
  GLUTAMINE(ImmutableGlutamine.of()),
  GLYCINE(ImmutableGlycine.of()),
  HISTIDINE(ImmutableHistidine.of()),
  ISOLEUCINE(ImmutableIsoLeucine.of()),
  LEUCINE(ImmutableLeucine.of()),
  LYSINE(ImmutableLysine.of()),
  METHIONINE(ImmutableMethionine.of()),
  PHENYLALANINE(ImmutablePhenylalanine.of()),
  PROLINE(ImmutableProline.of()),
  SERINE(ImmutableSerine.of()),
  THREONINE(ImmutableThreonine.of()),
  TRYPTOPHAN(ImmutableTryptophan.of()),
  TYROSINE(ImmutableTyrosine.of()),
  VALINE(ImmutableValine.of());

  private final Sidechain sidechain;

  AminoAcid(final Sidechain sidechain) {
    this.sidechain = sidechain;
  }

  /**
   * @return The sidechain instance.
   */
  public Sidechain sidechain() {
    return sidechain;
  }

  @Override
  public MoleculeType moleculeType() {
    return MoleculeType.PROTEIN;
  }

  @Override
  public List<ResidueComponent> moleculeComponents() {
    return sidechain.moleculeComponents();
  }

  @Override
  public char oneLetterName() {
    return sidechain.oneLetterName();
  }

  @Override
  public List<String> aliases() {
    return sidechain.aliases();
  }

  @Override
  public List<TorsionAngleType> torsionAngleTypes() {
    return sidechain.torsionAngleTypes();
  }
}
