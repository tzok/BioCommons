package pl.poznan.put.protein;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.protein.torsion.Calpha;
import pl.poznan.put.protein.torsion.Omega;
import pl.poznan.put.protein.torsion.Phi;
import pl.poznan.put.protein.torsion.ProteinChiType;
import pl.poznan.put.protein.torsion.Psi;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.types.Quadruplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class ProteinSidechain extends ResidueComponent
    implements ResidueInformationProvider {
  protected final Map<ProteinChiType, Quadruplet<AtomName>> chiAtoms =
      new EnumMap<>(ProteinChiType.class);
  protected final List<TorsionAngleType> torsionAngleTypes = new ArrayList<>();

  private final String longName;
  private final char oneLetterName;
  private final List<String> pdbNames;

  protected ProteinSidechain(
      final List<AtomName> atoms,
      final String longName,
      final char oneLetterName,
      final String... pdbNames) {
    super("sidechain", MoleculeType.PROTEIN, atoms);
    this.longName = longName;
    this.oneLetterName = oneLetterName;
    this.pdbNames = Arrays.asList(pdbNames);

    torsionAngleTypes.add(Phi.getInstance());
    torsionAngleTypes.add(Psi.getInstance());
    torsionAngleTypes.add(Omega.getInstance());
    torsionAngleTypes.add(Calpha.getInstance());
  }

  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Arrays.asList(ProteinBackbone.getInstance(), this);
  }

  @Override
  public final String description() {
    return longName;
  }

  @Override
  public final char oneLetterName() {
    return oneLetterName;
  }

  @Override
  public final String defaultPdbName() {
    assert !pdbNames.isEmpty();
    return pdbNames.get(0);
  }

  @Override
  public final List<String> allPdbNames() {
    return Collections.unmodifiableList(pdbNames);
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Collections.unmodifiableList(torsionAngleTypes);
  }

  public final Quadruplet<AtomName> getChiAtoms(final ProteinChiType chiType) {
    if (!hasChiDefined(chiType)) {
      throw new IllegalArgumentException("Invalid " + chiType + " angle for " + longName);
    }

    return chiAtoms.get(chiType);
  }

  public final boolean hasChiDefined(final ProteinChiType chiType) {
    return chiAtoms.containsKey(chiType);
  }
}
