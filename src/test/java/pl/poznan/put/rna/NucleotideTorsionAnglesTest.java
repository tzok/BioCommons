package pl.poznan.put.rna;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.torsion.AtomBasedTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

public class NucleotideTorsionAnglesTest {
  // @formatter:off
  private static final String ATOM_P =
      "ATOM      2  P     G A   1      50.626  49.730  50.573  1.00100.19           P  ";
  private static final String ATOM_O5p =
      "ATOM      5  O5'   G A   1      50.161  49.136  52.023  1.00 99.82           O  ";
  private static final String ATOM_C5p =
      "ATOM      6  C5'   G A   1      50.216  49.948  53.210  1.00 98.63           C  ";
  private static final String ATOM_C4p =
      "ATOM      7  C4'   G A   1      50.968  49.231  54.309  1.00 97.84           C  ";
  private static final Vector<Euclidean3D> V1 = new Vector3D(-0.465, -0.594, 1.450);
  private static final Vector<Euclidean3D> V2 = new Vector3D(0.055, 0.812, 1.187);
  private static final Vector<Euclidean3D> V3 = new Vector3D(0.752, -0.717, 1.099);
  private static final Vector<Euclidean3D> TMP1 = new Vector3D(-1.882478, 0.631705, -0.34491);
  private static final Vector<Euclidean3D> TMP2 = new Vector3D(1.743467, 0.832179, -0.650059);
  private static final Vector<Euclidean3D> TMP3 =
      new Vector3D(-0.6692346816, -0.8548933352, 2.0868608351);
  private static final double RADIANS = -2.2349490129;
  private static final double DEGREES = -128.0531458665;
  // @formatter:on

  private static boolean isBelowEpsilon(final double value) {
    return FastMath.abs(value) < 1.0e-6;
  }

  @Test
  public final void testAngle() {
    final PdbAtomLine a1 = PdbAtomLine.parse(NucleotideTorsionAnglesTest.ATOM_P);
    final PdbAtomLine a2 = PdbAtomLine.parse(NucleotideTorsionAnglesTest.ATOM_O5p);
    final PdbAtomLine a3 = PdbAtomLine.parse(NucleotideTorsionAnglesTest.ATOM_C5p);
    final PdbAtomLine a4 = PdbAtomLine.parse(NucleotideTorsionAnglesTest.ATOM_C4p);

    final Vector3D v1 = a2.toVector3D().subtract(a1.toVector3D());
    final Vector3D v2 = a3.toVector3D().subtract(a2.toVector3D());
    final Vector3D v3 = a4.toVector3D().subtract(a3.toVector3D());

    assertThat(
        NucleotideTorsionAnglesTest.isBelowEpsilon(v1.distance(NucleotideTorsionAnglesTest.V1)),
        is(true));
    assertThat(
        NucleotideTorsionAnglesTest.isBelowEpsilon(v2.distance(NucleotideTorsionAnglesTest.V2)),
        is(true));
    assertThat(
        NucleotideTorsionAnglesTest.isBelowEpsilon(v3.distance(NucleotideTorsionAnglesTest.V3)),
        is(true));

    final Vector3D tmp1 = v1.crossProduct(v2);
    final Vector3D tmp2 = v2.crossProduct(v3);
    final Vector3D tmp3 = v1.scalarMultiply(v2.getNorm());

    assertThat(
        NucleotideTorsionAnglesTest.isBelowEpsilon(tmp1.distance(NucleotideTorsionAnglesTest.TMP1)),
        is(true));
    assertThat(
        NucleotideTorsionAnglesTest.isBelowEpsilon(tmp2.distance(NucleotideTorsionAnglesTest.TMP2)),
        is(true));
    assertThat(
        NucleotideTorsionAnglesTest.isBelowEpsilon(tmp3.distance(NucleotideTorsionAnglesTest.TMP3)),
        is(true));

    final double torsionAngleRadians = FastMath.atan2(tmp3.dotProduct(tmp2), tmp1.dotProduct(tmp2));
    assertThat(
        NucleotideTorsionAnglesTest.isBelowEpsilon(
            torsionAngleRadians - NucleotideTorsionAnglesTest.RADIANS),
        is(true));

    final double torsionAngleDegrees = Math.toDegrees(torsionAngleRadians);
    assertThat(
        NucleotideTorsionAnglesTest.isBelowEpsilon(
            torsionAngleDegrees - NucleotideTorsionAnglesTest.DEGREES),
        is(true));

    final AtomBasedTorsionAngleType beta =
        (AtomBasedTorsionAngleType) NucleotideTorsionAngle.BETA.angleTypes().get(0);
    final TorsionAngleValue angleValue = beta.calculate(a1, a2, a3, a4);

    assertThat(angleValue.value().isValid(), is(true));
    assertThat(
        NucleotideTorsionAnglesTest.isBelowEpsilon(
            angleValue.value().radians() - NucleotideTorsionAnglesTest.RADIANS),
        is(true));
    assertThat(
        NucleotideTorsionAnglesTest.isBelowEpsilon(
            angleValue.value().degrees() - NucleotideTorsionAnglesTest.DEGREES),
        is(true));
  }
}
