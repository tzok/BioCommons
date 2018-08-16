package pl.poznan.put.rna.base;

import java.util.List;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.Base;
import pl.poznan.put.torsion.TorsionAngleType;

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
  public MoleculeType getMoleculeType() {
    return base.getMoleculeType();
  }

  @Override
  public List<ResidueComponent> getAllMoleculeComponents() {
    return base.getAllMoleculeComponents();
  }

  @Override
  public String getDescription() {
    return base.getDescription();
  }

  @Override
  public char getOneLetterName() {
    return base.getOneLetterName();
  }

  @Override
  public String getDefaultPdbName() {
    return base.getDefaultPdbName();
  }

  @Override
  public List<String> getPdbNames() {
    return base.getPdbNames();
  }

  @Override
  public List<TorsionAngleType> getTorsionAngleTypes() {
    return base.getTorsionAngleTypes();
  }
}
