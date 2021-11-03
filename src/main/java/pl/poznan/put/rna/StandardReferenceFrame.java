package pl.poznan.put.rna;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.biojava.nbio.structure.geometry.CalcPoint;
import org.biojava.nbio.structure.geometry.SuperPositions;
import org.biojava.nbio.structure.jama.Matrix;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.PdbResidue;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Value.Immutable
public abstract class StandardReferenceFrame {
  public static StandardReferenceFrame ofResidue(final PdbResidue residue) {
    final Nucleobase nucleobase = ((Nucleotide) residue.residueInformationProvider()).nucleobase();
    final Map<AtomName, PdbAtomLine> frame = nucleobase.standardReferenceFrame();

    final List<AtomName> order = frame.keySet().stream().sorted().collect(Collectors.toList());
    final Point3d[] fixed =
        frame.values().stream()
            .sorted(Comparator.comparingInt(t -> order.indexOf(t.detectAtomName())))
            .map(atom -> new Point3d(atom.x(), atom.y(), atom.z()))
            .toArray(Point3d[]::new);
    final Point3d[] moved =
        residue.atoms().stream()
            .filter(atom -> frame.containsKey(atom.detectAtomName()))
            .sorted(Comparator.comparingInt(t -> order.indexOf(t.detectAtomName())))
            .map(atom -> new Point3d(atom.x(), atom.y(), atom.z()))
            .toArray(Point3d[]::new);
    final Point3d centroidFixed = CalcPoint.centroid(fixed);
    final Point3d centroidMoved = CalcPoint.centroid(moved);
    final Matrix4d superposition = SuperPositions.superpose(fixed, moved);

    final Matrix fixedMatrix =
        new Matrix(
            new double[][] {new double[] {centroidFixed.x, centroidFixed.y, centroidFixed.z}});
    final Matrix movedMatrix =
        new Matrix(
            new double[][] {new double[] {centroidMoved.x, centroidMoved.y, centroidMoved.z}});
    final Matrix rotationMatrix =
        new Matrix(
            new double[][] {
              new double[] {superposition.m00, superposition.m01, superposition.m02},
              new double[] {superposition.m10, superposition.m11, superposition.m12},
              new double[] {superposition.m20, superposition.m21, superposition.m22}
            });
    final Vector3D origin =
        new Vector3D(movedMatrix.minus(fixedMatrix.times(rotationMatrix)).getRowPackedCopy());
    final Vector3D x = new Vector3D(superposition.m00, superposition.m01, superposition.m02);
    final Vector3D y = new Vector3D(superposition.m10, superposition.m11, superposition.m12);
    final Vector3D z = new Vector3D(superposition.m20, superposition.m21, superposition.m22);
    final PdbAtomLine longAxisAtom =
        nucleobase instanceof Purine
            ? residue.findAtom(AtomName.C8)
            : residue.findAtom(AtomName.C6);
    return ImmutableStandardReferenceFrame.of(origin, x, y, z)
        .withLongAxisAtom(Optional.of(longAxisAtom));
  }

  public static StandardReferenceFrame ofBasePair(
      final StandardReferenceFrame primary, final StandardReferenceFrame secondary) {
    final String message = "Cannot build reference frame without knowing the long axis atom";
    final PdbAtomLine primaryAtom =
        primary.longAxisAtom().orElseThrow(() -> new IllegalArgumentException(message));
    final PdbAtomLine secondaryAtom =
        secondary.longAxisAtom().orElseThrow(() -> new IllegalArgumentException(message));

    final Vector3D z =
        new Vector3D(
                IntStream.range(0, 3)
                    .mapToDouble(i -> primary.z().toArray()[i] + secondary.z().toArray()[i])
                    .map(v -> v / 2.0)
                    .toArray())
            .normalize();
    final Vector3D y =
        secondaryAtom
            .toVector3D()
            .subtract(primaryAtom.toVector3D())
            .orthogonal()
            .crossProduct(z)
            .normalize();
    final Vector3D x = y.crossProduct(z).normalize();

    final Vector3D origin = primary.origin().subtract(secondary.origin());
    return ImmutableStandardReferenceFrame.of(origin, x, y, z);
  }

  @Value.Parameter(order = 1)
  public abstract Vector3D origin();

  @Value.Parameter(order = 2)
  public abstract Vector3D x();

  @Value.Parameter(order = 3)
  public abstract Vector3D y();

  @Value.Parameter(order = 4)
  public abstract Vector3D z();

  @Value.Default
  public Optional<PdbAtomLine> longAxisAtom() {
    return Optional.empty();
  }
}
