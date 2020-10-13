package pl.poznan.put.rna;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.torsion.ImmutableAtomBasedTorsionAngleType;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.RangeProvider;
import pl.poznan.put.torsion.range.TorsionRange;
import pl.poznan.put.types.ImmutableQuadruple;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** A torsion angle defined in a nucleotide. */
public enum NucleotideTorsionAngle implements MasterTorsionAngleType {
  ALPHA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.ALPHA,
          "alpha",
          ImmutableQuadruple.of(AtomName.O3p, AtomName.P, AtomName.O5p, AtomName.C5p),
          ImmutableQuadruple.of(-1, 0, 0, 0))),
  BETA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.BETA,
          "beta",
          ImmutableQuadruple.of(AtomName.P, AtomName.O5p, AtomName.C5p, AtomName.C4p),
          ImmutableQuadruple.of(0, 0, 0, 0))),
  GAMMA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.GAMMA,
          "gamma",
          ImmutableQuadruple.of(AtomName.O5p, AtomName.C5p, AtomName.C4p, AtomName.C3p),
          ImmutableQuadruple.of(0, 0, 0, 0))),
  DELTA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.DELTA,
          "delta",
          ImmutableQuadruple.of(AtomName.C5p, AtomName.C4p, AtomName.C3p, AtomName.O3p),
          ImmutableQuadruple.of(0, 0, 0, 0))),
  EPSILON(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.EPSILON,
          "epsilon",
          ImmutableQuadruple.of(AtomName.C4p, AtomName.C3p, AtomName.O3p, AtomName.P),
          ImmutableQuadruple.of(0, 0, 0, 1))),
  ZETA(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.ZETA,
          "zeta",
          ImmutableQuadruple.of(AtomName.C3p, AtomName.O3p, AtomName.P, AtomName.O5p),
          ImmutableQuadruple.of(0, 0, 1, 1))),
  NU0(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.NU0,
          "nu0",
          ImmutableQuadruple.of(AtomName.C4p, AtomName.O4p, AtomName.C1p, AtomName.C2p),
          ImmutableQuadruple.of(0, 0, 0, 0))),
  NU1(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.NU1,
          "nu1",
          ImmutableQuadruple.of(AtomName.O4p, AtomName.C1p, AtomName.C2p, AtomName.C3p),
          ImmutableQuadruple.of(0, 0, 0, 0))),
  NU2(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.NU2,
          "nu2",
          ImmutableQuadruple.of(AtomName.C1p, AtomName.C2p, AtomName.C3p, AtomName.C4p),
          ImmutableQuadruple.of(0, 0, 0, 0))),
  NU3(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.NU3,
          "nu3",
          ImmutableQuadruple.of(AtomName.C2p, AtomName.C3p, AtomName.C4p, AtomName.O4p),
          ImmutableQuadruple.of(0, 0, 0, 0))),
  NU4(
      ImmutableAtomBasedTorsionAngleType.of(
          MoleculeType.RNA,
          Unicode.NU4,
          "nu4",
          ImmutableQuadruple.of(AtomName.C3p, AtomName.C4p, AtomName.O4p, AtomName.C1p),
          ImmutableQuadruple.of(0, 0, 0, 0))),
  ETA(
      ImmutableAtomBasedTorsionAngleType.builder()
          .moleculeType(MoleculeType.RNA)
          .shortDisplayName(Unicode.ETA)
          .exportName("eta")
          .atoms(ImmutableQuadruple.of(AtomName.C4p, AtomName.P, AtomName.C4p, AtomName.P))
          .residueRule(ImmutableQuadruple.of(-1, 0, 0, 1))
          .isPseudoTorsion(true)
          .build()),
  THETA(
      ImmutableAtomBasedTorsionAngleType.builder()
          .moleculeType(MoleculeType.RNA)
          .shortDisplayName(Unicode.THETA)
          .exportName("theta")
          .atoms(ImmutableQuadruple.of(AtomName.P, AtomName.C4p, AtomName.P, AtomName.C4p))
          .residueRule(ImmutableQuadruple.of(0, 0, 1, 1))
          .isPseudoTorsion(true)
          .build()),
  ETA_PRIM(
      ImmutableAtomBasedTorsionAngleType.builder()
          .moleculeType(MoleculeType.RNA)
          .shortDisplayName(Unicode.ETA_PRIM)
          .exportName("eta-prim")
          .atoms(ImmutableQuadruple.of(AtomName.C1p, AtomName.P, AtomName.C1p, AtomName.P))
          .residueRule(ImmutableQuadruple.of(-1, 0, 0, 1))
          .isPseudoTorsion(true)
          .build()),
  THETA_PRIM(
      ImmutableAtomBasedTorsionAngleType.builder()
          .moleculeType(MoleculeType.RNA)
          .shortDisplayName(Unicode.THETA_PRIM)
          .exportName("theta-prim")
          .atoms(ImmutableQuadruple.of(AtomName.P, AtomName.C1p, AtomName.P, AtomName.C1p))
          .residueRule(ImmutableQuadruple.of(0, 0, 1, 1))
          .isPseudoTorsion(true)
          .build()),
  CHI(ChiRange.getProvider(), Chi.PURINE.angleType(), Chi.PYRIMIDINE.angleType()),
  PSEUDOPHASE_PUCKER(Pseudorotation.getProvider(), ImmutablePseudophasePuckerType.of());

  private final RangeProvider rangeProvider;
  private final List<TorsionAngleType> angleTypes;

  NucleotideTorsionAngle(final TorsionAngleType... angleTypes) {
    this(TorsionRange.rangeProvider(), angleTypes);
  }

  NucleotideTorsionAngle(final RangeProvider rangeProvider, final TorsionAngleType... angleTypes) {
    this.rangeProvider = rangeProvider;
    this.angleTypes = Arrays.asList(angleTypes);
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
