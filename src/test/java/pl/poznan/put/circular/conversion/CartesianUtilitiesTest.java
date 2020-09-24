package pl.poznan.put.circular.conversion;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
import org.junit.Test;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CartesianUtilitiesTest {
  private static final double[] ATOM_OP3 = {50.193, 51.190, 50.534};
  private static final double[] ATOM_P = {50.626, 49.730, 50.573};
  private static final double[] ATOM_O5P = {50.161, 49.136, 52.023};
  private static final double[] ATOM_C5P = {50.216, 49.948, 53.210};
  private static final double[] ATOM_C4P = {50.968, 49.231, 54.309};
  private static final double[] ATOM_C3P = {52.454, 49.030, 54.074};

  @Test
  public final void testThreeAtomsRebuild() {
    final Vector3D atomOP3 =
        new Vector3D(
            CartesianUtilitiesTest.ATOM_OP3[0],
            CartesianUtilitiesTest.ATOM_OP3[1],
            CartesianUtilitiesTest.ATOM_OP3[2]);
    final Vector3D atomP =
        new Vector3D(
            CartesianUtilitiesTest.ATOM_P[0],
            CartesianUtilitiesTest.ATOM_P[1],
            CartesianUtilitiesTest.ATOM_P[2]);
    final Vector3D atomO5p =
        new Vector3D(
            CartesianUtilitiesTest.ATOM_O5P[0],
            CartesianUtilitiesTest.ATOM_O5P[1],
            CartesianUtilitiesTest.ATOM_O5P[2]);

    final Vector3D expectedAtomC5p =
        new Vector3D(
            CartesianUtilitiesTest.ATOM_C5P[0],
            CartesianUtilitiesTest.ATOM_C5P[1],
            CartesianUtilitiesTest.ATOM_C5P[2]);
    final Vector3D actualAtomC5p =
        CartesianUtilities.nextPlacement(
            atomOP3,
            atomP,
            atomO5p,
            expectedAtomC5p.distance(atomO5p),
            Angle.betweenPoints(atomP, atomO5p, expectedAtomC5p),
            Angle.torsionAngle(atomOP3, atomP, atomO5p, expectedAtomC5p));
    assertThat(actualAtomC5p, is(expectedAtomC5p));

    final Vector3D expectedAtomC4p =
        new Vector3D(
            CartesianUtilitiesTest.ATOM_C4P[0],
            CartesianUtilitiesTest.ATOM_C4P[1],
            CartesianUtilitiesTest.ATOM_C4P[2]);
    final Vector3D actualAtomC4p =
        CartesianUtilities.nextPlacement(
            atomP,
            atomO5p,
            actualAtomC5p,
            expectedAtomC4p.distance(actualAtomC5p),
            Angle.betweenPoints(atomO5p, actualAtomC5p, expectedAtomC4p),
            Angle.torsionAngle(atomP, atomO5p, actualAtomC5p, expectedAtomC4p));
    assertThat(actualAtomC4p, is(expectedAtomC4p));

    final Vector3D expectedAtomC3p =
        new Vector3D(
            CartesianUtilitiesTest.ATOM_C3P[0],
            CartesianUtilitiesTest.ATOM_C3P[1],
            CartesianUtilitiesTest.ATOM_C3P[2]);
    final Vector3D actualAtomC3p =
        CartesianUtilities.nextPlacement(
            atomO5p,
            actualAtomC5p,
            actualAtomC4p,
            expectedAtomC3p.distance(actualAtomC4p),
            Angle.betweenPoints(actualAtomC5p, actualAtomC4p, expectedAtomC3p),
            Angle.torsionAngle(atomO5p, actualAtomC5p, actualAtomC4p, expectedAtomC3p));
    assertThat(actualAtomC3p, is(expectedAtomC3p));
  }

  @Test
  public final void nextPlacement() {
    final Vector3D coordA = new Vector3D(50.626, 49.730, 50.573); // P
    final Vector3D coordB = new Vector3D(50.161, 49.136, 52.023); // O5'
    final Vector3D coordC = new Vector3D(50.216, 49.948, 53.210); // C5'
    final double lengthCD = 1.512413303300389;
    final ImmutableAngle angleBCD = ImmutableAngle.of(FastMath.toRadians(110.54));
    final ImmutableAngle torsionABCD = ImmutableAngle.of(FastMath.toRadians(-128.05));

    final Vector3D actualCoordD =
        CartesianUtilities.nextPlacement(coordA, coordB, coordC, lengthCD, angleBCD, torsionABCD);
    final Vector3D expectedCoordD = new Vector3D(50.968, 49.231, 54.309); // C4'

    assertThat(
        Precision.equals(
            Vector3D.distance(coordC, actualCoordD),
            Vector3D.distance(coordC, expectedCoordD),
            1.0e-3),
        is(true));

    assertThat(
        Angle.betweenPoints(coordB, coordC, actualCoordD),
        is(Angle.betweenPoints(coordB, coordC, expectedCoordD)));

    assertThat(
        Angle.torsionAngle(coordA, coordB, coordC, actualCoordD),
        is(Angle.torsionAngle(coordA, coordB, coordC, expectedCoordD)));

    assertThat(Precision.equals(actualCoordD.getX(), expectedCoordD.getX(), 1.0e-3), is(true));
    assertThat(Precision.equals(actualCoordD.getY(), expectedCoordD.getY(), 1.0e-3), is(true));
    assertThat(Precision.equals(actualCoordD.getZ(), expectedCoordD.getZ(), 1.0e-3), is(true));
  }
}
