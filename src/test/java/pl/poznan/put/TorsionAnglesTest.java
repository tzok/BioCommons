package pl.poznan.put;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.rna.torsion.Beta;
import pl.poznan.put.torsion.TorsionAngleValue;
import pl.poznan.put.torsion.TorsionAnglesHelper;

public class TorsionAnglesTest {
  private static final double EPSILON_E6 = 1.0e-6;

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
  private static final Vector<Euclidean3D> TMP3 = new Vector3D(-0.6692346816, -0.8548933352, 2.0868608351);

  private static final double RADIANS = -2.2349490129;
  private static final double DEGREES = -128.0531458665;
  // @formatter:on

  @Test
  public final void testAngle() {
    final PdbAtomLine a1 = PdbAtomLine.parse(TorsionAnglesTest.ATOM_P);
    final PdbAtomLine a2 = PdbAtomLine.parse(TorsionAnglesTest.ATOM_O5p);
    final PdbAtomLine a3 = PdbAtomLine.parse(TorsionAnglesTest.ATOM_C5p);
    final PdbAtomLine a4 = PdbAtomLine.parse(TorsionAnglesTest.ATOM_C4p);

    final Vector3D v1 = TorsionAnglesHelper.atomDistance(a1, a2);
    final Vector3D v2 = TorsionAnglesHelper.atomDistance(a2, a3);
    final Vector3D v3 = TorsionAnglesHelper.atomDistance(a3, a4);

    assertEquals(0.0, v1.distance(TorsionAnglesTest.V1), TorsionAnglesTest.EPSILON_E6);
    assertEquals(0.0, v2.distance(TorsionAnglesTest.V2), TorsionAnglesTest.EPSILON_E6);
    assertEquals(0.0, v3.distance(TorsionAnglesTest.V3), TorsionAnglesTest.EPSILON_E6);

    final Vector3D tmp1 = v1.crossProduct(v2);
    final Vector3D tmp2 = v2.crossProduct(v3);
    final Vector3D tmp3 = v1.scalarMultiply(v2.getNorm());

    assertEquals(0.0, tmp1.distance(TorsionAnglesTest.TMP1), TorsionAnglesTest.EPSILON_E6);
    assertEquals(0.0, tmp2.distance(TorsionAnglesTest.TMP2), TorsionAnglesTest.EPSILON_E6);
    assertEquals(0.0, tmp3.distance(TorsionAnglesTest.TMP3), TorsionAnglesTest.EPSILON_E6);

    final double torsionAngleRadians = FastMath.atan2(tmp3.dotProduct(tmp2), tmp1.dotProduct(tmp2));
    assertEquals(TorsionAnglesTest.RADIANS, torsionAngleRadians, TorsionAnglesTest.EPSILON_E6);

    final double torsionAngleDegrees = Math.toDegrees(torsionAngleRadians);
    assertEquals(TorsionAnglesTest.DEGREES, torsionAngleDegrees, TorsionAnglesTest.EPSILON_E6);

    final Beta beta = Beta.getInstance();
    final TorsionAngleValue angleValue = beta.calculate(a1, a2, a3, a4);

    Assert.assertThat(angleValue.getValue().isValid(), is(true));
    assertEquals(
            TorsionAnglesTest.RADIANS,
            angleValue.getValue().getRadians(),
            TorsionAnglesTest.EPSILON_E6);
    assertEquals(
            TorsionAnglesTest.DEGREES,
            angleValue.getValue().getDegrees(),
            TorsionAnglesTest.EPSILON_E6);
  }
}
