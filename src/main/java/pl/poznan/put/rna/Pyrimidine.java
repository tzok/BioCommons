package pl.poznan.put.rna;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.rna.torsion.Chi;
import pl.poznan.put.types.Quadruplet;

import java.util.List;

public abstract class Pyrimidine extends Base {
  protected Pyrimidine(
      final List<AtomName> atoms,
      final String longName,
      final char oneLetterName,
      final String... names) {
    super(atoms, longName, oneLetterName, names);
    torsionAngleTypes.add(Chi.PYRIMIDINE_CHI);
  }

  @Override
  public final Quadruplet<AtomName> getChiAtoms() {
    return Chi.PYRIMIDINE_ATOMS;
  }
}
