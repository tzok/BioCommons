package pl.poznan.put.circular.conversion;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Test;
import pl.poznan.put.circular.Angle;

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
}
