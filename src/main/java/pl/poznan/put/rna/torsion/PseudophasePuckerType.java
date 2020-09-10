package pl.poznan.put.rna.torsion;

import org.apache.commons.math3.util.FastMath;
import org.immutables.value.Value;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.torsion.ImmutableTorsionAngleValue;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

import java.util.List;
import java.util.stream.Stream;

@Value.Immutable(singleton = true)
public abstract class PseudophasePuckerType implements TorsionAngleType {
  @Override
  public String shortDisplayName() {
    return "P";
  }

  @Override
  public String longDisplayName() {
    return "P";
  }

  @Override
  public String exportName() {
    return "P";
  }

  @Override
  public MoleculeType moleculeType() {
    return MoleculeType.RNA;
  }

  @Override
  public TorsionAngleValue calculate(final List<PdbResidue> residues, final int currentIndex) {
    final TorsionAngleValue nu0 =
        RNATorsionAngleType.NU0.angleTypes().get(0).calculate(residues, currentIndex);
    final TorsionAngleValue nu1 =
        RNATorsionAngleType.NU1.angleTypes().get(0).calculate(residues, currentIndex);
    final TorsionAngleValue nu2 =
        RNATorsionAngleType.NU2.angleTypes().get(0).calculate(residues, currentIndex);
    final TorsionAngleValue nu3 =
        RNATorsionAngleType.NU3.angleTypes().get(0).calculate(residues, currentIndex);
    final TorsionAngleValue nu4 =
        RNATorsionAngleType.NU4.angleTypes().get(0).calculate(residues, currentIndex);

    if (Stream.of(nu0, nu1, nu2, nu3, nu4)
        .anyMatch(torsionAngleValue -> !Double.isNaN(torsionAngleValue.value().radians()))) {
      return ImmutableTorsionAngleValue.of(this, ImmutableAngle.of(Double.NaN));
    }

    final double scale =
        2.0 * (FastMath.sin(Math.toRadians(36.0)) + FastMath.sin(Math.toRadians(72.0)));
    final double y =
        (nu1.value().radians() + nu4.value().radians())
            - nu0.value().radians()
            - nu3.value().radians();
    final double x = nu2.value().radians() * scale;
    return ImmutableTorsionAngleValue.of(this, ImmutableAngle.of(FastMath.atan2(y, x)));
  }
}
