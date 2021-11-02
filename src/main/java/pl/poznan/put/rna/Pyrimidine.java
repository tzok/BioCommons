package pl.poznan.put.rna;

import org.apache.commons.lang3.tuple.Pair;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.notation.NucleobaseEdge;
import pl.poznan.put.torsion.TorsionAngleType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** A pyrimidine (cytosine, uracil or thymine). */
public interface Pyrimidine extends Nucleobase {
  @Override
  default Pair<AtomName, AtomName> edgeVectorAtoms(final NucleobaseEdge edge) {
    switch (edge) {
      case WATSON_CRICK:
        return Pair.of(AtomName.C6, AtomName.N3);
      case HOOGSTEEN:
        return Pair.of(AtomName.C2, AtomName.C5);
      case SUGAR:
        return Pair.of(AtomName.C4, AtomName.C2);
      case UNKNOWN:
      default:
        throw new IllegalArgumentException("Invalid nucleobase edge: " + edge);
    }
  }

  @Override
  default List<TorsionAngleType> torsionAngleTypes() {
    return Stream.of(
            NucleotideTorsionAngle.ALPHA.angleTypes().get(0),
            NucleotideTorsionAngle.BETA.angleTypes().get(0),
            NucleotideTorsionAngle.GAMMA.angleTypes().get(0),
            NucleotideTorsionAngle.DELTA.angleTypes().get(0),
            NucleotideTorsionAngle.EPSILON.angleTypes().get(0),
            NucleotideTorsionAngle.ZETA.angleTypes().get(0),
            NucleotideTorsionAngle.NU0.angleTypes().get(0),
            NucleotideTorsionAngle.NU1.angleTypes().get(0),
            NucleotideTorsionAngle.NU2.angleTypes().get(0),
            NucleotideTorsionAngle.NU3.angleTypes().get(0),
            NucleotideTorsionAngle.NU4.angleTypes().get(0),
            NucleotideTorsionAngle.ETA.angleTypes().get(0),
            NucleotideTorsionAngle.THETA.angleTypes().get(0),
            NucleotideTorsionAngle.ETA_PRIM.angleTypes().get(0),
            NucleotideTorsionAngle.THETA_PRIM.angleTypes().get(0),
            NucleotideTorsionAngle.PSEUDOPHASE_PUCKER.angleTypes().get(0),
            Chi.PYRIMIDINE.angleType())
        .collect(Collectors.toList());
  }
}
