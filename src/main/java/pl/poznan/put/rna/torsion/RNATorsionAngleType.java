package pl.poznan.put.rna.torsion;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.rna.torsion.range.ChiRange;
import pl.poznan.put.rna.torsion.range.Pseudorotation;
import pl.poznan.put.torsion.AverageTorsionAngleType;
import pl.poznan.put.torsion.ImmutableAtomBasedTorsionAngleType;
import pl.poznan.put.torsion.ImmutableAverageTorsionAngleType;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.RangeProvider;
import pl.poznan.put.torsion.range.TorsionRange;
import pl.poznan.put.types.ImmutableQuadruplet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum RNATorsionAngleType implements MasterTorsionAngleType {
  ALPHA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.ALPHA,
          "alpha",
          ImmutableQuadruplet.of(AtomName.O3p, AtomName.P, AtomName.O5p, AtomName.C5p),
          ImmutableQuadruplet.of(-1, 0, 0, 0))),
  BETA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.BETA,
          "beta",
          ImmutableQuadruplet.of(AtomName.P, AtomName.O5p, AtomName.C5p, AtomName.C4p),
          ImmutableQuadruplet.of(0, 0, 0, 0))),
  GAMMA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.GAMMA,
          "gamma",
          ImmutableQuadruplet.of(AtomName.O5p, AtomName.C5p, AtomName.C4p, AtomName.C3p),
          ImmutableQuadruplet.of(0, 0, 0, 0))),
  DELTA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.DELTA,
          "delta",
          ImmutableQuadruplet.of(AtomName.C5p, AtomName.C4p, AtomName.C3p, AtomName.O3p),
          ImmutableQuadruplet.of(0, 0, 0, 0))),
  EPSILON(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.EPSILON,
          "epsilon",
          ImmutableQuadruplet.of(AtomName.C4p, AtomName.C3p, AtomName.O3p, AtomName.P),
          ImmutableQuadruplet.of(0, 0, 0, 1))),
  ZETA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.ZETA,
          "zeta",
          ImmutableQuadruplet.of(AtomName.C3p, AtomName.O3p, AtomName.P, AtomName.O5p),
          ImmutableQuadruplet.of(0, 0, 1, 1))),
  NU0(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.NU0,
          "nu0",
          ImmutableQuadruplet.of(AtomName.C4p, AtomName.O4p, AtomName.C1p, AtomName.C2p),
          ImmutableQuadruplet.of(0, 0, 0, 0))),
  NU1(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.NU1,
          "nu1",
          ImmutableQuadruplet.of(AtomName.O4p, AtomName.C1p, AtomName.C2p, AtomName.C3p),
          ImmutableQuadruplet.of(0, 0, 0, 0))),
  NU2(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.NU2,
          "nu2",
          ImmutableQuadruplet.of(AtomName.C1p, AtomName.C2p, AtomName.C3p, AtomName.C4p),
          ImmutableQuadruplet.of(0, 0, 0, 0))),
  NU3(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.NU3,
          "nu3",
          ImmutableQuadruplet.of(AtomName.C2p, AtomName.C3p, AtomName.C4p, AtomName.O4p),
          ImmutableQuadruplet.of(0, 0, 0, 0))),
  NU4(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.NU4,
          "nu4",
          ImmutableQuadruplet.of(AtomName.C3p, AtomName.C4p, AtomName.O4p, AtomName.C1p),
          ImmutableQuadruplet.of(0, 0, 0, 0))),
  ETA(
      ImmutableAtomBasedTorsionAngleType.builder()
          .moleculeType(MoleculeType.RNA)
          .shortDisplayName(Unicode.ETA)
          .exportName("eta")
          .atoms(ImmutableQuadruplet.of(AtomName.C4p, AtomName.P, AtomName.C4p, AtomName.P))
          .residueRule(ImmutableQuadruplet.of(-1, 0, 0, 1))
          .isPseudoTorsion(true)
          .build()),
  THETA(
      ImmutableAtomBasedTorsionAngleType.builder()
          .moleculeType(MoleculeType.RNA)
          .shortDisplayName(Unicode.THETA)
          .exportName("theta")
          .atoms(ImmutableQuadruplet.of(AtomName.P, AtomName.C4p, AtomName.P, AtomName.C4p))
          .residueRule(ImmutableQuadruplet.of(0, 0, 1, 1))
          .isPseudoTorsion(true)
          .build()),
  ETA_PRIM(
      ImmutableAtomBasedTorsionAngleType.builder()
          .moleculeType(MoleculeType.RNA)
          .shortDisplayName(Unicode.ETA_PRIM)
          .exportName("eta-prim")
          .atoms(ImmutableQuadruplet.of(AtomName.C1p, AtomName.P, AtomName.C1p, AtomName.P))
          .residueRule(ImmutableQuadruplet.of(-1, 0, 0, 1))
          .isPseudoTorsion(true)
          .build()),
  THETA_PRIM(
      ImmutableAtomBasedTorsionAngleType.builder()
          .moleculeType(MoleculeType.RNA)
          .shortDisplayName(Unicode.THETA_PRIM)
          .exportName("theta-prim")
          .atoms(ImmutableQuadruplet.of(AtomName.P, AtomName.C1p, AtomName.P, AtomName.C1p))
          .residueRule(ImmutableQuadruplet.of(0, 0, 1, 1))
          .isPseudoTorsion(true)
          .build()),
  CHI(ChiRange.getProvider(), Chi.PURINE_CHI, Chi.PYRIMIDINE_CHI),
  PSEUDOPHASE_PUCKER(Pseudorotation.getProvider(), ImmutablePseudophasePuckerType.of());

  private static final List<RNATorsionAngleType> MAIN =
      Arrays.asList(
          RNATorsionAngleType.ALPHA,
          RNATorsionAngleType.BETA,
          RNATorsionAngleType.GAMMA,
          RNATorsionAngleType.DELTA,
          RNATorsionAngleType.EPSILON,
          RNATorsionAngleType.ZETA,
          RNATorsionAngleType.CHI);
  private static final AverageTorsionAngleType AVERAGE_TORSION_INSTANCE =
      ImmutableAverageTorsionAngleType.of(MoleculeType.RNA, RNATorsionAngleType.MAIN);
  private final RangeProvider rangeProvider;
  private final List<TorsionAngleType> angleTypes;

  RNATorsionAngleType(final TorsionAngleType... angleTypes) {
    this(TorsionRange.getProvider(), angleTypes);
  }

  RNATorsionAngleType(final RangeProvider rangeProvider, final TorsionAngleType... angleTypes) {
    this.rangeProvider = rangeProvider;
    this.angleTypes = Arrays.asList(angleTypes);
  }

  public static AverageTorsionAngleType getAverageOverMainAngles() {
    return RNATorsionAngleType.AVERAGE_TORSION_INSTANCE;
  }

  @Override
  public List<TorsionAngleType> angleTypes() {
    return Collections.unmodifiableList(angleTypes);
  }

  @Override
  public Range range(final Angle angle) {
    return rangeProvider.fromAngle(angle);
  }

  @Override
  public String shortDisplayName() {
    assert !angleTypes.isEmpty();
    return angleTypes.get(0).shortDisplayName();
  }

  @Override
  public String longDisplayName() {
    assert !angleTypes.isEmpty();
    return angleTypes.get(0).longDisplayName();
  }

  @Override
  public String exportName() {
    assert !angleTypes.isEmpty();
    return angleTypes.get(0).exportName();
  }
}
