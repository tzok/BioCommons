package pl.poznan.put.rna.base;

import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.Base;
import pl.poznan.put.torsion.TorsionAngleType;

import java.util.List;

public enum NucleobaseType implements ResidueInformationProvider {
  ADENINE(Adenine.getInstance()),
  CYTOSINE(Cytosine.getInstance()),
  GUANINE(Guanine.getInstance()),
  URACIL(Uracil.getInstance()),
  THYMINE(Thymine.getInstance());

  private final Base base;

  NucleobaseType(final Base base) {
    this.base = base;
  }

  public Base getBaseInstance() {
    return base;
  }

  @Override
  public MoleculeType moleculeType() {
    return base.moleculeType();
  }

  @Override
  public List<ResidueComponent> moleculeComponents() {
    return base.moleculeComponents();
  }

  @Override
  public String description() {
    return base.description();
  }

  @Override
  public char oneLetterName() {
    return base.oneLetterName();
  }

  @Override
  public String defaultPdbName() {
    return base.defaultPdbName();
  }

  @Override
  public List<String> allPdbNames() {
    return base.allPdbNames();
  }

  @Override
  public List<TorsionAngleType> torsionAngleTypes() {
    return base.torsionAngleTypes();
  }
}
