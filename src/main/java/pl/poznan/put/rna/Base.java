package pl.poznan.put.rna;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;
import pl.poznan.put.rna.torsion.RNATorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.types.ImmutableQuadruplet;
import pl.poznan.put.types.Quadruplet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        public Quadruplet<AtomName> getChiAtoms() {
          return chiAtoms;
        }

        @Override
        public Sugar getDefaultSugarInstance() {
          return Sugar.invalidInstance();
        }

        @Override
        public List<TorsionAngleType> torsionAngleTypes() {
          return Collections.emptyList();
        }
      };

  final List<TorsionAngleType> torsionAngleTypes =
      Stream.of(
              RNATorsionAngleType.ALPHA,
              RNATorsionAngleType.BETA,
              RNATorsionAngleType.GAMMA,
              RNATorsionAngleType.DELTA,
              RNATorsionAngleType.EPSILON,
              RNATorsionAngleType.ZETA,
              RNATorsionAngleType.NU0,
              RNATorsionAngleType.NU1,
              RNATorsionAngleType.NU2,
              RNATorsionAngleType.NU3,
              RNATorsionAngleType.NU4,
              RNATorsionAngleType.ETA,
              RNATorsionAngleType.THETA,
              RNATorsionAngleType.ETA_PRIM,
              RNATorsionAngleType.THETA_PRIM,
              RNATorsionAngleType.PSEUDOPHASE_PUCKER)
          .flatMap(masterType -> masterType.angleTypes().stream())
          .collect(Collectors.toList());

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
  }

  public static Base invalidInstance() {
    return Base.INVALID;
  }

  public abstract Quadruplet<AtomName> getChiAtoms();

  protected abstract Sugar getDefaultSugarInstance();

  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Arrays.asList(Phosphate.getInstance(), getDefaultSugarInstance(), this);
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
  public List<TorsionAngleType> torsionAngleTypes() {
    return Collections.unmodifiableList(torsionAngleTypes);
  }
}
