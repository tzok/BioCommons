package pl.poznan.put.rna;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.torsion.Alpha;
import pl.poznan.put.rna.torsion.Beta;
import pl.poznan.put.rna.torsion.Delta;
import pl.poznan.put.rna.torsion.Epsilon;
import pl.poznan.put.rna.torsion.Eta;
import pl.poznan.put.rna.torsion.EtaPrim;
import pl.poznan.put.rna.torsion.Gamma;
import pl.poznan.put.rna.torsion.Nu0;
import pl.poznan.put.rna.torsion.Nu1;
import pl.poznan.put.rna.torsion.Nu2;
import pl.poznan.put.rna.torsion.Nu3;
import pl.poznan.put.rna.torsion.Nu4;
import pl.poznan.put.rna.torsion.PseudophasePuckerType;
import pl.poznan.put.rna.torsion.Theta;
import pl.poznan.put.rna.torsion.ThetaPrim;
import pl.poznan.put.rna.torsion.Zeta;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;
import pl.poznan.put.types.Quadruplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Base extends NucleicAcidResidueComponent
    implements ResidueInformationProvider {
  private static final Base INVALID =
      new Base(Collections.emptyList(), "UNK", 'X', "UNK") {
        private final Quadruplet<AtomName> chiAtoms =
            ImmutableQuadruplet.<AtomName>builder()
                .a(AtomName.UNKNOWN)
                .b(AtomName.UNKNOWN)
                .c(AtomName.UNKNOWN)
                .d(AtomName.UNKNOWN)
                .build();

        @Override
        public List<TorsionAngleType> getTorsionAngleTypes() {
          return Collections.emptyList();
        }

        @Override
        public Sugar getDefaultSugarInstance() {
          return Sugar.invalidInstance();
        }

        @Override
        public Quadruplet<AtomName> getChiAtoms() {
          return chiAtoms;
        }
      };

  final List<TorsionAngleType> torsionAngleTypes = new ArrayList<>();
  private final String longName;
  private final char oneLetterName;
  private final List<String> pdbNames;

  Base(
      final List<AtomName> atoms,
      final String longName,
      final char oneLetterName,
      final String... pdbNames) {
    super(RNAResidueComponentType.BASE, atoms);
    this.longName = longName;
    this.oneLetterName = oneLetterName;
    this.pdbNames = Arrays.asList(pdbNames);

    torsionAngleTypes.addAll(
        Arrays.asList(
            Alpha.getInstance(),
            Beta.getInstance(),
            Gamma.getInstance(),
            Delta.getInstance(),
            Epsilon.getInstance(),
            Zeta.getInstance(),
            Nu0.getInstance(),
            Nu1.getInstance(),
            Nu2.getInstance(),
            Nu3.getInstance(),
            Nu4.getInstance(),
            Eta.getInstance(),
            Theta.getInstance(),
            EtaPrim.getInstance(),
            ThetaPrim.getInstance(),
            PseudophasePuckerType.getInstance()));
  }

  public static Base invalidInstance() {
    return Base.INVALID;
  }

  @Override
  public final List<ResidueComponent> getAllMoleculeComponents() {
    return Arrays.asList(Phosphate.getInstance(), getDefaultSugarInstance(), this);
  }

  @Override
  public final String getDescription() {
    return longName;
  }

  @Override
  public final char getOneLetterName() {
    return oneLetterName;
  }

  @Override
  public final String getDefaultPdbName() {
    assert !pdbNames.isEmpty();
    return pdbNames.get(0);
  }

  @Override
  public final List<String> getPdbNames() {
    return Collections.unmodifiableList(pdbNames);
  }

  @Override
  public List<TorsionAngleType> getTorsionAngleTypes() {
    return Collections.unmodifiableList(torsionAngleTypes);
  }

  protected abstract Sugar getDefaultSugarInstance();

  public abstract Quadruplet<AtomName> getChiAtoms();
}
